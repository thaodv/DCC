package io.wexchain.dcc.sdk.client.contract;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.base.Suppliers;

import io.wexchain.dcc.sdk.service.UploadParam;

public class ContractClientImpl extends BaseClientImpl implements ContractClient {

	public static final Duration DEFAULT_EXPIRED_DURATION = Duration.ofHours(1);

	private Duration expiredDuration = DEFAULT_EXPIRED_DURATION;

	private Supplier<String> contractAddressValue;
	private Supplier<String> ownerValue;

	public void init() {
		if (expiredDuration != null && expiredDuration.getSeconds() > 0) {
			contractAddressValue = Suppliers.memoizeWithExpiration(this::innerGetContractAddress,
					expiredDuration.getSeconds(), TimeUnit.SECONDS);
			ownerValue = Suppliers.memoizeWithExpiration(this::innerGetOwner, expiredDuration.getSeconds(),
					TimeUnit.SECONDS);
		} else {
			contractAddressValue = Suppliers.memoize(this::innerGetContractAddress);
			contractAddressValue = Suppliers.memoize(this::innerGetOwner);
		}

	}

	@Override
	public String getContractAddress() {
		return contractAddressValue.get();
	}

	@Override
	public String getOwner() {
		return ownerValue.get();
	}

	protected URI getContractAddressUri() {
		return UriComponentsBuilder.fromHttpUrl(basePath + subPath + "/getContractAddress").build().toUri();
	}

	protected String innerGetContractAddress() {
		return querySingleResult(getContractAddressUri(), objectMapper.constructType(String.class));
	}

	protected URI getContractOwnerUri() {
		return UriComponentsBuilder.fromHttpUrl(basePath + subPath + "/getOwner").build().toUri();
	}

	protected String innerGetOwner() {
		return querySingleResult(getContractOwnerUri(), objectMapper.constructType(String.class));
	}

	@Override
	public String fastFail(UploadParam uploadParam) {
		return upload(createSubFunctionUri("/fastFail"), uploadParam);
	}

}
