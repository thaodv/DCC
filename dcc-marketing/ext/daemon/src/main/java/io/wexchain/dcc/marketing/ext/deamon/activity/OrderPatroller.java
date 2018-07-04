package io.wexchain.dcc.marketing.ext.deamon.activity;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.wexmarket.topia.commons.basic.competition.LockTemplate2;

import io.wexchain.dcc.marketing.domainservice.Patroller;

public class OrderPatroller {
	@Autowired
	private LockTemplate2 lockTemplate;
	private List<Patroller> patrollers;

	public void patrol() {
		lockTemplate.execute("patrol", () -> {
			patrollers.parallelStream().forEach(Patroller::patrol);
			return null;
		});
	}

	public void setPatrollers(List<Patroller> patrollers) {
		this.patrollers = patrollers;
	}

}
