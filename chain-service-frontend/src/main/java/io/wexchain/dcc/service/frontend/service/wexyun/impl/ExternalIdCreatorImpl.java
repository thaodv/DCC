package io.wexchain.dcc.service.frontend.service.wexyun.impl;

import io.wexchain.dcc.service.frontend.service.wexyun.ExternalIdCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 *
 */
@Service
public class ExternalIdCreatorImpl implements ExternalIdCreator {

    private static final String EXTERNAL_APPLY_ID  = "{0}_{1}_{2}";

    @Value(value = "${app.identity}")
    private String appIdentity;

    public String getExternalId(String mark,String orderId){
        return MessageFormat.format(EXTERNAL_APPLY_ID,appIdentity,mark,orderId);
    }
}
