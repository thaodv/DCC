package io.wexchain.dcc.service.frontend.integration.message;

public interface MessageRouter<T> {
	void route(T message);
}
