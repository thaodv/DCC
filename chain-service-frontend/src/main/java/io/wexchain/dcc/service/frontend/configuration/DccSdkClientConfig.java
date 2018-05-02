package io.wexchain.dcc.service.frontend.configuration;

import io.wexchain.dcc.ca.sdk.client.CaLowerLevelClient;
import io.wexchain.dcc.ca.sdk.client.CaLowerLevelClientImpl;
import io.wexchain.dcc.loan.sdk.client.LoanLowerLevelClient;
import io.wexchain.dcc.loan.sdk.client.LoanLowerLevelClientImpl;
import io.wexchain.dcc.sdk.client.receipt.ReceiptClient;
import io.wexchain.dcc.sdk.client.receipt.ReceiptClientImpl;
import io.wexchain.dcc.sdk.client.ticket.TicketClient;
import io.wexchain.dcc.sdk.client.ticket.TicketClientImpl;
import io.wexchain.dcc.service.frontend.utils.Rests;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by yy on 2018/4/10.
 */
@Configuration
public class DccSdkClientConfig {


    @Value("${gateway.url.prefix}")
    private String basePath;

    @Bean(name = "ticketClient")
    public TicketClient ticketClient(){
        TicketClientImpl ticketClient = new TicketClientImpl();
        ticketClient.setBasePath(basePath);
        ticketClient.setObjectMapper(Rests.OBJECT_MAPPER);
        ticketClient.setRestTemplate(Rests.REST_TEMPLATE);
        return ticketClient;
    }
    @Bean(name = "receiptClient")
    public ReceiptClient receiptClient(){
        ReceiptClientImpl receiptClient = new ReceiptClientImpl();
        receiptClient.setBasePath(basePath);
        receiptClient.setObjectMapper(Rests.OBJECT_MAPPER);
        receiptClient.setRestTemplate(Rests.REST_TEMPLATE);
        return receiptClient;
    }

    @Bean(name = "caLowerLevelClient")
    public CaLowerLevelClient caLowerLevelClient(){
        CaLowerLevelClientImpl caLowerLevelClient = new CaLowerLevelClientImpl();
        caLowerLevelClient.setBasePath(basePath);
        caLowerLevelClient.setObjectMapper(Rests.OBJECT_MAPPER);
        caLowerLevelClient.setRestTemplate(Rests.REST_TEMPLATE);
        caLowerLevelClient.init();
        return caLowerLevelClient;
    }
    @Bean(name = "loanLowerLevelClient")
    public LoanLowerLevelClient loanClient(){
        LoanLowerLevelClientImpl loanLowerLevelClient = new LoanLowerLevelClientImpl();
        loanLowerLevelClient.setBasePath(basePath);
        loanLowerLevelClient.setObjectMapper(Rests.OBJECT_MAPPER);
        loanLowerLevelClient.setRestTemplate(Rests.REST_TEMPLATE);
        loanLowerLevelClient.setSubPath("/dcc/loan/1");
        loanLowerLevelClient.init();
        return loanLowerLevelClient;
    }

}
