package liuxun.test.lucene;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TopDocs;

import liuxun.test.utils.LuceneUtils;

/**
 * 测试排序（搜索结果按照字段值排序）
 * @author liuxun
 *
 */
public class TestSort {
	public static void main(String[] args) throws Exception {
		IndexSearcher indexSearcher=LuceneUtils.getIndexSearcher();
		String keywords="全文检索";
		
		String fields []={"content"};
		
		QueryParser queryParser=new MultiFieldQueryParser(LuceneUtils.getVersion(),fields,LuceneUtils.getAnalyzer());
		
		//条件
		Query query=queryParser.parse(keywords);
		
		//需要根据哪个字段进行排序
		// 第一个参数：fieldName 字段名称，根据哪个字段进行排序
		// 第二个参数：type  字段类型，排序的字段的类型
		// 第三个参数：reverse 是否逆序排序，true 降序  false 升序
		SortField sortField = new SortField("id", Type.INT, false);
		//设置排序的条件
		Sort sort = new Sort(sortField);
		TopDocs topDocs=indexSearcher.search(query, 10,sort);
		ScoreDoc scoreDocs[]=topDocs.scoreDocs;
		
		for(ScoreDoc scoreDoc :scoreDocs){
			//根据id 去击中一个文档
			Document document=indexSearcher.doc(scoreDoc.doc);
			System.out.println("id==="+document.get("id")+"得分===="+scoreDoc.score);
			System.out.println("title==="+document.get("title"));
			System.out.println("content==="+document.get("content"));
			System.out.println("url==="+document.get("url"));
			System.out.println("author==="+document.get("author"));
			
		}
	}
}
