package io.wexchain.dcc.service.frontend.service.dcc.loan;

import com.wexmarket.topia.commons.pagination.PageParam;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexyun.open.api.domain.credit2.Credit2Apply;
import io.wexchain.dcc.loan.sdk.contract.LoanOrder;
import io.wexchain.dcc.service.frontend.integration.wexyun.Credit2OperationClient;
import io.wexchain.dcc.service.frontend.model.request.AuthenticationPageRequest;
import io.wexchain.dcc.service.frontend.model.vo.LoanCreditVo;
import io.wexchain.dcc.service.frontend.model.vo.LoanProductVo;
import io.wexchain.dcc.service.frontend.model.vo.PageVo;
import io.wexchain.dcc.service.frontend.service.dcc.loanProduct.LoanProductService;
import io.wexchain.dcc.service.frontend.service.wexyun.ExternalIdCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * LoanCreditServiceImpl
 *
 */
@Service(value = "dccLoanService")
public class LoanServiceImpl implements LoanService{

    private static final Logger logger = LoggerFactory.getLogger(LoanServiceImpl.class);

    @Autowired
    private io.wexchain.dcc.loan.sdk.service.LoanService loanService;

    @Autowired
    private ExternalIdCreator externalIdCreator;

    @Autowired
    private Credit2OperationClient credit2OperationClient;

    @Autowired
    private LoanProductService loanProductService;

    @Override
    public Pagination queryOrders(AuthenticationPageRequest pageRequest) {
        PageParam pageParam = new PageParam(pageRequest.getNumber(), pageRequest.getSize());
        Pagination<LoanOrder> loanOrderPagination = loanService.queryOrderPageByBorrowIndex(pageRequest.getAddress(), pageParam);

        if(CollectionUtils.isEmpty(loanOrderPagination.getItems())){
            return loanOrderPagination;
        }else {
            Pagination<LoanCreditVo> result = new Pagination<>();
            result.setSortPageParam(loanOrderPagination.getSortPageParam());
            result.setTotalPages(loanOrderPagination.getTotalPages());
            result.setTotalElements(loanOrderPagination.getTotalElements());

            Map<String, LoanCreditVo> loanCreditVoMap = new HashMap<>();

            for (LoanOrder loanOrder : loanOrderPagination.getItems()) {
                LoanCreditVo loanCreditVo = new LoanCreditVo();
                loanCreditVo.setOrder(loanOrder);
                loanCreditVoMap.put(externalIdCreator.getExternalId(String.valueOf(loanOrder.getId())),loanCreditVo);
            }
            List<String> externalApplyIdList = loanCreditVoMap.keySet().stream().collect(Collectors.toList());

            List<Credit2Apply> credit2Applies = credit2OperationClient.pageGet(externalApplyIdList);

            if(!CollectionUtils.isEmpty(credit2Applies)){
                for (Credit2Apply credit2Apply : credit2Applies) {
                    String loanProductId = credit2Apply.getItemDetailMap().get("loanProductId");
                    String externalApplyId = credit2Apply.getExternalApplyId();
                    LoanProductVo loanProductVo = loanProductService.getLoanProductVo(Long.parseLong(loanProductId));
                    loanCreditVoMap.get(externalApplyId).setCredit2Apply(credit2Apply);
                    loanCreditVoMap.get(externalApplyId).setLoanProduct(loanProductVo);
                }
            }
            List<LoanCreditVo> loanCreditVos = loanCreditVoMap.values().stream().collect(Collectors.toList());
            result.setItems(loanCreditVos);
            return result;
        }
    }
}
