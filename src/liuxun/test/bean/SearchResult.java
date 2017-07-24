package liuxun.test.bean;

import java.util.ArrayList;
/**
 * 对搜索结果进行封装
 * @author liuxun
 *
 */
import java.util.List;

public class SearchResult<T> {
	private int count;
	private List<T> list;

	public SearchResult(int count, List<T> list) {
		this.count = count;
		this.list = list;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}
    
}
