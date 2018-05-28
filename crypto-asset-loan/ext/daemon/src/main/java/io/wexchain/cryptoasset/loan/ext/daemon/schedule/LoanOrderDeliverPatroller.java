package io.wexchain.cryptoasset.loan.ext.daemon.schedule;

import com.godmonth.status.executor.intf.OrderExecutor;
import com.wexmarket.topia.commons.basic.competition.LockTemplate;
import io.wexchain.cryptoasset.loan.api.constant.LoanOrderStatus;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.repository.LoanOrderRepository;
import io.wexchain.cryptoasset.loan.service.processor.order.loan.LoanOrderInstruction;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;

/**
 * LoanOrderPatroller
 *
 * @author zhengpeng
 */
public class LoanOrderDeliverPatroller {

	private static final Logger logger = LoggerFactory.getLogger(LoanOrderDeliverPatroller.class);
	
	@Autowired
	private LockTemplate lockTemplate;

	@Autowired
	private LoanOrderRepository loanOrderRepository;

	@Resource(name = "loanOrderExecutor")
	private OrderExecutor<LoanOrder, LoanOrderInstruction> loanOrderExecutor;

	private static final int PAGE_SIZE = 20;

	public void patrol() {

		lockTemplate.execute("LoanOrderDeliverPatroller", null, () -> {
			logger.debug("Loan order deliver patrol start");

			DateTime now = new DateTime();
			Date beginTime = now.minusDays(3).toDate();
			Date endTime = now.minusMinutes(1).toDate();

			int count = loanOrderRepository.countByCreatedTimeBetweenAndStatusIn(
					beginTime, endTime,
					Collections.singletonList(LoanOrderStatus.DELIVERED));
			int totalPage = (count + PAGE_SIZE - 1) / PAGE_SIZE;

			for (int i = totalPage; i > 0; i--) {
				Page<LoanOrder> orders = loanOrderRepository.findByCreatedTimeBetweenAndStatusIn(
						beginTime, endTime,
						Collections.singletonList(LoanOrderStatus.DELIVERED),
						PageRequest.of(i - 1, PAGE_SIZE, Sort.by("createdTime")));
				if (CollectionUtils.isNotEmpty(orders.getContent())) {
					orders.getContent().forEach(order -> {
						try {
							loanOrderExecutor.executeAsync(order, null, null);
						} catch (Exception e) {
							logger.error("advance order fail, order id:{}", order.getId(), e);
						}
					});
				}
			}
			logger.debug("Loan order deliver patrol end");
			return null;
		});
	}

}
