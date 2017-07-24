package liuxun.test.utils;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * lucene工具类
 * 
 * @author liuxun
 *
 */
public class LuceneUtils {
	private static IndexWriter indexWriter = null;
	private static IndexSearcher indexSearcher = null;

	// 索引存放目录
	private static Directory directory = null;
	// 索引写入器配置
	private static IndexWriterConfig indexWriterConfig = null;
	// 当前使用Lucene的版本
	private static Version version = null;
	// 分词器
	private static Analyzer analyzer = null;

	static {
		try {
			// 按照标准开发应该将地址配置在properties资源文件中然后进行读取
			directory = FSDirectory.open(new File("/Users/liuxun/Desktop/indexes"));
			version = Version.LUCENE_44;
			analyzer = new StandardAnalyzer(version);
			indexWriterConfig = new IndexWriterConfig(version, analyzer);
			// 初始化IndexWriter
			indexWriter = new IndexWriter(directory, indexWriterConfig);
			System.out.println("==>> 已经初始化IndexWriter <<==");
			
			//指定在JVM退出前要执行的代码
			Runtime.getRuntime().addShutdownHook(new Thread(){
				public void run(){
					try {
						closeIndexWriter();
						closeIndexSearcher();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return 返回用于操作索引的对象
	 * @throws IOException
	 */
	public static IndexWriter getIndexWriter() throws IOException {
		// 全局唯一 在运行过程中不关闭，在JVM退出时才进行关闭操作
		
		return indexWriter;
	}

	/**
	 * 
	 * @return 返回搜索索引的对象
	 * @throws IOException
	 */
	public static IndexSearcher getIndexSearcher() throws IOException {
		if (indexSearcher == null) {
			IndexReader indexReader = DirectoryReader.open(directory);
			indexSearcher = new IndexSearcher(indexReader);
		}
		return indexSearcher;
	}

	/**
	 * 关闭IndexWriter
	 * @throws IOException 
	 */
	private static void closeIndexWriter() throws IOException {
		if (indexWriter != null) {
				indexWriter.close();
				System.out.println("==>> 已经关闭IndexWriter <<==");
		}
	}

	/**
	 * 关闭IndexSearcher
	 * @throws IOException 
	 */
	private static void closeIndexSearcher() throws IOException {
		if (indexSearcher != null) {
				indexSearcher.getIndexReader().close();
				System.out.println("-->> 关闭了IndexSearcher <<--");
		}
	}
	/**
	 * 通知索引库更改
	 * 当索引库中的值发生改变的时候，需要关闭IndexSearcher对象(实质是关闭IndexReader)
	 * @throws IOException 
	 */
	public static void indexChanged() throws IOException{
		if (indexSearcher!=null) {
			closeIndexSearcher();
			indexSearcher = null;
		}
	}

	/**
	 * 返回Lucene当前的版本
	 * 
	 * @return
	 */
	public static Version getVersion() {
		return version;
	}

	/**
	 * 返回Lucene当前使用的分词器
	 * 
	 * @return
	 */
	public static Analyzer getAnalyzer() {
		return analyzer;
	}

}
