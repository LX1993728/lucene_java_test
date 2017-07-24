package liuxun.test.lucene;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.junit.Test;

import liuxun.test.bean.Article;
import liuxun.test.utils.ArticleDocumentUtils;
import liuxun.test.utils.LuceneUtils;

public class TestQuery {

	private void searchAndShowResult(Query query) {
		try {
			System.out.println(">>>>>>>>>>>对应的查询字符串为 " + query.toString() + " >>>>>>>>>>>>>");
			// 2，执行搜索，得到中间结果
			IndexSearcher indexSearcher = LuceneUtils.getIndexSearcher();
			TopDocs topDocs = indexSearcher.search(query, 100); // 返回查询出来的前n条结果
			int count = topDocs.totalHits; // 总结果数
			ScoreDoc[] scoreDocs = topDocs.scoreDocs; // 前n条结果的信息
			// 3，处理结果
			List<Article> list = new ArrayList<Article>();
			for (int i = 0; i < scoreDocs.length; i++) {
				// 根据内部编号取出真正的Document数据
				Document doc = indexSearcher.doc(scoreDocs[i].doc);
				// 把Document转为Article
				Article article = ArticleDocumentUtils.documentToArticle(doc);
				list.add(article);
			}

			// 显示结果
			System.out.println("总结果数：" + count);
			for (Article article : list) {
				System.out.println("---------> id = " + article.getId());
				System.out.println("title  = " + article.getTitle());
				System.out.println("content= " + article.getContent());
			}
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 当前使用的分词器：StandardAnalyzer 对title="Lucene全文检索的说明" 的分词结果(词元)如下： terms—>
	 * lucene 全 文 检 索 的 说 明 index—> 0 1 2 3 4 5 6 7
	 */

	// 范围查询
	@Test
	public void testNumericRangeQuery() {
		// 查询字符串为：id:{1 TO 3}
		// Query query = NumericRangeQuery.newIntRange("id", 1, 3, false,
		// false);

		// 查询字符串为：id:[1 TO 3]
		Query query = NumericRangeQuery.newIntRange("id", 1, 3, true, true);
		searchAndShowResult(query);
		/**
		 * 查询字符串总结： 范围查询是针对数字类型的域(字段) 格式：fieldName:{或[ min TO max }或]
		 * 如果不包含最大值和最小值 fieldName:{min TO max} 如果包含最大值和最小值 fieldName:[min TO
		 * max] 如果不包含最大值但包含最小值 fieldName:[min TO max} 如果包含最大值但不包含最小值
		 * fieldName:{min TO max}
		 */
	}

	// 关键词查询
	@Test
	public void testTermQuery() {
		// 查询字符串为：title:lucene
		Query query = new TermQuery(new Term("title", "lucene"));
		searchAndShowResult(query);
		/**
		 * 查询字符串总结： 关键词查询是针对文本或字符串类型的域(字段) 格式: fieldName:keyword 其中keyword是关键字
		 * 注意： 如果field属于StringField类型 因为StringField不分词 只有把相关字段的的整体值全部作为关键字
		 * 才可能会有搜索结果 如果field属于TextField类型 TextField类型是分词的 选取关键词需要考虑当前使用的分词器
		 * —>如果当前的分词器是StandardAnalyzer或ChineseAnalyzer 即单字分词器 关键字如果设置中文只能选取单个字符
		 * —>如果当前的分词器是CJKAnalyzer 即二分法分词器 关键字如果设置中文只能选取相邻的两个字符
		 * —>如果当前的分词器是MMAnalyzer(极易)或IKAnalyzer(庖丁) 即词库分词器
		 * 关键字如果设置中文基本可以选取符合中文语法的词语
		 * 
		 */
	}

	// 通配符查询
	// ? 代表一个任意字符 * 表示任意个任意字符
	@Test
	public void testWildcardQuery() {
		// 查询字符串为：title:lu*n?
		Query query = new WildcardQuery(new Term("title", "lu*n?"));
		searchAndShowResult(query);
		/**
		 * 查询字符串总结： 通配符查询是针对文本或字符串类型的域(字段) 格式: fieldName:pattern 其中pattern是匹配格式
		 * 不管是文本类型还是字符串类型的域 只要某切分后的词元符合pattern即可 类似于正则表达式
		 * 相当于将pattern看成了该字段切分后一个词元的值 关键词以外的内容使用通配符替代 注意：filedName 对应的pattern
		 * 匹配的是一个切分后的词元 表达式的选取也跟分词器有关
		 */
	}

	// 查询所有
	@Test
	public void testMatchAllDocsQuery() {
		// 查询字符串为： *:*
		Query query = new MatchAllDocsQuery();
		searchAndShowResult(query);
	}

	// 模糊查询
	@Test
	public void testFuzzyQuery() {
		/**
		 * 1:查询的条件term 2：maxEidts 默认值为2 ，最大的可编辑数，允许我的查询当中的值可以错误几个字符..
		 */
		// 查询字符串为：title:lcbcene~2
		FuzzyQuery query = new FuzzyQuery(new Term("title", "lcbcene"), 2);
		searchAndShowResult(query);
		/**
		 * 查询字符串总结： 模糊查询时针对字符串类型的域(StringField)或文本类型(TextField)使用的
		 * 格式：fieldName:value~2 使用模糊查询要注意以下几点：也是针对切分后的词元来说的 切分后的词元和value可以错误两个字符
		 * 例如："Lucene全文检索的说明" 根据标准分词器切分后的词元 含有lucene (在创建索引时分词器做了转化将L大写改为了小写)
		 * "lcbcene"字符串 和 "lucene"词元 相差了两个字符，如果改为"Lcbcene" 就和"lucene"词元相差3个字符
		 * 就会匹配不到
		 */
	}

	// 短语查询
	@Test
	public void testPhraseQuery() {
		PhraseQuery query = new PhraseQuery();
		// 查询字符串为：title:"lucene ? ? ? ? ? ? 明"
		// query.add(new Term("title", "lucene"), 0);//0 表示第一个位置
		// query.add(new Term("title", "明"), 7);

		// 查询字符串为：title:"lucene 全 明"~10
		query.add(new Term("title", "lucene"));
		query.add(new Term("title", "全"));
		query.add(new Term("title", "明"));
		// 表示之间的间隔最大不超过10个词
		query.setSlop(10);
		searchAndShowResult(query);
		/**
		 * 查询字符串总结：如果涉及多个词元必须带有双引号 fieldName:"a ? ? ? b ? ? ? ? c" 其中a,b,c
		 * 都是分割后的词元 ？代替一个字符 a和b之间有三个词元 b和c之间有4个词元 fieldName:"a b c"~10 其中a,b,c
		 * 都是分割后的词元 ~10表示a,b,c三个词元之间两两间隔最多不超过10个词元
		 */
	}

	// 布尔查询(********)
	@Test
	public void testBooleanQuery() {
		// booleanQuery.add(query, Occur.MUST); // 必须满足
		// booleanQuery.add(query, Occur.MUST_NOT); // 非
		// booleanQuery.add(query, Occur.SHOULD); // 多个SHOULD一起用是OR的关系
		BooleanQuery booleanQuery = new BooleanQuery();

		Query query1 = new MatchAllDocsQuery();
		Query query2 = NumericRangeQuery.newIntRange("id", 5, 15, false, false);

		// 对应的查询字符串为： +*:* +id:{5 TO 15} 等同于 *:* AND id:{5 TO 15]
		// booleanQuery.add(query1, Occur.MUST);
		// booleanQuery.add(query2, Occur.MUST);

		// 对应的查询字符串为： +*:* -id:{5 TO 15} 等同于 *:* NOT id:{5 TO 15]
		// booleanQuery.add(query1, Occur.MUST);
		// booleanQuery.add(query2, Occur.MUST_NOT);

		// 对应的查询字符串为：*:* id:{5 TO 15} 等同于 *:* OR id:{5 TO 15]
		booleanQuery.add(query1, Occur.SHOULD);
		booleanQuery.add(query2, Occur.SHOULD);

		searchAndShowResult(booleanQuery);
	}

	/**
	 * ------------------------------------------------------------------------
	 * 对于以上各种Query的子类都是通过Term匹配切分后的词元 如果词元写错 就不会有搜索结果了
	 * 
	 * 查询的另外一种方式是使用QueryParser或MultiFieldQueryParser（单字段或多字段解析器）
	 * 使用里面的parse(keywords|queryString) 方法根据输入的关键字或字符串表达式 如果输入的是关键字
	 * 会自动解析term(词元)拼接成查询字符串 进行检索
	 * 
	 * @throws ParseException
	 */
	@Test
	public void test_MultiFieldQueryParser_parse() throws ParseException {
		MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(LuceneUtils.getVersion(),
				new String[] { "title", "content", "id" }, LuceneUtils.getAnalyzer());
		// 查询字符串：(title:全 title:文 title:检 title:索)
		// Query query = multiFieldQueryParser.parse("全文检索"); //使用关键字搜索
		// Query query = multiFieldQueryParser.parse("title:全"); //使用查询字符串搜索_1
		// Query query = multiFieldQueryParser.parse("author:张四~2");
		// //使用查询字符串搜索_2
		// Query query =
		// multiFieldQueryParser.parse("title:Lucen*");//使用查询字符串搜索_3

		// Query query = multiFieldQueryParser.parse("+title:全 +content:全文检索");
		// //使用查询字符串+关键字搜索_1
		// Query query = multiFieldQueryParser.parse("title:\"Lucene 检索
		// 明\"~10"); //使用查询字符串+关键字搜索_2
		// 查询字符串：+author:钱三~2 +(content:全 content:文 content:检)
		// Query query = multiFieldQueryParser.parse("+author:钱三~10
		// +content:全文检"); //使用查询字符串+关键字搜索_3

		// 查询字符串：
		// (title:"lucene 检 索 明"~10 content:"lucene 检 索 明"~10 id:"lucene 检 索
		// 明"~10) +(content:含 content:有 content:指 content:定 content:词 content:汇)
		// 如果不指定fieldName 默认从MultiQueryParser初始化时传入的String[]
		// fields指定的多个field中进行查询 他们之间是或 关系 OR
		// 对于关系复杂的可以使用() 进行分组 多个括号之间可以使用"+"或者 "-" 或者空格" " 来表示AND NOT和OR关系
		// Query query = multiFieldQueryParser.parse("\"Lucene 检索 明\"~10
		// +content:含有指定词汇"); //使用查询字符串+关键字搜索_3

		// 注意：QueryParser不支持数字范围的查询 如果查询范围的不会搜索出任何查询结果
		// 解决方案 在调用indexWriter.search() 方法时 可以传入一个NumericRangeQueryFilter的过滤器
		// Query query = multiFieldQueryParser.parse("id:[10 TO 15]");

		// ------------------------------------------------------------------------------------
		// 多字段多关键字查询 字段1—>关键字1 字段2—>关键字2 ... 字段N—>关键字2N 他们之间有NOT AND 或OR的关系

		// 关键字数组
		String[] keywords = new String[] { "Lucene", "李四", "全文检索" };
		// 与关键字对应的字段数组
		String[] fields = new String[] { "title", "author", "content" };
		// 创建关系数组
		Occur[] occurs = new Occur[] { Occur.MUST, Occur.MUST_NOT, Occur.MUST };
		//查询字符串为：+title:lucene -(author:李 author:四) +(content:全 content:文 content:检 content:索)
		//含义 title属性中必须含有与Lucene相关的词元 作者属性中不能含有李四  content中必须含有全文检索相关的内容
		Query query = MultiFieldQueryParser.parse(LuceneUtils.getVersion(), keywords, fields, occurs,
				LuceneUtils.getAnalyzer());

		searchAndShowResult(query);

	}
}
