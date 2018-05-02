package io.wexchain.dcc.marketing.domainservice.processor.activity.advancer;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;
import io.wexchain.dcc.marketing.api.constant.ActivityStatus;
import io.wexchain.dcc.marketing.domain.Activity;
import io.wexchain.dcc.marketing.domainservice.function.web3.AllowanceAmountReader;
import io.wexchain.dcc.marketing.domainservice.processor.activity.ActivityInstruction;
import io.wexchain.dcc.marketing.domainservice.processor.activity.ActivityTrigger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;


public class SwitchAdvancer extends AbstractAdvancer<Activity, ActivityInstruction, ActivityTrigger> {

	@Autowired
	private AllowanceAmountReader allowanceAmountReader;

	@Override
	public AdvancedResult<Activity, ActivityTrigger> advance(
			Activity activity, ActivityInstruction instruction, Object message) {
		DateTime now = new DateTime();
		DateTime startTime = new DateTime(activity.getStartTime());
		DateTime endTime = new DateTime(activity.getEndTime());
		if (now.isAfter(endTime)) {
			return new AdvancedResult<>(new TriggerBehavior<>(ActivityTrigger.END));
		} else if (now.isAfter(startTime)) {
			BigInteger allowanceAmount = allowanceAmountReader.getAllowanceAmount(activity.getSupplierAddress());
			if (allowanceAmount.compareTo(BigInteger.ZERO) > 0) {
				if (activity.getStatus() == ActivityStatus.STARTED) {
					return null;
				}
				return new AdvancedResult<>(new TriggerBehavior<>(ActivityTrigger.START));
			} else {
				return new AdvancedResult<>(new TriggerBehavior<>(ActivityTrigger.END));
			}
		}
		return null;
	}
}