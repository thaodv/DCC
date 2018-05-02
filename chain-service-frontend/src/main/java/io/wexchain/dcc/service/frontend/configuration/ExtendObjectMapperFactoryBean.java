package io.wexchain.dcc.service.frontend.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.godmonth.util.jackson.ObjectMapperFactoryBean;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;

import java.util.List;

/**
 * Created by yy on 2017/7/20.
 */
public class ExtendObjectMapperFactoryBean extends Jackson2ObjectMapperFactoryBean {

    private boolean camel;
    private List<ObjectMapperFactoryBean.Mixin> mixins;

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        if (camel) {
            getObject().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        }
        getObject().setSerializationInclusion(JsonInclude.Include.ALWAYS);
        if (CollectionUtils.isNotEmpty(mixins)) {
            for (ObjectMapperFactoryBean.Mixin mixin : mixins) {
                getObject().addMixIn(mixin.getTarget(), mixin.getMixinSource());
            }
        }

    }

    public void setCamel(boolean camel) {
        this.camel = camel;
    }

    public void setMixins(List<ObjectMapperFactoryBean.Mixin> mixins) {
        this.mixins = mixins;
    }

    public static class Mixin {
        private Class<?> target;
        private Class<?> mixinSource;

        public Class<?> getTarget() {
            return target;
        }

        public void setTarget(Class<?> target) {
            this.target = target;
        }

        public Class<?> getMixinSource() {
            return mixinSource;
        }

        public void setMixinSource(Class<?> mixinSource) {
            this.mixinSource = mixinSource;
        }

    }
}
