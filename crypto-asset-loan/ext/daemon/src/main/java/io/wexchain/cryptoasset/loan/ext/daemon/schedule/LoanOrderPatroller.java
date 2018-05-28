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
import java.util.Arrays;
import java.util.Date;

/**
 * LoanOrderPatroller
 *
 * @author zhengpeng
 */
public class LoanOrderPatroller {

	private static final Logger logger = LoggerFactory.getLogger(LoanOrderPatroller.class);
	
	@Autowired
	private LockTemplate lockTemplate;

	@Autowired
	private LoanOrderRepository loanOrderRepository;

	@Resource(name = "loanOrderExecutor")
	private OrderExecutor<LoanOrder, LoanOrderInstruction> loanOrderExecutor;

	private static final int PAGE_SIZE = 20;

	public void patrol() {

		lockTemplate.execute("LoanOrderPatroller", null, () -> {
			logger.debug("Loan order patrol start");

			DateTime now = new DateTime();
			Date beginTime = now.minusDays(3).toDate();
			Date endTime = now.minusMinutes(5).toDate();

			int count = loanOrderRepository.countByCreatedTimeBetweenAndStatusIn(
					beginTime, endTime,
					Arrays.asList(LoanOrderStatus.CREATED, LoanOrderStatus.APPROVED));
			int totalPage = (count + PAGE_SIZE - 1) / PAGE_SIZE;

			for (int i = totalPage; i > 0; i--) {
				Page<LoanOrder> orders = loanOrderRepository.findByCreatedTimeBetweenAndStatusIn(
						beginTime, endTime,
						Arrays.asList(LoanOrderStatus.CREATED, LoanOrderStatus.APPROVED),
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

			logger.debug("Loan order patrol end");
			return null;
		});
	}

}
