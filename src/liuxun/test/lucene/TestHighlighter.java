package liuxun.test.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

import liuxun.test.bean.Article;
import liuxun.test.utils.LuceneUtils;

/**
 * 测试高亮
 * 
 * 使用高亮的时候我们需要导入两个jar lucene-highlighter-4.4.0.jar lucene-memory-4.4.0.jar
 * 
 * @author liuxun 对查询出来的结果当中包含的搜索关键字进行高亮
 */
public class TestHighlighter {
	public static void main(String[] args) throws Exception {
		IndexSearcher indexSearcher = LuceneUtils.getIndexSearcher();
		String keywords = "检索";
		String[] fields = { "content" };
		// 构造QueryParser，解析用户输入的关键字
		QueryParser queryParser = new MultiFieldQueryParser(LuceneUtils.getVersion(), fields,
				LuceneUtils.getAnalyzer());
		Query query = queryParser.parse(keywords);
		TopDocs topDocs = indexSearcher.search(query, 1);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;

		// 高亮显示的格式
		Formatter formatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");

		// 与query 查询条件进行关联，因为query 包含了搜索的关键字
		// 只有知道了搜索的关键字，高亮显示的格式，才能把一段文本进行高亮
		Scorer scorer = new QueryScorer(query);

		// 创建一个高亮器，使用Lucene自带的高亮器进行高亮
		Highlighter highlighter = new Highlighter(formatter, scorer);
		Article article = null;
		List<Article> articles = new ArrayList<Article>();
		for (ScoreDoc scoreDoc : scoreDocs) {
			article = new Article();
			Document document = indexSearcher.doc(scoreDoc.doc);
			String title = document.get("title");
			String content = document.get("content");
			System.out.println("id==" + document.get("id"));
			System.out.println("title===" + title);
			System.out.println("content===" + content);
			System.out.println("没有高亮之前的结果....----------------------------------------------------");

			if (content != null) {
				// 返回高亮之后的文本
				String highcontent = highlighter.getBestFragment(LuceneUtils.getAnalyzer(), "content", content);
				System.out.println("高亮过后的highcontent=" + highcontent);
				if (highcontent != null) {
					article.setContent(highcontent);
				} else {
					article.setContent(content);
				}
			}

			if (title != null) {
				String hightitle = highlighter.getBestFragment(LuceneUtils.getAnalyzer(), "title", title);
				//如果我们要对一段文本进行高亮，假设这段文本中不包含关键字，对这段文本高亮后返回bull
				System.out.println("高亮过后的hightitle="+hightitle);
				//不能将null返回客户端，所以我们要进行判断，如果为null，就返回没有高亮之前的文本
				if (hightitle !=null) {
					article.setTitle(hightitle);
				}else{
					article.setTitle(title);
				}
			}
		}

	}
}
