package io.wexchain.dcc.sdk.client.ticket;

import java.io.IOException;

public interface TicketClient {
	String getTicket() throws IOException;
}
