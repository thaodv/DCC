package io.wexchain.passport.gateway.ctrlr.block;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

import juzix.web3j.response.RemoteTimeStamp;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionResult;
import org.web3j.protocol.core.methods.response.EthBlockNumber;

import com.wexmarket.topia.commons.basic.rpc.utils.ListResultResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;

import juzix.web3j.protocol.CustomWeb3j;
import org.web3j.utils.Numeric;

@RequestMapping("/block/1")
@RestController
public class BlockController {
	@Autowired
	protected CustomWeb3j web3j;

	@RequestMapping("/getTxHashList")
	public ListResultResponse<String> getTxHashList(@RequestParam @NotBlank BigInteger blockNumber) throws IOException {
		EthBlock ethBlock = web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(blockNumber), false).send();
		List<TransactionResult> transactions = ethBlock.getBlock().getTransactions();
		List<String> collect = transactions.stream().map(TransactionResult::get).map(t -> (String) t)
				.collect(Collectors.toList());
		return ListResultResponseUtils.successListResultResponse(collect);
	}

	@RequestMapping("/getBlockNumber")
	public ResultResponse<BigInteger> getBlockNumber() throws IOException {
		EthBlockNumber send = web3j.ethBlockNumber().send();
		return ResultResponseUtils.successResultResponse(send.getBlockNumber());
	}

	@RequestMapping("/isAlive")
	public void isAlive(HttpServletResponse response) throws IOException {
		BigInteger blockNumber = web3j.ethBlockNumber().send().getBlockNumber();
		EthBlock.Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), false).send().getBlock();
		response.getWriter().print(block.getTimestamp().longValue() - get5MinBeforeTime() > 0);

	}

	private long get5MinBeforeTime(){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE,-5);
		return cal.getTimeInMillis()/1000;
	}
}
