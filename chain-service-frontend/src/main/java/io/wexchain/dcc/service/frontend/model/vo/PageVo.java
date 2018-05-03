package io.wexchain.dcc.service.frontend.model.vo;

import java.util.List;

/**
 * 分页
 * 
 *
 * @param <T>
 */
public class PageVo<T> {
	/**
	 * 总条目数
	 */
	private long totalElements;
	/**
	 * 总分页数
	 */
	private long totalPages;

	/**
	 * 分页号
	 */
	private int number;
	/**
	 * 页尺寸
	 */
	private int size;

	/**
	 * 当前分页条目
	 */
	private List<T> items;

	public long getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(long totalPages) {
		this.totalPages = totalPages;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	public List<T> getItems() {
		return items;
	}

	public void setItems(List<T> items) {
		this.items = items;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
