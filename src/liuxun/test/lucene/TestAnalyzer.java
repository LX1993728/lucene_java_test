package liuxun.test.lucene;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class TestAnalyzer {

	/**
	 * 使用指定的分词器，对指定的文本进行分词
	 * @param analyzer
	 * @param text
	 * @throws IOException 
	 */
	public  void testAnalyzer(Analyzer analyzer,String text) throws IOException{
		System.out.println("当前使用的分词器："+analyzer.getClass().getSimpleName());
		//切分关键字，切分后的关键字都存放在tokenStream里面
		TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(text));
		
		tokenStream.addAttribute(CharTermAttribute.class);
		tokenStream.reset();
		while(tokenStream.incrementToken()){
			 CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
	          System.out.print(new String(charTermAttribute.toString())+"  ");
		}
		System.out.println();
		tokenStream.close();
	}
	
	@Test
	public  void test() throws IOException{
		//String text = "网友也纷纷吐槽我虽然是中国人，但是操你妈我更爱美国";
		String text = "Lucene全文检索的说明";
		//单字切分
		Analyzer standardAnalyzer = new StandardAnalyzer(Version.LUCENE_44);
		//二分法切分
		Analyzer cjkAnalyzer = new CJKAnalyzer(Version.LUCENE_44);
		//按词库切分：庖丁分词器
		Analyzer ikAnalyzer = new IKAnalyzer();
		testAnalyzer(standardAnalyzer, text);
		System.out.println("---------------------------------------------------");
		testAnalyzer(cjkAnalyzer, text);
		System.out.println("---------------------------------------------------");
		testAnalyzer(ikAnalyzer, text);
	} 
	
}
