package io.wexchain.dcc.marketing.domainservice.processor.candy.advancer;

import com.godmonth.status.advancer.impl.InstructionAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.advancer.intf.NextOperation;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;

import io.wexchain.dcc.marketing.api.constant.CandyStatus;
import io.wexchain.dcc.marketing.domain.Candy;
import io.wexchain.dcc.marketing.domainservice.processor.candy.CandyInstruction;
import io.wexchain.dcc.marketing.domainservice.processor.candy.CandyTrigger;

public class PickAdvancer extends InstructionAdvancer<Candy, CandyInstruction, CandyTrigger> {
	{
		availableStatus = CandyStatus.CREATED;
		expectedInstruction = CandyInstruction.PICK;
	}

	@Override
	protected AdvancedResult<Candy, CandyTrigger> doAdvance(Candy model, Object message) {
		return new AdvancedResult<>(new TriggerBehavior<>(CandyTrigger.PICK, model1 -> model1.setBoxCode(null)),
				NextOperation.ASYNC_ADVANCE);
	}

}
