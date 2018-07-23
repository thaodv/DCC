package io.wexchain.passport.gateway.service.erc20;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.godmonth.eth.rlp.web3j.SignMessageParser;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import io.wexchain.passport.gateway.ctrlr.ca.CaErrorCode;
import io.wexchain.passport.gateway.ctrlr.erc20.Erc20ErrorCode;
import io.wexchain.passport.gateway.service.cah.CahFunction;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.protocol.core.methods.request.RawTransaction;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Erc20Validation {
	public static ObjectMapper objectMapper = new ObjectMapper();

	private static Set<String> grantMethods = new HashSet<>();
	static {
		grantMethods.add("eth_gasPrice");
		grantMethods.add("eth_getTransactionReceipt");
		grantMethods.add("eth_getTransactionCount");
	}

	private static Set<String> readFunctions = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	static {
		readFunctions.add("70a08231");// balanceOf(address)
	}

	private static Set<String> writeFunctions = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	static {
		writeFunctions.add("a9059cbb");// transfer(address,uint256)
	}

	private Map<String,String> contractAddressMap;

	private CahFunction cahFunction;

	private void validate(JsonNode root,String contractAddress) throws IOException {
		String method = root.get("method").asText();
		if (grantMethods.contains(method)) {
			return;
		} else if (method.equals("eth_call")) {
			JsonNode params = root.get("params");
			ethCallParam(params.get(0),contractAddress);
			return;
		} else if (method.equals("eth_sendRawTransaction")) {
			JsonNode params = root.get("params");
			ethSendRawTransaction(params.get(0),contractAddress);
			return;
		} else {
			throw new IllegalArgumentException();
		}

	}

	public void validateWithBusiness(Map<String, Object> body,String business) throws IOException {
		String contractAddress = getContractAddress(business);
		JsonNode root = objectMapper.readTree(objectMapper.writeValueAsString(body));
		validate(root,contractAddress);
	}

	public void validateWithContractAddress(Map<String, Object> body) throws IOException {
		JsonNode root = objectMapper.readTree(objectMapper.writeValueAsString(body));
		String method = root.get("method").asText();
		String requestAddress;
		if (grantMethods.contains(method)) {
			return;
		} else if (method.equals("eth_call")) {
			requestAddress = root.get("params").get(0).get("to").asText();
		} else if (method.equals("eth_sendRawTransaction")) {
			Pair<RawTransaction, SignatureData> pair = SignMessageParser.parseFull(root.get("params").get(0).asText());
			RawTransaction rawTransaction = pair.getLeft();
			requestAddress = rawTransaction.getTo();
		} else {
			throw new IllegalArgumentException();
		}
		ErrorCodeValidate.isTrue(cahFunction.matchContractAddress(requestAddress),
				CaErrorCode.SIGN_MESSAGE_INVALID,"to invalid");
		validate(root,requestAddress);
	}

	private void ethCallParam(JsonNode param,String contractAddress) throws IOException {
		JsonNode to = param.get("to");
		ErrorCodeValidate.isTrue(to.asText().equalsIgnoreCase(contractAddress),
				CaErrorCode.SIGN_MESSAGE_INVALID, "to invalid");
		ethReadFunction(param);
	}

	private void ethSendRawTransaction(JsonNode param, String contractAddress) throws IOException {
		String data = param.asText();
		Pair<RawTransaction, SignatureData> pair = SignMessageParser.parseFull(data);
		RawTransaction rawTransaction = pair.getLeft();
		ErrorCodeValidate.isTrue(rawTransaction.getTo().equalsIgnoreCase(contractAddress),
				CaErrorCode.SIGN_MESSAGE_INVALID, "to invalid");
		String data2 = rawTransaction.getData();
		String functionId = data2.substring(0, 8);
		ErrorCodeValidate.isTrue(writeFunctions.contains(functionId),
				CaErrorCode.SIGN_MESSAGE_INVALID, "methodId invalid");
	}

	private void ethReadFunction(JsonNode param) throws IOException {
		String data = param.get("data").asText();
		String functionId = StringUtils.substring(data, 2).substring(0, 8);
		ErrorCodeValidate.isTrue(readFunctions.contains(functionId),
				CaErrorCode.SIGN_MESSAGE_INVALID, "methodId invalid");
	}

	public String getContractAddress(String business){
		return ErrorCodeValidate.notNull(contractAddressMap.get(business), Erc20ErrorCode.BUSINESS_NOT_FOUND);
	}

	@Required
	public void setContractAddressMap(Map<String, String> contractAddressMap) {
		this.contractAddressMap = contractAddressMap;
	}
	@Required
	public void setCahFunction(CahFunction cahFunction) {
		this.cahFunction = cahFunction;
	}
}