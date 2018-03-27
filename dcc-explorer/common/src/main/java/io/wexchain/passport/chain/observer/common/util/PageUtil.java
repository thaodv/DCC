package io.wexchain.passport.chain.observer.common.util;

import io.wexchain.passport.chain.observer.common.request.PageParam;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * PageUtil
 *
 * @author zhengpeng
 */
public class PageUtil {

     public static PageRequest convert(PageParam pageParam) {
          return new PageRequest(pageParam.getPage() <=0 ? pageParam.getPage() : pageParam.getPage() -1 , pageParam.getPageSize());
     }

     public static PageRequest convert(PageParam pageParam, Sort sort) {
          return new PageRequest(pageParam.getPage() <=0 ? pageParam.getPage() : pageParam.getPage() -1 , pageParam.getPageSize(), sort);
     }

}
