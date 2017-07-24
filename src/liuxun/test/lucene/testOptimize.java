package liuxun.test.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LogByteSizeMergePolicy;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import liuxun.test.bean.Article;
import liuxun.test.utils.ArticleDocumentUtils;
import liuxun.test.utils.LuceneUtils;

/**
 * 优化索引库方案
 * 
 * @author liuxun
 *
 */
public class testOptimize {
	@Test
	// 优化的第一种方式:通过 IndexWriterConfig 优化设置mergePolicy（合并策略）
	public void testoptimize_1() throws IOException {
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_44,
				new StandardAnalyzer(Version.LUCENE_44));
		// 创建索引库优化对象...
		LogMergePolicy logMergePolicy = new LogByteSizeMergePolicy();
		// 值越小，搜索的时候越快,创建索引的时候越慢
		// 值越大，搜索的时候越慢，创建索引的时候越快。
		logMergePolicy.setMergeFactor(3);
		// 设置segment最大合并文档(Document)数
		// 值较小有利于追加索引的速度
		// 值较大,适合批量建立索引和更快的搜索
		logMergePolicy.setMaxMergeDocs(1000);
		indexWriterConfig.setMergePolicy(logMergePolicy);

		Directory directory = FSDirectory.open(new File("/Users/liuxun/Desktop/indexes"));
		IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
	}

	@Test
	// 测试RAMDirectory
	public void testoptimize_randdirectory() throws IOException {
		// 文件系统中的真是目录，可存储，但是只能通过IO读写，速度相对较慢
		Directory fsDir = FSDirectory.open(new File("/Users/liuxun/Desktop/indexes"));
		// 在内存中虚拟的目录，速度快，但不可存储，还对机器的内存大小有要求
		Directory randDir = new RAMDirectory();

		IndexWriter ramIndexWriter = new IndexWriter(randDir,
				new IndexWriterConfig(LuceneUtils.getVersion(), LuceneUtils.getAnalyzer()));
		Article article = new Article();
		article.setId(5);
		article.setTitle("Lucene是什么");
		article.setContent("Lucene是全文检索框架");
		Document document = ArticleDocumentUtils.articleToDocument(article);
		ramIndexWriter.close();
	}

	@Test
	//优化的第二种方式:使用虚拟目录操作索引库，然后合并到硬盘上
	public void testoptimize_2() throws IOException {
		//创建索引目录
		Directory fsDir = FSDirectory.open(new File("/Users/liuxun/Desktop/indexes"));
		//通过IOContext对象可将硬盘上的额索引读到内存中，涉及IO操作
		IOContext context = new IOContext();
		//将磁盘上的索引加载到内存中，以后每次操作索引的时候，直接操作内存中的索引即可，不用操作硬盘
		Directory ramDir = new RAMDirectory(fsDir, context);
		// 构造索引读取器
		IndexReader indexReader = DirectoryReader.open(ramDir);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		Query query = new TermQuery(new Term("title","罗贯中"));
		TopDocs topDocs = indexSearcher.search(query, 100);
		System.out.println(topDocs.totalHits);
	}

	@Test
	//索引文件越大，会影响检索的速度..  (减少索引文件的大小)
	//优化的第三种方式：排除停用词
	//1:排除停用词..
	public void testoptimize_3() {
		// ......
	}

	@Test
	//优化的第四种方式，将索引数据归类，分目录存放
	public void testoptimize_4() {

	}

}
