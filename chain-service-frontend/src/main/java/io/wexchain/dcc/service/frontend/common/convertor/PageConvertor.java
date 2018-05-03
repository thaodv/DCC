package io.wexchain.dcc.service.frontend.common.convertor;

import com.wexmarket.topia.commons.pagination.Pagination;
import io.wexchain.dcc.service.frontend.model.vo.PageVo;

import java.util.List;

/**
 * Created by yy on 2018/5/2.
 */
public class PageConvertor {

    public static <T> PageVo<T> convert(Pagination from , List<T> items){
        PageVo<T> to = new PageVo<>();
        to.setItems(items);
        to.setNumber(from.getSortPageParam().getNumber());
        to.setSize(from.getSortPageParam().getSize());
        to.setTotalElements(from.getTotalElements());
        to.setTotalPages(from.getTotalPages());
        return to;
    }

    public static <T> PageVo<T> convert(Pagination<T> from){
        return convert(from,from.getItems());
    }
}
