package com.xxy.maple.tllibrary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xxy.maple.tllibrary.R;
import com.xxy.maple.tllibrary.app.AppConstant;
import com.xxy.maple.tllibrary.base.BaseActivity;
import com.xxy.maple.tllibrary.engine.TransferEngine;
import com.xxy.maple.tllibrary.entity.BalanceParams;
import com.xxy.maple.tllibrary.entity.BaseEntity;
import com.xxy.maple.tllibrary.entity.GaspriceNonceEntity;
import com.xxy.maple.tllibrary.entity.SignParams;
import com.xxy.maple.tllibrary.entity.TransferCancleData;
import com.xxy.maple.tllibrary.entity.TransferCreateData;
import com.xxy.maple.tllibrary.entity.TransferIndemnityData;
import com.xxy.maple.tllibrary.entity.TransferLendData;
import com.xxy.maple.tllibrary.entity.TransferMortgageData;
import com.xxy.maple.tllibrary.entity.TransferRevertData;
import com.xxy.maple.tllibrary.exception.ApiException;
import com.xxy.maple.tllibrary.retrofit.RetrofitFactory;
import com.xxy.maple.tllibrary.retrofit.observer.ProgressObserver;
import com.xxy.maple.tllibrary.utils.Utils;

import org.json.JSONObject;
import org.web3j.abi.datatypes.Address;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public abstract class TlWalletPayActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton ibtBack;
    private TextView tvTitle;
    private TextView tvLoadnAddress;
    private TextView tvContractAddress;
    private TextView tvTipFee;
    private TextView tvFee;
    private TextView tvServiceFee;
    private TextView tvValue;
    private SeekBar seekBar;
    private Button btnSubmit;

    private TransferCreateData transferCreateData;
    private TransferMortgageData transferMortgageData;
    private TransferLendData transferLendData;
    private TransferRevertData transferRevertData;
    private TransferIndemnityData transferIndemnityData;
    private TransferCancleData transferCancleData;

    private GaspriceNonceEntity gaspriceNonce;


    private String loadnAddress, contractAddress, tipFee, fee, serviceFee;

    private String type = "";
    private String signType = "";
    private String country = "cn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tlwallet_pay);
        String data = getIntent().getStringExtra("data");
        String country = Locale.getDefault().getCountry();
        if (TextUtils.equals(country, "CN")) {
            this.country = "cn";
        } else if (TextUtils.equals(country, "UK") || TextUtils.equals(country, "US")) {
            this.country = "en";
        }
        initView();
        initData(data);
    }

    private void initView() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowHomeEnabled(false);
            supportActionBar.setDisplayHomeAsUpEnabled(false);
            supportActionBar.setDisplayShowTitleEnabled(false);
        }
        ibtBack = findViewById(R.id.ibt_back);
        tvTitle = findViewById(R.id.tv_title);
        tvLoadnAddress = findViewById(R.id.tv_loadn_address);
        tvContractAddress = findViewById(R.id.tv_contract_address);
        tvServiceFee = findViewById(R.id.tv_service_fee);
        tvTipFee = findViewById(R.id.tv_tip_fee);
        tvFee = findViewById(R.id.tv_fee);
        tvValue = findViewById(R.id.tv_value);
        seekBar = findViewById(R.id.seekBar);
        btnSubmit = findViewById(R.id.btn_submit);

        ibtBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    private void initData(String data) {

        Observable.just(data).flatMap((Function<String, ObservableSource<BaseEntity<GaspriceNonceEntity>>>) s -> {

            if (TextUtils.isEmpty(data)) throw new ApiException("数据异常");
            JSONObject jsonObject = new JSONObject(data);
            type = jsonObject.getString("type");
            BalanceParams params = new BalanceParams();

            if (TextUtils.equals(type, AppConstant.GlobalValue.TYPE_CREATE)) {
                transferCreateData = new Gson().fromJson(data, TransferCreateData.class);
                params.setType(transferCreateData.getType());
                params.setLanguage(country);
                params.setOrder_no(transferCreateData.getOrder_no());
                params.setAddress(transferCreateData.getLoan_address());

                loadnAddress = transferCreateData.getLoan_address();
                contractAddress = transferCreateData.getMaster_contract_address();

                tipFee = Utils.getString(TlWalletPayActivity.this, R.string.text_transaction_amount_create);
                fee = transferCreateData.getTransaction_amount();
                serviceFee = transferCreateData.getTransaction_fee();
                signType = "1";
            } else if (TextUtils.equals(type, AppConstant.GlobalValue.TYPE_MORTGAGE)) {
                transferMortgageData = new Gson().fromJson(data, TransferMortgageData.class);
                params.setType(transferMortgageData.getType());
                params.setLanguage(country);
                params.setOrder_no(transferMortgageData.getOrder_no());
                params.setAddress(transferMortgageData.getLoan_address());

                loadnAddress = transferMortgageData.getLoan_address();
                contractAddress = transferMortgageData.getNew_address();

                tipFee = Utils.getString(TlWalletPayActivity.this, R.string.text_transaction_amount_mortgage);
                fee = transferMortgageData.getTransaction_amount();
                serviceFee = transferMortgageData.getTransaction_fee();

                signType = transferMortgageData.getSign_type();
            } else if (TextUtils.equals(type, AppConstant.GlobalValue.TYPE_LEND)) {
                transferLendData = new Gson().fromJson(data, TransferLendData.class);
                params.setType(transferLendData.getType());
                params.setLanguage(country);
                params.setOrder_no(transferLendData.getOrder_no());
                params.setAddress(transferLendData.getLender_address());

                loadnAddress = transferLendData.getLoan_address();
                contractAddress = transferLendData.getNew_address();

                tipFee = Utils.getString(TlWalletPayActivity.this, R.string.text_transaction_amount_lend);
                fee = transferLendData.getTransaction_amount();
                serviceFee = transferLendData.getTransaction_fee();

                signType = transferLendData.getSign_type();
            } else if (TextUtils.equals(type, AppConstant.GlobalValue.TYPE_REVERT)) {
                transferRevertData = new Gson().fromJson(data, TransferRevertData.class);
                params.setType(transferRevertData.getType());
                params.setLanguage(country);
                params.setOrder_no(transferRevertData.getOrder_no());
                params.setAddress(transferRevertData.getLoan_address());

                loadnAddress = transferRevertData.getLoan_address();
                contractAddress = transferRevertData.getNew_address();

                tipFee = Utils.getString(TlWalletPayActivity.this, R.string.text_transaction_amount_revert);
                fee = transferRevertData.getTransaction_amount();
                serviceFee = transferRevertData.getTransaction_fee();
                signType = transferRevertData.getSign_type();
            } else if (TextUtils.equals(type, AppConstant.GlobalValue.TYPE_INDEMNITY)) {
                transferIndemnityData = new Gson().fromJson(data, TransferIndemnityData.class);
                params.setType(transferIndemnityData.getType());
                params.setLanguage(country);
                params.setOrder_no(transferIndemnityData.getOrder_no());
                params.setAddress(transferIndemnityData.getLender_address());

                loadnAddress = transferIndemnityData.getLender_address();
                contractAddress = transferIndemnityData.getNew_address();

                tipFee = Utils.getString(TlWalletPayActivity.this, R.string.text_transaction_amount_indemnity);
                fee = transferIndemnityData.getTransaction_amount();
                serviceFee = transferIndemnityData.getTransaction_fee();
                signType = transferIndemnityData.getSign_type();
            } else if (TextUtils.equals(type, AppConstant.GlobalValue.TYPE_CANCLE)) {
                transferCancleData = new Gson().fromJson(data, TransferCancleData.class);
                params.setType(transferCancleData.getType());
                params.setLanguage(country);
                params.setOrder_no(transferCancleData.getOrder_no());
                params.setAddress(transferCancleData.getLoan_address());

                loadnAddress = transferCancleData.getLoan_address();
                contractAddress = transferCancleData.getNew_address();

                tipFee = Utils.getString(TlWalletPayActivity.this, R.string.text_transaction_amount_cancle);
                fee = transferCancleData.getTransaction_amount();
                serviceFee = transferCancleData.getTransaction_fee();
                signType = transferCancleData.getSign_type();
            } else {
                throw new ApiException("数据异常");
            }
            return RetrofitFactory.getInstance().getGaspriceNonce(params.getType(), params.getOrder_no(), params.getAddress(), params.getLanguage());
        }).compose(compose(this.bindToLifecycle()))
                .subscribe(new ProgressObserver<BaseEntity<GaspriceNonceEntity>>(this) {
                    @Override
                    protected void onSuccess(BaseEntity<GaspriceNonceEntity> entity) {
                        gaspriceNonce = entity.getData();
                        if (gaspriceNonce != null) {
                            processData(signType, gaspriceNonce);
                        }
                    }
                });
    }

    Double base = 0d;


    private String mValue = "";

    private void processData(String signType, GaspriceNonceEntity data) {
        tvLoadnAddress.setText(loadnAddress);
        tvContractAddress.setText(contractAddress);
        tvTipFee.setText(tipFee);
        tvFee.setText(fee);
        tvServiceFee.setText(serviceFee);
        btnSubmit.setEnabled(true);

        if (TextUtils.isEmpty(signType)) return;
        if (TextUtils.equals(signType, "1")) {
            String gas = data.getGas_price().getGas();
            base = Double.parseDouble(gas);
        } else if (TextUtils.equals(signType, "2")) {
            String gas_auth = data.getGas_price().getGas_auth();
            String gas_trans = data.getGas_price().getGas_trans();
            base = Double.parseDouble(gas_auth) + Double.parseDouble(gas_trans);
        }

        String maxStr = data.getGas_price().getMax();
        String minStr = data.getGas_price().getMin();


        double dmax = Double.parseDouble(maxStr);
        double dmin = Double.parseDouble(minStr);


        double max = getBase(dmax, 100d);
        double min = getBase(dmin, 100d);
        seekBar.setMax((int) (max - min));

        double zValue = getValueBase(Double.valueOf(dmin), base);

        tvValue.setText(String.format("%.6f", zValue));


        mValue = String.format("%.2f", dmin);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                double value = progress + min;
                mValue = String.format("%.2f", (value / 100d));
                double zValue = getValueBase(Double.valueOf(mValue), base);
                tvValue.setText(String.format("%.6f", zValue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private double getBase(double b, double v) {
        return b * v;
    }

    private double getValueBase(double b, double v) {
        return b * v * Math.pow(10, -9);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ibt_back) {
            finish();
        } else if (id == R.id.btn_submit) {
            if (TextUtils.isEmpty(type) && TextUtils.isEmpty(signType)) return;
            if (TextUtils.equals(type, AppConstant.GlobalValue.TYPE_CREATE)) {
                engineCreate(type);
            } else if (TextUtils.equals(type, AppConstant.GlobalValue.TYPE_MORTGAGE)) {
                engineMortgage(type, signType);
            } else if (TextUtils.equals(type, AppConstant.GlobalValue.TYPE_LEND)) {
                engineLend(type, signType);
            } else if (TextUtils.equals(type, AppConstant.GlobalValue.TYPE_REVERT)) {
                engineRevert(type, signType);
            } else if (TextUtils.equals(type, AppConstant.GlobalValue.TYPE_INDEMNITY)) {
                engineIndemnity(type);
            } else if (TextUtils.equals(type, AppConstant.GlobalValue.TYPE_CANCLE)) {
                engineCancle(type);
            } else {
                Toast.makeText(this, "不存在的类型", Toast.LENGTH_LONG).show();
            }
        }
    }

//    private BigInteger getGasPrice(String gasPrice) {
//        Double dGasPrice = Double.valueOf(gasPrice);
//        BigDecimal bdGasPrice = BigDecimal.valueOf(dGasPrice);
//        BigDecimal bdBase = BigDecimal.valueOf(base);
//        BigDecimal dbValue = bdGasPrice.divide(bdBase, 10, BigDecimal.ROUND_HALF_DOWN);
//        BigDecimal value = dbValue.multiply(BigDecimal.valueOf(Math.pow(10, 18)));
//        BigInteger GAS_PRICE = value.toBigInteger();
//        return GAS_PRICE;
//    }

    private BigInteger getGasPrice() {
        BigInteger GAS_PRICE = BigInteger.valueOf(20_000_000_000L);
        ;
        if (!TextUtils.isEmpty(mValue)) {
            double dValue = Double.parseDouble(mValue);
            BigDecimal bigDecimal = BigDecimal.valueOf(dValue);
            BigDecimal value = bigDecimal.multiply(BigDecimal.valueOf(Math.pow(10, 9)));
            GAS_PRICE = value.toBigInteger();
        }
        return GAS_PRICE;
    }


    private void engineCreate(String type) {
        Observable.create((ObservableOnSubscribe<SignParams>) e -> {
            if (TextUtils.isEmpty(type) && transferCreateData == null && gaspriceNonce == null && base <= 0d)
                throw new ApiException("数据异常！");

            Address from = new Address(transferCreateData.getParam().getLoan_token_address());
            Address to = new Address(transferCreateData.getMaster_contract_address());


            BigInteger nonce = BigInteger.valueOf(Long.valueOf(gaspriceNonce.getNonce()));
            BigInteger gasLimit = new BigInteger(gaspriceNonce.getGas_price().getGas());
            BigInteger gasPrice = getGasPrice();
            BigInteger value = BigInteger.ZERO;

            SignParams signParams = new SignParams();
            signParams.setFrom(from);
            signParams.setTo(to);
            signParams.setNonce(nonce);
            signParams.setGasLimit(gasLimit);
            signParams.setGasPrice(gasPrice);
            signParams.setValue(value);
            String dataHax = TransferEngine.engineCreate(transferCreateData);
            signParams.setDataHex(dataHax);
            e.onNext(signParams);
            e.onComplete();
        }).flatMap((Function<SignParams, ObservableSource<BaseEntity>>) signParams -> {
            //String sing = transmitCreate(signParams);
            String sing = transmitSign(signParams);
            if (TextUtils.isEmpty(sing)) throw new ApiException("外部签名串错误");
            String lender_address = "";
            String auth_sign = "";
            return RetrofitFactory.getInstance().requestChain(type, transferCreateData.getOrder_no(), transferCreateData.getLoan_address(), lender_address, auth_sign, sing);
        }).compose(compose(this.bindToLifecycle()))
                .subscribe(new ProgressObserver<BaseEntity>(this) {
                    @Override
                    protected void onSuccess(BaseEntity entity) {
                        if (entity.isSuccess()) {
                            Intent intent = new Intent().putExtra("type", "0").putExtra("order_no", transferCreateData.getOrder_no());
                            setResultFinish(intent);
                        } else {
                            Toast.makeText(TlWalletPayActivity.this, entity.getErrmsg(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void engineMortgage(String type, String signType) {

        if (TextUtils.equals(signType, "1")) {
            Observable.create((ObservableOnSubscribe<SignParams>) e -> {
                if (TextUtils.isEmpty(type) && transferMortgageData == null && gaspriceNonce == null && base <= 0d)
                    throw new ApiException("数据异常！");
                SignParams signParams = new SignParams();
                Address from = new Address(transferMortgageData.getLoan_address());
                Address to = new Address(transferMortgageData.getNew_address());


                BigInteger nonce = BigInteger.valueOf(Long.valueOf(gaspriceNonce.getNonce()));
                BigInteger gasLimit = new BigInteger(gaspriceNonce.getGas_price().getGas());
                BigInteger gasPrice = getGasPrice();
                BigInteger value = new BigInteger(transferMortgageData.getEth_total_fee());

                signParams.setFrom(from);
                signParams.setTo(to);
                signParams.setNonce(nonce);
                signParams.setGasLimit(gasLimit);
                signParams.setGasPrice(gasPrice);
                signParams.setValue(value);
                String dataHax = TransferEngine.engineMortgage();
                signParams.setDataHex(dataHax);

                e.onNext(signParams);
                e.onComplete();
            }).flatMap((Function<SignParams, ObservableSource<BaseEntity>>) signParams -> {
                //String sing = transmitMortgage(signParams);
                String sing = transmitSign(signParams);
                if (TextUtils.isEmpty(sing)) throw new ApiException("外部签名串错误");
                String lender_address = "";
                String auth_sign = "";
                return RetrofitFactory.getInstance().requestChain(type, transferMortgageData.getOrder_no(), transferMortgageData.getLoan_address(), lender_address, auth_sign, sing);
            }).compose(compose(this.bindToLifecycle()))
                    .subscribe(new ProgressObserver<BaseEntity>(this) {
                        @Override
                        protected void onSuccess(BaseEntity entity) {
                            if (entity.isSuccess()) {
                                Intent intent = new Intent().putExtra("type", type).putExtra("order_no", transferMortgageData.getOrder_no());
                                setResultFinish(intent);
                            } else {
                                Toast.makeText(TlWalletPayActivity.this, entity.getErrmsg(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else if (TextUtils.equals(signType, "2")) {
            Observable.create((ObservableOnSubscribe<SignParams>) e -> {
                if (TextUtils.isEmpty(type) && transferMortgageData == null && gaspriceNonce == null && base <= 0d)
                    throw new ApiException("数据异常！");
                SignParams signParams = new SignParams();
                Address from = new Address(transferMortgageData.getLoan_address());
                Address to = new Address(transferMortgageData.getToken_address());
                BigInteger nonce = BigInteger.valueOf(Long.valueOf(gaspriceNonce.getNonce()));
                BigInteger gasLimit = new BigInteger(gaspriceNonce.getGas_price().getGas_auth());
                BigInteger gasPrice = getGasPrice();
                BigInteger value = BigInteger.ZERO;

                signParams.setFrom(from);
                signParams.setTo(to);
                signParams.setNonce(nonce);
                signParams.setGasLimit(gasLimit);
                signParams.setGasPrice(gasPrice);
                signParams.setValue(value);
                String dataHax = TransferEngine.engineMortgageApprove(transferMortgageData);
                signParams.setDataHex(dataHax);

                e.onNext(signParams);
                e.onComplete();
            }).flatMap((Function<SignParams, ObservableSource<BaseEntity>>) authParams -> {
                if (authParams == null) throw new ApiException("签名参数异常！");
                // String authSign = transmitMortgageApprove(authParams);
                String authSign = transmitSign(authParams);
                if (TextUtils.isEmpty(authSign)) throw new ApiException("外部签名串错误");

                SignParams signParams = new SignParams();
                Address from = new Address(transferMortgageData.getLoan_address());
                Address to = new Address(transferMortgageData.getNew_address());
                BigInteger nonce = BigInteger.valueOf(Long.valueOf(gaspriceNonce.getNonce()) + 1);
                BigInteger gasLimit = new BigInteger(gaspriceNonce.getGas_price().getGas_trans());
                BigInteger gasPrice = getGasPrice();
                BigInteger value = new BigInteger(transferMortgageData.getEth_service_fee());

                signParams.setFrom(from);
                signParams.setTo(to);
                signParams.setNonce(nonce);
                signParams.setGasLimit(gasLimit);
                signParams.setGasPrice(gasPrice);
                signParams.setValue(value);
                String dataHax = TransferEngine.engineMortgage();
                signParams.setDataHex(dataHax);
                //String sign = transmitMortgage(signParams);
                String sign = transmitSign(signParams);
                if (TextUtils.isEmpty(sign)) throw new ApiException("外部签名串错误");
                String lender_address = "";
                return RetrofitFactory.getInstance().requestChain(type, transferMortgageData.getOrder_no(), transferMortgageData.getLoan_address(), lender_address, authSign, sign);
            }).compose(compose(this.bindToLifecycle()))
                    .subscribe(new ProgressObserver<BaseEntity>(this) {
                        @Override
                        protected void onSuccess(BaseEntity entity) {
                            if (entity.isSuccess()) {
                                Intent intent = new Intent().putExtra("type", type).putExtra("order_no", transferMortgageData.getOrder_no());
                                setResultFinish(intent);
                            } else {
                                Toast.makeText(TlWalletPayActivity.this, entity.getErrmsg(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }


    private void engineLend(String type, String signType) {
        if (TextUtils.equals(signType, "1")) {
            Observable.create((ObservableOnSubscribe<SignParams>) e -> {
                if (TextUtils.isEmpty(type) && transferLendData == null && gaspriceNonce == null && base <= 0d)
                    throw new ApiException("数据异常！");

                Address from = new Address(transferLendData.getLender_address());
                Address to = new Address(transferLendData.getNew_address());
                BigInteger nonce = BigInteger.valueOf(Long.valueOf(gaspriceNonce.getNonce()));
                BigInteger gasLimit = new BigInteger(gaspriceNonce.getGas_price().getGas());
                BigInteger gasPrice = getGasPrice();
                BigInteger value = new BigInteger(transferLendData.getEth_total_fee());

                SignParams signParams = new SignParams();
                signParams.setFrom(from);
                signParams.setTo(to);
                signParams.setNonce(nonce);
                signParams.setGasLimit(gasLimit);
                signParams.setGasPrice(gasPrice);
                signParams.setValue(value);

                String dataHax = TransferEngine.engineLend();
                signParams.setDataHex(dataHax);

                e.onNext(signParams);
                e.onComplete();
            }).flatMap((Function<SignParams, ObservableSource<BaseEntity>>) signParams -> {
                //String sign = transmitLend(signParams);
                String sign = transmitSign(signParams);
                if (TextUtils.isEmpty(sign)) throw new ApiException("外部签名串错误");
                String authSign = "";
                String mContractAddress = transferLendData.getLender_address();
                return RetrofitFactory.getInstance().requestChain(type, transferLendData.getOrder_no(), transferLendData.getLoan_address(), mContractAddress, authSign, sign);
            }).compose(compose(this.<BaseEntity>bindToLifecycle()))
                    .subscribe(new ProgressObserver<BaseEntity>(this) {
                        @Override
                        protected void onSuccess(BaseEntity entity) {
                            if (entity.isSuccess()) {
                                Intent intent = new Intent().putExtra("type", type).putExtra("order_no", transferLendData.getOrder_no());
                                setResultFinish(intent);
                            } else {
                                Toast.makeText(TlWalletPayActivity.this, entity.getErrmsg(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else if (TextUtils.equals(signType, "2")) {
            Observable.create((ObservableOnSubscribe<SignParams>) e -> {
                if (TextUtils.isEmpty(type) && transferLendData == null && gaspriceNonce == null && base <= 0d)
                    throw new ApiException("数据异常！");

                SignParams signParams = new SignParams();
                Address from = new Address(transferLendData.getLender_address());
                Address to = new Address(transferLendData.getToken_address());


                BigInteger nonce = BigInteger.valueOf(Long.valueOf(gaspriceNonce.getNonce()));
                BigInteger gasLimit = new BigInteger(gaspriceNonce.getGas_price().getGas_auth());
                BigInteger gasPrice = getGasPrice();
                BigInteger value = BigInteger.ZERO;

                signParams.setFrom(from);
                signParams.setTo(to);
                signParams.setNonce(nonce);
                signParams.setGasLimit(gasLimit);
                signParams.setGasPrice(gasPrice);
                signParams.setValue(value);
                String dataHax = TransferEngine.engineLendApprove(transferLendData);
                signParams.setDataHex(dataHax);

                e.onNext(signParams);
                e.onComplete();
            }).flatMap((Function<SignParams, ObservableSource<BaseEntity>>) authParams -> {
                if (authParams == null) throw new ApiException("签名参数异常！");
                //String authSign = transmitLendApprove(authParams);
                String authSign = transmitSign(authParams);
                if (TextUtils.isEmpty(authSign)) throw new ApiException("外部签名串错误");

                SignParams signParams = new SignParams();

                Address from = new Address(transferLendData.getLender_address());
                Address to = new Address(transferLendData.getNew_address());
                BigInteger nonce = BigInteger.valueOf(Long.valueOf(gaspriceNonce.getNonce()) + 1);
                BigInteger gasLimit = new BigInteger(gaspriceNonce.getGas_price().getGas_trans());
                BigInteger gasPrice = getGasPrice();
                BigInteger value = new BigInteger(transferLendData.getEth_service_fee());

                signParams.setFrom(from);
                signParams.setTo(to);
                signParams.setNonce(nonce);
                signParams.setGasLimit(gasLimit);
                signParams.setGasPrice(gasPrice);
                signParams.setValue(value);
                String dataHax = TransferEngine.engineLend();
                signParams.setDataHex(dataHax);
                //String sign = transmitMortgage(signParams);
                String sign = transmitSign(signParams);
                if (TextUtils.isEmpty(sign)) throw new ApiException("外部签名串错误");
                String mContractAddress = transferLendData.getLender_address();
                return RetrofitFactory.getInstance().requestChain(type, transferLendData.getOrder_no(), transferLendData.getLoan_address(), mContractAddress, authSign, sign);
            }).compose(compose(this.bindToLifecycle()))
                    .subscribe(new ProgressObserver<BaseEntity>(this) {
                        @Override
                        protected void onSuccess(BaseEntity entity) {
                            if (entity.isSuccess()) {
                                Intent intent = new Intent().putExtra("type", type).putExtra("order_no", transferLendData.getOrder_no());
                                setResultFinish(intent);
                            } else {
                                Toast.makeText(TlWalletPayActivity.this, entity.getErrmsg(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void engineRevert(String type, String signType) {
        if (TextUtils.equals(signType, "1")) {
            Observable.create((ObservableOnSubscribe<SignParams>) e -> {
                if (TextUtils.isEmpty(type) && transferRevertData == null && gaspriceNonce == null && base <= 0d)
                    throw new ApiException("数据异常！");
                Address from = new Address(transferRevertData.getLoan_address());
                Address to = new Address(transferRevertData.getNew_address());
                BigInteger nonce = BigInteger.valueOf(Long.valueOf(gaspriceNonce.getNonce()));
                BigInteger gasLimit = new BigInteger(gaspriceNonce.getGas_price().getGas());
                BigInteger gasPrice = getGasPrice();
                BigInteger value = new BigInteger(transferRevertData.getEth_total_fee());

                SignParams signParams = new SignParams();
                signParams.setFrom(from);
                signParams.setTo(to);
                signParams.setNonce(nonce);
                signParams.setGasLimit(gasLimit);
                signParams.setGasPrice(gasPrice);
                signParams.setValue(value);

                String dataHax = TransferEngine.engineRevert();
                signParams.setDataHex(dataHax);
                e.onNext(signParams);
                e.onComplete();
            }).flatMap((Function<SignParams, ObservableSource<BaseEntity>>) signParams -> {
                //String sign = transmitLend(signParams);
                String sign = transmitSign(signParams);
                if (TextUtils.isEmpty(sign)) throw new ApiException("外部签名串错误");
                String authSign = "";
                String lender_address = "";
                return RetrofitFactory.getInstance().requestChain(type, transferRevertData.getOrder_no(), transferRevertData.getLoan_address(), lender_address, authSign, sign);

            }).compose(compose(this.bindToLifecycle()))
                    .subscribe(new ProgressObserver<BaseEntity>(this) {
                        @Override
                        protected void onSuccess(BaseEntity entity) {
                            if (entity.isSuccess()) {
                                Intent intent = new Intent().putExtra("type", type).putExtra("order_no", transferRevertData.getOrder_no());
                                setResultFinish(intent);
                            } else {
                                Toast.makeText(TlWalletPayActivity.this, entity.getErrmsg(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else if (TextUtils.equals(signType, "2")) {
            Observable.create((ObservableOnSubscribe<SignParams>) e -> {
                if (TextUtils.isEmpty(type) && transferRevertData == null && gaspriceNonce == null && base <= 0d)
                    throw new ApiException("数据异常！");
                SignParams signParams = new SignParams();
                Address from = new Address(transferRevertData.getLoan_address());
                Address to = new Address(transferRevertData.getToken_address());
                BigInteger nonce = BigInteger.valueOf(Long.valueOf(gaspriceNonce.getNonce()));
                BigInteger gasLimit = new BigInteger(gaspriceNonce.getGas_price().getGas_auth());
                BigInteger gasPrice = getGasPrice();
                BigInteger value = BigInteger.ZERO;

                signParams.setFrom(from);
                signParams.setTo(to);
                signParams.setNonce(nonce);
                signParams.setGasLimit(gasLimit);
                signParams.setGasPrice(gasPrice);
                signParams.setValue(value);
                String dataHax = TransferEngine.engineRevertApprove(transferRevertData);
                signParams.setDataHex(dataHax);

                e.onNext(signParams);
                e.onComplete();
            }).flatMap((Function<SignParams, ObservableSource<BaseEntity>>) authParams -> {
                if (authParams == null) throw new ApiException("签名参数异常！");
                //String authSign = transmitRevertApprove(authParams);
                String authSign = transmitSign(authParams);
                if (TextUtils.isEmpty(authSign)) throw new ApiException("外部签名串错误");

                SignParams signParams = new SignParams();
                Address from = new Address(transferRevertData.getLoan_address());
                Address to = new Address(transferRevertData.getNew_address());
                BigInteger nonce = BigInteger.valueOf(Long.valueOf(gaspriceNonce.getNonce()) + 1);
                BigInteger gasLimit = new BigInteger(gaspriceNonce.getGas_price().getGas_trans());
                BigInteger gasPrice = getGasPrice();
                BigInteger value = new BigInteger(transferRevertData.getEth_service_fee());

                signParams.setFrom(from);
                signParams.setTo(to);
                signParams.setNonce(nonce);
                signParams.setGasLimit(gasLimit);
                signParams.setGasPrice(gasPrice);
                signParams.setValue(value);
                String dataHax = TransferEngine.engineRevert();
                signParams.setDataHex(dataHax);
                //String sign = transmitMortgage(signParams);
                String sign = transmitSign(signParams);
                if (TextUtils.isEmpty(sign)) throw new ApiException("外部签名串错误");
                String lender_address = "";
                return RetrofitFactory.getInstance().requestChain(type, transferRevertData.getOrder_no(), transferRevertData.getLoan_address(), lender_address, authSign, sign);
            }).compose(compose(this.bindToLifecycle()))
                    .subscribe(new ProgressObserver<BaseEntity>(this) {
                        @Override
                        protected void onSuccess(BaseEntity entity) {
                            if (entity.isSuccess()) {
                                Intent intent = new Intent().putExtra("type", type).putExtra("order_no", transferRevertData.getOrder_no());
                                setResultFinish(intent);
                            } else {
                                Toast.makeText(TlWalletPayActivity.this, entity.getErrmsg(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }


    private void engineIndemnity(String type) {
        Observable.create((ObservableOnSubscribe<SignParams>) e -> {
            if (TextUtils.isEmpty(type) && transferIndemnityData == null && gaspriceNonce == null && base <= 0d)
                throw new ApiException("数据异常！");

            Address from = new Address(transferIndemnityData.getLender_address());
            Address to = new Address(transferIndemnityData.getNew_address());
            BigInteger nonce = BigInteger.valueOf(Long.valueOf(gaspriceNonce.getNonce()));
            BigInteger gasLimit = new BigInteger(gaspriceNonce.getGas_price().getGas());
            BigInteger gasPrice = getGasPrice();
            BigInteger value = BigInteger.ZERO;

            SignParams signParams = new SignParams();
            signParams.setFrom(from);
            signParams.setTo(to);
            signParams.setNonce(nonce);
            signParams.setGasLimit(gasLimit);
            signParams.setGasPrice(gasPrice);
            signParams.setValue(value);

            String dataHax = TransferEngine.engineIndemnity();
            signParams.setDataHex(dataHax);
            e.onNext(signParams);
            e.onComplete();
        }).flatMap((Function<SignParams, ObservableSource<BaseEntity>>) signParams -> {
            //String sign = transmitIndemnity(signParams);
            String sign = transmitSign(signParams);
            if (TextUtils.isEmpty(sign)) throw new ApiException("外部签名串错误");
            String authSign = "";
            String mContractAddress = transferIndemnityData.getLender_address();
            return RetrofitFactory.getInstance().requestChain(type, transferIndemnityData.getOrder_no(), transferIndemnityData.getLender_address(), mContractAddress, authSign, sign);
        }).compose(compose(this.bindToLifecycle()))
                .subscribe(new ProgressObserver<BaseEntity>(this) {
                    @Override
                    protected void onSuccess(BaseEntity entity) {
                        if (entity.isSuccess()) {
                            Intent intent = new Intent().putExtra("type", type).putExtra("order_no", transferIndemnityData.getOrder_no());
                            setResultFinish(intent);
                        } else {
                            Toast.makeText(TlWalletPayActivity.this, entity.getErrmsg(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void engineCancle(String type) {
        Observable.create((ObservableOnSubscribe<SignParams>) e -> {
            if (TextUtils.isEmpty(type) && transferCancleData == null && gaspriceNonce == null && base <= 0d)
                throw new ApiException("数据异常！");
            Address from = new Address(transferCancleData.getLoan_address());
            Address to = new Address(transferCancleData.getNew_address());
            BigInteger nonce = BigInteger.valueOf(Long.valueOf(gaspriceNonce.getNonce()));
            BigInteger gasLimit = new BigInteger(gaspriceNonce.getGas_price().getGas());
            BigInteger gasPrice = getGasPrice();
            BigInteger value = BigInteger.ZERO;

            SignParams signParams = new SignParams();
            signParams.setFrom(from);
            signParams.setTo(to);
            signParams.setNonce(nonce);
            signParams.setGasLimit(gasLimit);
            signParams.setGasPrice(gasPrice);
            signParams.setValue(value);

            String dataHax = TransferEngine.engineCancle();
            signParams.setDataHex(dataHax);
            e.onNext(signParams);
            e.onComplete();
        }).flatMap((Function<SignParams, ObservableSource<BaseEntity>>) signParams -> {
            //String sign = transmitCancle(signParams);
            String sign = transmitSign(signParams);
            if (TextUtils.isEmpty(sign)) throw new ApiException("外部签名串错误");
            String authSign = "";
            String mContractAddress = transferCancleData.getLoan_address();
            return RetrofitFactory.getInstance().requestChain(type, transferCancleData.getOrder_no(), transferCancleData.getLoan_address(), mContractAddress, authSign, sign);
        }).compose(compose(this.bindToLifecycle()))
                .subscribe(new ProgressObserver<BaseEntity>(this) {
                    @Override
                    protected void onSuccess(BaseEntity entity) {
                        if (entity.isSuccess()) {
                            Intent intent = new Intent().putExtra("type", type).putExtra("order_no", transferCancleData.getOrder_no());
                            setResultFinish(intent);
                        } else {
                            Toast.makeText(TlWalletPayActivity.this, entity.getErrmsg(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    public void setResultFinish(Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
    }

    protected abstract String transmitSign(@NonNull SignParams params);

}
