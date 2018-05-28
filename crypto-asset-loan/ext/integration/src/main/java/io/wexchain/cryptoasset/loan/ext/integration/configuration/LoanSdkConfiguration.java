package io.wexchain.cryptoasset.loan.ext.integration.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.wexchain.dcc.cert.sdk.client.CertLowerLevelClient;
import io.wexchain.dcc.cert.sdk.client.CertLowerLevelClientImpl;
import io.wexchain.dcc.cert.sdk.service.CertService;
import io.wexchain.dcc.cert.sdk.service.CertServiceImpl;
import io.wexchain.dcc.loan.sdk.client.AgreementLowerLevelClient;
import io.wexchain.dcc.loan.sdk.client.AgreementLowerLevelClientImpl;
import io.wexchain.dcc.loan.sdk.client.LoanLowerLevelClient;
import io.wexchain.dcc.loan.sdk.client.LoanLowerLevelClientImpl;
import io.wexchain.dcc.loan.sdk.service.AgreementService;
import io.wexchain.dcc.loan.sdk.service.AgreementServiceImpl;
import io.wexchain.dcc.loan.sdk.service.LoanService;
import io.wexchain.dcc.loan.sdk.service.LoanServiceImpl;
import io.wexchain.dcc.sdk.client.receipt.ReceiptClient;
import io.wexchain.dcc.sdk.client.receipt.ReceiptClientImpl;
import io.wexchain.dcc.sdk.client.ticket.TicketClient;
import io.wexchain.dcc.sdk.client.ticket.TicketClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.IOException;

/**
 * LoanSdkConfiguration
 *
 * @author zhengpeng
 */
@Configuration
public class LoanSdkConfiguration {

	private static RestTemplate REST_TEMPLATE = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
	private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Value("${gateway.url.prefix}")
	private String basePath;

	@Value("${gateway.wallet.path}")
	private String walletPath;

	@Value("${gateway.wallet.password}")
	private String walletPassword;

	@Value("${wallet.address.pay}")
	private String payerAddress;

	public String getPayerAddress() {
		return payerAddress;
	}

	@Bean(name = "loanSdkCredentials")
	public Credentials loanSdkCredentials() throws IOException, CipherException {
		return WalletUtils.loadCredentials(walletPassword, walletPath);
	}

	@Bean
	public LoanService loanService() {
		LoanServiceImpl loanService = new LoanServiceImpl();
		loanService.setContractClient(loanLowerLevelClient());
		loanService.setReceiptClient(receiptClient());
		loanService.setTicketClient(ticketClient());
		return loanService;
	}

	@Bean
	public AgreementService agreementService() {
		AgreementServiceImpl agreementService = new AgreementServiceImpl();
		agreementService.setContractClient(agreementLowerLevelClient());
		agreementService.setReceiptClient(receiptClient());
		agreementService.setTicketClient(ticketClient());
		return agreementService;
	}

	@Bean
	public CertService certService() {
		CertServiceImpl certService = new CertServiceImpl();
		certService.setCertLowerLevelClient(idCertClient());
		certService.setReceiptClient(receiptClient());
		certService.setTicketClient(ticketClient());
		return certService;
	}

	@Bean
	public TicketClient ticketClient() {
		TicketClientImpl ticketClient = new TicketClientImpl();
		ticketClient.setBasePath(basePath);
		ticketClient.setObjectMapper(OBJECT_MAPPER);
		ticketClient.setRestTemplate(REST_TEMPLATE);
		return ticketClient;
	}

	@Bean
	public ReceiptClient receiptClient() {
		ReceiptClientImpl receiptClient = new ReceiptClientImpl();
		receiptClient.setBasePath(basePath);
		receiptClient.setObjectMapper(OBJECT_MAPPER);
		receiptClient.setRestTemplate(REST_TEMPLATE);
		return receiptClient;
	}

	@Bean
	public LoanLowerLevelClient loanLowerLevelClient() {
		LoanLowerLevelClientImpl loanLowerLevelClient = new LoanLowerLevelClientImpl();
		loanLowerLevelClient.setBasePath(basePath);
		loanLowerLevelClient.setObjectMapper(OBJECT_MAPPER);
		loanLowerLevelClient.setRestTemplate(REST_TEMPLATE);
		loanLowerLevelClient.setSubPath("/dcc/loan/1");
		loanLowerLevelClient.init();
		return loanLowerLevelClient;
	}

	@Bean
	public AgreementLowerLevelClient agreementLowerLevelClient() {
		AgreementLowerLevelClientImpl agreementLowerLevelClient = new AgreementLowerLevelClientImpl();
		agreementLowerLevelClient.setBasePath(basePath);
		agreementLowerLevelClient.setObjectMapper(OBJECT_MAPPER);
		agreementLowerLevelClient.setRestTemplate(REST_TEMPLATE);
		agreementLowerLevelClient.setSubPath("/dcc/agreement/1");
		agreementLowerLevelClient.init();
		return agreementLowerLevelClient;
	}

	@Bean
	public CertLowerLevelClient idCertClient(){
		CertLowerLevelClientImpl certLowerLevelClient = new CertLowerLevelClientImpl();
		certLowerLevelClient.setBasePath(basePath);
		certLowerLevelClient.setObjectMapper(OBJECT_MAPPER);
		certLowerLevelClient.setRestTemplate(REST_TEMPLATE);
		certLowerLevelClient.setSubPath("/dcc/cert/2/id");
		certLowerLevelClient.init();
		return certLowerLevelClient;
	}
}
