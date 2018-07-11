package io.wexchain.dcc.marketing.domainservice.function.miningevent.impl;

import io.wexchain.dcc.marketing.domain.LastLoginTime;
import io.wexchain.dcc.marketing.domain.MiningRewardRecord;
import io.wexchain.dcc.marketing.domainservice.function.miningevent.MiningEventHandler;
import io.wexchain.dcc.marketing.repository.LastLoginTimeRepository;
import io.wexchain.notify.domain.dcc.LoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * MiningLoginEventHandler
 *
 * @author zhengpeng
 */
public class MiningLoginEventHandler implements MiningEventHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String eventName;

    @Autowired
    private LastLoginTimeRepository lastLoginTimeRepository;

    @Override
    public boolean canHandle(Object obj) {
        return obj instanceof LoginEvent;
    }

    @Override
    public MiningRewardRecord handle(Object obj) {
        if (!(obj instanceof LoginEvent)) {
            return null;
        }

        LoginEvent event = (LoginEvent) obj;
        LastLoginTime lastLoginTime = lastLoginTimeRepository.findByAddress(event.getAddress());
        if (lastLoginTime != null) {
            lastLoginTime.setLastLoginTime(event.getLoginDate());
        } else {
            lastLoginTime = new LastLoginTime();
            lastLoginTime.setAddress(event.getAddress());
            lastLoginTime.setLastLoginTime(event.getLoginDate());
        }
        lastLoginTimeRepository.save(lastLoginTime);

        return null;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
