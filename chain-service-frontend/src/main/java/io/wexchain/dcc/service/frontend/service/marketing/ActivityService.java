package io.wexchain.dcc.service.frontend.service.marketing;


import io.wexchain.dcc.service.frontend.model.request.QueryActivityVoRequest;
import io.wexchain.dcc.service.frontend.model.vo.ActivityVo;

import java.util.List;

/**
 * 用户服务
 * @author yanyi
 */

public interface ActivityService {

    List<ActivityVo> queryActivity(QueryActivityVoRequest request);

}
