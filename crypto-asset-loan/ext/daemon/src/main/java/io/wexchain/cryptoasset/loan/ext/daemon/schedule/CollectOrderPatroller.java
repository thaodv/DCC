package io.wexchain.cryptoasset.loan.ext.daemon.schedule;

import com.godmonth.status.executor.intf.OrderExecutor;
import com.wexmarket.topia.commons.basic.competition.LockTemplate;
import com.wexmarket.topia.commons.data.page.PageUtils;
import com.wexmarket.topia.commons.pagination.PageParam;
import io.wexchain.cryptoasset.loan.api.constant.CollectOrderStatus;
import io.wexchain.cryptoasset.loan.domain.CollectOrder;
import io.wexchain.cryptoasset.loan.repository.CollectOrderRepository;
import io.wexchain.cryptoasset.loan.service.processor.order.collect.CollectOrderInstruction;
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
 * CollectOrderPatroller
 *
 * @author zhengpeng
 */
public class CollectOrderPatroller {

	private static final Logger logger = LoggerFactory.getLogger(CollectOrderPatroller.class);
	
	@Autowired
	private LockTemplate lockTemplate;

	@Autowired
	private CollectOrderRepository collectOrderRepository;

	@Resource(name = "collectOrderExecutor")
	private OrderExecutor<CollectOrder, CollectOrderInstruction> collectOrderExecutor;

	private static final int PAGE_SIZE = 20;

	public void patrol() {

		lockTemplate.execute("CollectOrderPatroller", null, () -> {
			logger.debug("Collect order patrol start");

			DateTime now = new DateTime();
			Date beginTime = now.minusDays(3).toDate();
			Date endTime = now.minusMinutes(20).toDate();

			int count = collectOrderRepository.countByCreatedTimeBetweenAndStatusIn(
					beginTime, endTime,
					Arrays.asList(CollectOrderStatus.CREATED, CollectOrderStatus.FUELED));
			int totalPage = (count + PAGE_SIZE - 1) / PAGE_SIZE;

			for (int i = totalPage; i > 0; i--) {
				Page<CollectOrder> orders = collectOrderRepository.findByCreatedTimeBetweenAndStatusIn(
                            beginTime, endTime,
                            Arrays.asList(CollectOrderStatus.CREATED, CollectOrderStatus.FUELED),
							PageRequest.of(i - 1, PAGE_SIZE, Sort.by("createdTime")));
				if (CollectionUtils.isNotEmpty(orders.getContent())) {
					orders.getContent().forEach(order -> {
						try {
							collectOrderExecutor.executeAsync(order, null, null);
						} catch (Exception e) {
							logger.error("advance order fail, order id:{}", order.getId(), e);
						}
					});
				}
			}

			logger.debug("Collect order patrol end");
			return null;
		});
	}

}
