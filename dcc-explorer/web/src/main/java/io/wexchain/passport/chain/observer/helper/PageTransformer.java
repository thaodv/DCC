package io.wexchain.passport.chain.observer.helper;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.wexmarket.topia.commons.data.page.PageUtils;
import com.wexmarket.topia.commons.data.page.SortUtils;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexmarket.topia.commons.pagination.SortPageParam;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class PageTransformer {

	public static <IN, OUT> Pagination<OUT> transform(Page<IN> page, Function<IN, OUT> function) {
		if (page == null) {
			return null;
		}
		Pagination<OUT> pagination = new Pagination<OUT>();
		pagination.setTotalElements(page.getTotalElements());
		pagination.setTotalPages(page.getTotalPages());
		pagination.setItems(Lists.transform(page.getContent(), function));
		pagination.setSortPageParam(
				new SortPageParam(convertPageNum(page), page.getSize(), SortUtils.convert(page.getSort())));
		return pagination;
	}
	public static <T> Pagination<T> transform(Page<T> page) {
		if (page == null) {
			return null;
		}
		Pagination<T> pagination = new Pagination<T>();
		pagination.setTotalElements(page.getTotalElements());
		pagination.setTotalPages(page.getTotalPages());
		pagination.setItems(page.getContent());
		pagination.setSortPageParam(
				new SortPageParam(convertPageNum(page), page.getSize(), SortUtils.convert(page.getSort())));
		return pagination;
	}

	public static <T> Page<T> transform(Pagination<T> pagination) {
		if (pagination == null) {
			return null;
		}
		PageRequest pageRequest = PageUtils.convert(pagination.getSortPageParam());
		return new PageImpl<T>(pagination.getItems(), pageRequest, pagination.getTotalElements());
	}

	private static int convertPageNum(Page page){
		if(page.getNumber() <= 0){
			if(CollectionUtils.isEmpty(page.getContent())){
				return 0;
			}
		}
		return page.getNumber() +1;
	}

}
