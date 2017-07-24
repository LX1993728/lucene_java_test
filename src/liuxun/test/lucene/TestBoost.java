package liuxun.test.lucene;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import liuxun.test.utils.LuceneUtils;

/**
 * 测试权值（搜索结果默认按照权重值从大到小排序）
 * @author liuxun
 *
 */
public class TestBoost {
	public static void main(String[] args) throws Exception {
		IndexSearcher indexSearcher=LuceneUtils.getIndexSearcher();
		String keywords="全文检索";
		
		String fields []={"content"};
		
		QueryParser queryParser=new MultiFieldQueryParser(LuceneUtils.getVersion(),fields,LuceneUtils.getAnalyzer());
		
		//条件
		Query query=queryParser.parse(keywords);
		
		TopDocs topDocs=indexSearcher.search(query, 50);
		ScoreDoc scoreDocs[]=topDocs.scoreDocs;
		
		for(ScoreDoc scoreDoc :scoreDocs){
			//根据id 去击中一个文档呢..
			Document document=indexSearcher.doc(scoreDoc.doc);
			//每个文档都有一个得分,这个得分是float 类型，他是lucene 自己内部算出来，VSM
			System.out.println("id==="+document.get("id")+"得分===="+scoreDoc.score);
			System.out.println("title==="+document.get("title"));
			System.out.println("content==="+document.get("content"));
			System.out.println("url==="+document.get("url"));
			System.out.println("author==="+document.get("author"));
			
		}
	}
}
