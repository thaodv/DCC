package io.wexchain.dcc.service.frontend.service.dcc.lender;


import io.wexchain.dcc.service.frontend.model.Lender;

import java.util.List;

/**
 * @author yanyi
 */

public interface LenderService {

    Lender getLender(String code);

    Lender getDefaultLender();

    List<Lender> getLenderList();

}
