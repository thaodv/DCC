package io.wexchain.dcc.service.frontend.integration.common;

/**
 * <p>执行器</p>
 * @author yanyi
 */
public interface Invoker<T> {

    public T excute() throws Exception;

}
