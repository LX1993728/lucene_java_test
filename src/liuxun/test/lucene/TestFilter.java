package liuxun.test.lucene;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TopDocs;

import liuxun.test.utils.LuceneUtils;

/**
 * 测试过滤
 * 对查询结果进行过滤，以获取更小范围的结果
 * @author liuxun
 *
 */
public class TestFilter {
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
		
		//过滤器
		//NumericRangeFilter.newIntRange(field, min, max, minInclusive, maxInclusive)
		//参数含义：
		// 1.field  需要根据那个字段进行过滤
		// 2.min    字段对应范围的最小值
		// 3.max    字段对应范围的最大值
		// 4.minInclusive 是否包含最小值
		// 5.maxInclusive 是否包含最大值
		Filter filter = NumericRangeFilter.newIntRange("id", 1, 3, true, true);
		
		TopDocs topDocs=indexSearcher.search(query,filter, 10,sort);
		ScoreDoc scoreDocs[]=topDocs.scoreDocs;
		
		for(ScoreDoc scoreDoc :scoreDocs){
			//根据id 去击中一个文档
			Document document=indexSearcher.doc(scoreDoc.doc);
			System.out.println("id==="+document.get("id"));
			System.out.println("title==="+document.get("title"));
			System.out.println("content==="+document.get("content"));
			System.out.println("url==="+document.get("url"));
			System.out.println("author==="+document.get("author"));
			
		}
	}
}
