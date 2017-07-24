package liuxun.test.utils;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;

import liuxun.test.bean.Article;

/**
 * Bean类和索引库Document之间的转化
 * 
 * @author liuxun
 *
 */

public class ArticleDocumentUtils {
	/**
	 * 将文章转化为文档对象
	 * @param article
	 * @return
	 */
	public static Document articleToDocument(Article article) {
		Document document = new Document();
		IntField idField = new IntField("id", article.getId(), Store.YES);
		// StringField 对应的值不分词，TextField分词
		TextField titleField = new TextField("title", article.getTitle(), Store.YES);
		TextField contentField = new TextField("content", article.getContent(), Store.YES);
		
		//修改这个值的权重值 默认是1f
		contentField.setBoost(3f);
		
		StringField authorField = new StringField("author", article.getAuthor(), Store.YES);
		StringField urlField = new StringField("url", article.getUrl(), Store.YES);
		document.add(idField);
        document.add(titleField);
        document.add(contentField);
        document.add(authorField);
        document.add(urlField);
		return document;
	}
	
	/**
	 * 将文档对象转为Article对象
	 * @param document
	 * @return
	 */
	public static Article documentToArticle(Document document){
		Article article=new Article();
		article.setId(Integer.parseInt(document.get("id")));
		article.setAuthor(document.get("author"));
		article.setContent(document.get("content"));
		article.setUrl(document.get("url"));
		article.setTitle(document.get("title"));
		return article;
	}
}
