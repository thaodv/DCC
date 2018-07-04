package com.xxy.maple.tllibrary.engine;

import com.xxy.maple.tllibrary.entity.TransferCreateData;
import com.xxy.maple.tllibrary.entity.TransferLendData;
import com.xxy.maple.tllibrary.entity.TransferMortgageData;
import com.xxy.maple.tllibrary.entity.TransferRevertData;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;


/**
 * Created by Gaoguanqi on 2018/5/30.
 */

public class TransferEngine {

    //创建合约
    public static String engineCreate(TransferCreateData data ){
        //-- 一下是需要签名的参数
        TransferCreateData.ParamBean param = data.getParam();
        Address _value1 = new Address(param.getPledge_token_address());
        BigInteger pledge_amount = new BigInteger(param.getPledge_amount());
        Uint256 _value2 = new Uint256(pledge_amount);
        Address _value3 = new Address(param.getLoan_token_address());
        BigInteger loan_amount = new BigInteger(param.getLoan_amount());
        Uint256 _value4 = new Uint256(loan_amount);
        Address _value5 = new Address(param.getPlatform_accounts_address());
        BigInteger platform_account_rate = new BigInteger(param.getPlatform_account_rate());
        Uint256 _value6 = new Uint256(platform_account_rate);
        Address _value7 = new Address(param.getWallet_accounts_address());
        BigInteger pledge_service_fee = new BigInteger(param.getPledge_service_fee());
        Uint256 _value8 = new Uint256(pledge_service_fee);
        BigInteger lender_service_fee = new BigInteger(param.getLender_service_fee());
        Uint256 _value9 = new Uint256(lender_service_fee);
        BigInteger repay_amount = new BigInteger(param.getRepay_amount());
        Uint256 _value10 = new Uint256(repay_amount);
        BigInteger repay_service_fee = new BigInteger((param.getRepay_service_fee()));
        Uint256 _value11 = new Uint256(repay_service_fee);
        BigInteger compensate_service_fee = new BigInteger(param.getCompensate_service_fee());
        Uint256 _value12 = new Uint256(compensate_service_fee);
        BigInteger compensate_amount = new BigInteger(param.getCompensate_amount());
        Uint256 _value13 = new Uint256(compensate_amount);
        //---
        Function function = new Function("newTokenLeaderOrder", Arrays.<Type>asList(_value1, _value2, _value3, _value4, _value5, _value6, _value7, _value8, _value9, _value10, _value11, _value12, _value13), Collections.<TypeReference<?>>emptyList());
        String dataHex = FunctionEncoder.encode(function);
        return dataHex;
    }

    //抵押
    public static String engineMortgage() {
        return "0xb8e20c41";
    }

    //抵押授权
    public static String engineMortgageApprove(TransferMortgageData data) {
        Address _value1 = new Address(data.getNew_address());
        BigInteger amount = new BigInteger(data.getAmount());
        Uint256 _value2 = new Uint256(amount);
        Function function = new Function("approve", Arrays.<Type>asList(_value1,_value2), Collections.<TypeReference<?>>emptyList());
        String dataHex = FunctionEncoder.encode(function);
        return dataHex;
    }

    //出借
    public static String engineLend() {
        return "0x95e97eee";
    }


    //出借授权
    public static String engineLendApprove(TransferLendData data) {
        Address _value1 = new Address(data.getNew_address());
        BigInteger amount = new BigInteger(data.getAmount());
        Uint256 _value2 = new Uint256(amount);
        Function function = new Function("approve", Arrays.<Type>asList(_value1,_value2), Collections.<TypeReference<?>>emptyList());
        String dataHex = FunctionEncoder.encode(function);
        return dataHex;
    }

    //归还
    public static String engineRevert() {
        return "0x590e1ae3";
    }


    //归还授权
    public static String engineRevertApprove(TransferRevertData data) {
        Address _value1 = new Address(data.getNew_address());
        BigInteger amount = new BigInteger(data.getAmount());
        Uint256 _value2 = new Uint256(amount);
        Function function = new Function("approve", Arrays.<Type>asList(_value1,_value2), Collections.<TypeReference<?>>emptyList());
        String dataHex = FunctionEncoder.encode(function);
        return dataHex;
    }

    //赔付
    public static String engineIndemnity() {
        return "0x1f97a75a";
    }

    public static String engineCancle() {
        return "0xea8a1af0";
    }
}
