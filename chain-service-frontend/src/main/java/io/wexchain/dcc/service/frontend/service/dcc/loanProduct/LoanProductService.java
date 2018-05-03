package io.wexchain.dcc.service.frontend.service.dcc.loanProduct;


import io.wexchain.dcc.service.frontend.model.vo.LoanProductVo;

import java.util.List;

/**
 * @author yanyi
 */

public interface LoanProductService {

    LoanProductVo getLoanProductVo(Long id);

    List<LoanProductVo> getLoanProductVoList(String lenderCode);

}
