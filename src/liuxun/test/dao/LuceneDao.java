package liuxun.test.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import liuxun.test.bean.Article;
import liuxun.test.bean.SearchResult;
import liuxun.test.utils.ArticleDocumentUtils;
import liuxun.test.utils.LuceneUtils;

/**
 * 使用LuceneAPI来操作索引库
 * 
 * @author liuxun
 *
 */
public class LuceneDao {

	/**
	 * 创建索引
	 * @param article
	 */
	public void addIndex(Article article) {
		// 将Article转为Document
		Document doc = ArticleDocumentUtils.articleToDocument(article);
		// 建立索引
		try {
			LuceneUtils.getIndexWriter().addDocument(doc);
			// 提交更改
			LuceneUtils.getIndexWriter().commit();
			LuceneUtils.indexChanged();
		} catch (IOException e) {
			try {
				LuceneUtils.getIndexWriter().rollback();
			} catch (IOException e1) {
				throw new RuntimeException("创建索引失败");
			}
		}
	}

	/**
	 * 删除索引——根据非IntField类型的
	 */
	public void delIndexByTerm(String fieldName,String fieldValue){
		Term term = new Term(fieldName,fieldValue);
		try {
			// 删除所有含有指定term的所有Document
			LuceneUtils.getIndexWriter().deleteDocuments(term);
			// 提交更改
			LuceneUtils.getIndexWriter().commit();
			LuceneUtils.indexChanged();
		} catch (IOException e) {
			try {
				LuceneUtils.getIndexWriter().rollback();
			} catch (IOException e1) {
				throw new RuntimeException("删除索引失败");
			}
		}
	}
	/**
	 * 删除索引——根据IntField类型删除
	 */
	public void delIndexByNumeric(Integer id){
		Query query = NumericRangeQuery.newIntRange("id", id-1, id+1, false, false);
		try {
			// 删除所有含有指定term的所有Document
			LuceneUtils.getIndexWriter().deleteDocuments(query);;
			// 提交更改
			LuceneUtils.getIndexWriter().commit();
			LuceneUtils.indexChanged();
		} catch (IOException e) {
			try {
				LuceneUtils.getIndexWriter().rollback();
			} catch (IOException e1) {
				throw new RuntimeException("删除索引失败");
			}
		}
	}

	/**
	 * 更新索引——根据非IntField类型的字段属性更新
	 * @param article
	 */
	public void updateIndex(String fieldName,String fieldString,Article article) {
		Term term = new Term(fieldName,fieldString);
		try {
			Document doc = ArticleDocumentUtils.articleToDocument(article);
			// 更新索引，就是先查找删除再创建，如果原有的不存在 直接创建新的索引
			LuceneUtils.getIndexWriter().updateDocument(term, doc);
			// 提交更改
			LuceneUtils.getIndexWriter().commit();
			LuceneUtils.indexChanged();
		} catch (IOException e) {
			try {
				LuceneUtils.getIndexWriter().rollback();
			} catch (IOException e1) {
				throw new RuntimeException("更新索引失败");
			}
		}
	}
	

	/**
	 * 查询索引
	 * 
	 * @param queryString 查询关键字
	 * @param firstResult 第一条数据的位置
	 * @param maxResult   数据记录的条数
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	// 注意使用：QueryParser时需要导入QueryParser所在的jar包 lucene-queryparser-4.4.0.jar
	public SearchResult<Article> findIndex(String queryString, int firstResult, int maxResult) throws IOException, ParseException {
		// 1.将查询字符串转为Query对象，单字段查询 例如默认只在"title"中搜索 
		// 1.1 先使用TermQuery 只对分词结果进行搜索，不会拼接各个分词部分
		// Query query = new TermQuery(new Term("title", queryString));
		
		// 1.2 使用QueryParser 搜索时会将分词结果 组合拼接 进行搜索
		// QueryParser queryParser = new QueryParser(LuceneUtils.getVersion(), "title", LuceneUtils.getAnalyzer());
        
		// 1.3 使用QueryParser的子类可以进行多字段查询 例如默认在title和content字段中搜索
		String[] fields = {"title","content"};
		QueryParser queryParser = new MultiFieldQueryParser(LuceneUtils.getVersion(), fields, LuceneUtils.getAnalyzer());
		Query query = queryParser.parse(queryString);
		
		// 2.执行查询得到中间结果
		IndexSearcher indexSearcher = LuceneUtils.getIndexSearcher();
		// 返回前n个结果 0 10 10    10 10 20   20 10 30
		TopDocs topDocs = indexSearcher.search(query, firstResult+maxResult);
		int count = topDocs.totalHits; //符合query的总结果数
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		// 处理结果
		List<Article> list = new ArrayList<Article>();
		int endIndex = Math.min(firstResult+maxResult, scoreDocs.length);
		
		for(int i=firstResult;i<endIndex;i++){
			//根据编号取出真正的Document数据
			Document doc = LuceneUtils.getIndexSearcher().doc(scoreDocs[i].doc);
			//将document转为Article
			Article article = ArticleDocumentUtils.documentToArticle(doc);
			list.add(article);
		}
		
		return new SearchResult<Article>(count, list);
	}
}
