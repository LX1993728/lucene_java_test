package liuxun.test.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

/**
 * 测试Lucene-入门程序
 * 
 * @author liuxun
 *
 */
public class TestLucene {
	/**
	 * 通过Lucene提供的API对数据建立索引。indexwriter
	 * @throws IOException
	 */
	@Test
	public void testAdd() throws IOException {
		// 索引在硬盘上面的存储位置
		Directory directory = FSDirectory.open(new File("/Users/liuxun/Desktop/indexes"));
		// lucene 当前使用的版本
		Version matchVersion = Version.LUCENE_44;
		// 分词器, 作用是将一段文本分词
		// analyzer 是一个抽象类，具体的切分词规则由子类实现
		Analyzer analyzer = new StandardAnalyzer(matchVersion);

		IndexWriterConfig config = new IndexWriterConfig(matchVersion, analyzer);
		// 构造索引写入的对象
		IndexWriter indexWriter = new IndexWriter(directory, config);

		// 往索引库里写数据
		// 索引库里的数据都是document，一个document相当于一条记录
		// 这个document里面的数据相当于是索引结构
		Document document = new Document();
		IndexableField intField = new IntField("id", 2, Store.YES);
		IndexableField stringField = new StringField("title", "李小龙简介", Store.YES);
		IndexableField textField = new TextField("content", "中国一顶级武术大师", Store.YES);

		// document.add(field) 包含了两个过程：存储数据和创建索引
		// 使用分词器分词后每个分词部分都是一个索引，如果索引在索引库中不存在便会创建索引，每个索引都有一个存储指向document的DocId数组
		// 指向document.add(field)后会根据分词器规则将filed的内容切分成多个索引，并将当前document的DocId添加到切分后每个索引中的DocId内
		// 而持久化记录字段值到Document是与Store.YES有关，如果为NO 该字段的内容就不会持久化到document文件中
		document.add(intField);
		document.add(stringField);
		document.add(textField);
		// 索引库里面接收的都是document对象
		indexWriter.addDocument(document);
		indexWriter.close();
	}

	/**
	 * 对建立的索引进行搜索... 通过indexSearcher去搜索
	 * @throws IOException
	 */
	@Test
	public void testSearcher() throws IOException {
		// 索引在硬盘上面的存储位置
		Directory directory = FSDirectory.open(new File("/Users/liuxun/Desktop/indexes"));
		//将索引目录里的索引读取到IndexReader中
		IndexReader indexReader = DirectoryReader.open(directory);
		//构造搜索索引的对象（索引搜索器）
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		
		//Query 是一个查询条件对象，它是一个抽象类，不同的查询条件就构造不同的子类
		//Term(fieldName,value) 会将value与当前字段值的分词结果(多个索引)进行匹配 匹配到则命中
		//Query query = new TermQuery(new Term("title", "刘勋简介"));
		Query query = new TermQuery(new Term("content", "一"));
		
		//检索符合query条件的前N条记录
		TopDocs topDocs = indexSearcher.search(query, 1);
		//返回总记录数(命中数)
		System.out.println(topDocs.totalHits);
		
		//存放的是document的id
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			//返回的就是document id  注意不是设置的字段由Lucene自动生成维护的
			int docID= scoreDoc.doc;
			//还需要根据document的id 检索到对应的document
			Document document = indexSearcher.doc(docID);
			
			System.out.println("id== "+document.get("id"));
			System.out.println("title== "+document.get("title"));
			System.out.println("content== "+document.get("content"));
		}
		
	}
}
