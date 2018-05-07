package io.wexchain.dcc.service.frontend.service.dcc.loan;

import com.wexmarket.topia.commons.pagination.PageParam;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexyun.open.api.domain.credit2.Credit2Apply;
import io.wexchain.dcc.loan.sdk.contract.LoanOrder;
import io.wexchain.dcc.service.frontend.common.constants.FrontendWebConstants;
import io.wexchain.dcc.service.frontend.ctrlr.security.MemberDetails;
import io.wexchain.dcc.service.frontend.integration.wexyun.Credit2OperationClient;
import io.wexchain.dcc.service.frontend.model.request.QueryLoanOrderPageRequest;
import io.wexchain.dcc.service.frontend.model.vo.LoanCreditVo;
import io.wexchain.dcc.service.frontend.model.vo.LoanProductVo;
import io.wexchain.dcc.service.frontend.service.dcc.loanProduct.LoanProductService;
import io.wexchain.dcc.service.frontend.service.wexyun.ExternalIdCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


/**
 * LoanCreditServiceImpl
 *
 */
@Service(value = "dccLoanService")
public class LoanServiceImpl implements LoanService,FrontendWebConstants{

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
    public Pagination queryOrders(QueryLoanOrderPageRequest pageRequest, MemberDetails memberDetails) {
        PageParam pageParam = new PageParam(pageRequest.getNumber(), pageRequest.getSize());
        Pagination<LoanOrder> loanOrderPagination = loanService.queryOrderPageByBorrowIndex("123123123", pageParam);

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
                setOrderInfo(loanOrder,loanCreditVo);
                loanCreditVoMap.put(externalIdCreator.getExternalId(memberDetails.getId().toString(),String.valueOf(loanOrder.getId())),loanCreditVo);
            }
            List<String> externalApplyIdList = loanCreditVoMap.keySet().stream().collect(Collectors.toList());

            List<Credit2Apply> credit2Applies = credit2OperationClient.pageGet(externalApplyIdList);

            if(!CollectionUtils.isEmpty(credit2Applies)){
                for (Credit2Apply credit2Apply : credit2Applies) {
                    String loanProductId = credit2Apply.getItemDetailMap().get("loan_product_id");
                    if(loanProductId == null){
                        continue;
                    }
                    String externalApplyId = credit2Apply.getExternalApplyId();
                    LoanProductVo loanProductVo = loanProductService.getLoanProductVo(Long.parseLong(loanProductId));
                    setCredit2Apply(credit2Apply,loanCreditVoMap.get(externalApplyId));
                    setLoanProduct(loanProductVo,loanCreditVoMap.get(externalApplyId));
                }
            }
            List<LoanCreditVo> loanCreditVos = loanCreditVoMap.values().stream().collect(Collectors.toList());
            loanCreditVos.sort(Comparator.comparingLong(LoanCreditVo::getOrderId));
            result.setItems(loanCreditVos);
            return result;
        }
    }

    @Override
    public LoanOrder getLastOrder(MemberDetails memberDetails) {
        try {
            return loanService.getLastOrder(memberDetails.getUsername());
        } catch (IOException e) {
            logger.error("查询最后借款订单失败：", e);
            throw new RuntimeException(e);
        }
    }


    private void setOrderInfo(LoanOrder from,LoanCreditVo to){
        to.setOrderId(from.getId());
        to.setBorrower(from.getBorrower());
        to.setStatus(from.getStatus());
        to.setFee(from.getFee());
        to.setReceiveAddress(from.getReceiveAddress());
    }

    private void setLoanProduct(LoanProductVo from, LoanCreditVo to){
        to.setLoanProductId(from.getId());
        to.setCurrency(from.getCurrency());
        to.setLoanRate(from.getLoanRate());
        to.setLender(from.getLender());
        to.setRepayAheadRate(from.getRepayAheadRate());

    }

    private void setCredit2Apply(Credit2Apply from, LoanCreditVo to){
        BigDecimal borrowAmount = new BigDecimal(from.getBorrowAmount().toString()).divide(AMOUNT_MULTIPLY).setScale(4, RoundingMode.DOWN);
        to.setApplyId(from.getApplyId());
        to.setBorrowAmount(borrowAmount);
        to.setBorrowDuration(from.getBorrowDuration());
        to.setDurationType(from.getDurationType());
        to.setApplyDate(from.getApplyDate());
        to.setGmtCreate(from.getGmtCreate());

        Calendar c = Calendar.getInstance();
        c.setTime(from.getApplyDate());
        switch (from.getDurationType()){
            case DAY:{
                c.add(Calendar.DATE, from.getBorrowDuration());
                break;
            }
            case MONTH:{
                c.add(Calendar.MONTH, from.getBorrowDuration());
                break;
            }
            case YEAR:{
                c.add(Calendar.YEAR, from.getBorrowDuration());
                break;
            }
        }
        to.setExpirationTime(c.getTime());
    }
}
