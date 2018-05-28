package io.wexchain.cryptoasset.loan.common.function;

/**
 * ThrowingFunction
 *
 * @author zhengpeng
 */
@FunctionalInterface
public interface ThrowingFunction<T, R> {

    R apply(T t) throws Exception;
}
