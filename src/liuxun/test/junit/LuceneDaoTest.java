package liuxun.test.junit;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;

import liuxun.test.bean.Article;
import liuxun.test.bean.SearchResult;
import liuxun.test.dao.LuceneDao;
import liuxun.test.utils.LuceneUtils;

public class LuceneDaoTest {
	private LuceneDao luceneDao = new LuceneDao();

	@Test
	public void testAddIndex_1() throws IOException {
		// 模拟一个已保存到索引库中的数据
		Article article = new Article();
		article.setId(5);
		article.setTitle("Lucene全文检索的说明");
		article.setContent("全文检索（Full-Text Retrieval）是指以文本作为全文检索对象，全文检索找出含有指定词汇的文本。");
		article.setAuthor("张三");
		article.setUrl("www.abc.com");
		luceneDao.addIndex(article);
	}
	
	@Test
	public void testAddIndex_25() throws IOException {
		// 模拟批量保存到索引库
		for(int i=6;i<=25;i++){
			Article article = new Article();
			article.setId(i);
			article.setTitle("Lucene全文检索的说明");
			article.setContent("全文检索（Full-Text Retrieval）是指以文本作为检索对象，找出含有指定词汇的文本。");
			article.setAuthor("张三");
			article.setUrl("www.abc.com");
			luceneDao.addIndex(article);
		}
	}

	@Test
	public void testDelIndex() throws IOException {
		Integer id = 1;
		luceneDao.delIndexByNumeric(id);
	}

	@Test
	//并不是真正意义上的更新
	public void testUpdateIndex() throws IOException {
		Article article = new Article();
		article.setAuthor("罗贯中");
		article.setTitle("三国演义是一部历史小说");
		article.setContent("三国演义中这样说道，潘金莲打开窗户...");
		article.setId(1);
		article.setUrl("www.xiaoshuo.com");
		luceneDao.updateIndex("title", "检", article);;
	}

	@Test
	public void testFindIndex() throws IOException, ParseException {
		SearchResult<Article> searchResult = luceneDao.findIndex("中检索", 0, 100);
		System.out.println("命中总数："+searchResult.getCount());
		for (Article article: searchResult.getList()) {
			System.out.println(">>>> id= "+article.getId());
			System.out.println(">>>> title= "+article.getTitle());
			System.out.println(">>>> content= "+article.getContent());
			System.out.println("---------------------------------");
		}
	}

}
