package io.wexchain.passport.chain.observer.helper;

import com.godmonth.util.dozer.DozerMapperFunction;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.wexmarket.topia.commons.basic.rpc.utils.ListResultResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import com.wexmarket.topia.commons.rpc.SystemCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Page;

import java.util.List;

public class ResponseHelper<DOMAIN, MODEL> implements InitializingBean {
	private static final Logger logger = LoggerFactory.getLogger(ResponseHelper.class);

	protected Class<MODEL> modelClass;

	protected Mapper mapper;

	protected Function<DOMAIN, MODEL> function;

	@Override
	public void afterPropertiesSet() throws Exception {
		function = new DozerMapperFunction<DOMAIN, MODEL>(mapper, modelClass);
	}

	public ResultResponse<MODEL> returnSuccess(DOMAIN domain) {
		if (domain != null) {
			return ResultResponseUtils.successResultResponse(getFunction().apply(domain));
		} else {
			return ResultResponseUtils.successResultResponse(null);
		}
	}

	public ListResultResponse<MODEL> returnListSuccess(List<DOMAIN> domainList) {
		if (CollectionUtils.isNotEmpty(domainList)) {
			List<MODEL> modelList = Lists.transform(domainList, getFunction());
			return ListResultResponseUtils.successListResultResponse(modelList);
		} else {
			return ListResultResponseUtils.successListResultResponse(null);
		}
	}

	public ResultResponse<Pagination<MODEL>> returnPageSuccess(Page<DOMAIN> domainPage) {
		Pagination<MODEL> transform = PageTransformer.transform(domainPage, getFunction());
		return ResultResponseUtils.successResultResponse(transform);
	}

	public <K> ResultResponse<MODEL> returnError(K key, Exception e) {
		logger.error("", e);
		ResultResponse<MODEL> sor = ResultResponseUtils.codeResultResponse(null, SystemCode.FAILURE, null,
				e.getMessage());
		sor.setTrace(ExceptionUtils.getStackTrace(e));
		if (key != null) {
			DOMAIN domain = findModel(key);
			if (domain != null) {
				sor.setResult(getFunction().apply(domain));
			}
		}
		return sor;
	}

	protected DOMAIN findModel(Object key) {
		return null;
	}

	public void setModelClass(Class<MODEL> modelClass) {
		this.modelClass = modelClass;
	}

	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}

	public Function<DOMAIN, MODEL> getFunction() {
		return function;
	}

}
