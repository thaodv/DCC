package io.wexchain.dcc.service.frontend.configuration;

import io.wexchain.dcc.marketing.api.model.Activity;
import io.wexchain.dcc.service.frontend.model.vo.ActivityVo;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

/**
 * OrikaConfiguration
 *
 * @author zhengpeng
 */
@Component
public class OrikaConfiguration extends ConfigurableMapper {

    protected void configure(MapperFactory factory) {

        factory.classMap(DateTime.class, DateTime.class)
                .customize(new CustomMapper<DateTime, DateTime>() {
                    @Override
                    public void mapAtoB(DateTime a, DateTime b, MappingContext mappingContext) {
                        b.withTime(a.hourOfDay().get(), a.minuteOfHour().get(),
                                a.secondOfMinute().get(), a.millisOfSecond().get());
                    }
                })
                .register();

        factory.classMap(Activity.class, ActivityVo.class)
                .byDefault()
                .register();

    }
}
