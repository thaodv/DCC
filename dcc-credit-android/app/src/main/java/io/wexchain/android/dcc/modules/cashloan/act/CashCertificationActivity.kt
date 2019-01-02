package io.wexchain.android.dcc.modules.cashloan.act

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.zipWith
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.*
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.PassportOperations
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.domain.CertificationType
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.android.dcc.modules.cashloan.bean.CertificationInfo
import io.wexchain.android.dcc.modules.cashloan.vm.CashCertificationVm
import io.wexchain.android.dcc.modules.cert.BankCardCertificationActivity
import io.wexchain.android.dcc.modules.cert.IdCertificationActivity
import io.wexchain.android.dcc.modules.cert.SubmitBankCardActivity
import io.wexchain.android.dcc.modules.cert.SubmitIdActivity
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.tools.toBean
import io.wexchain.android.dcc.tools.toJson
import io.wexchain.android.dcc.vm.AuthenticationStatusVm
import io.wexchain.android.dcc.vm.domain.UserCertStatus
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityCashCertificationBinding
import io.wexchain.dccchainservice.CertApi
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.CashLoanRequest
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.util.ParamSignatureUtil
import io.wexchain.ipfs.utils.io_main
import worhavah.certs.bean.TNcert1newreport
import worhavah.tongniucertmodule.SubmitTNLogActivity
import worhavah.tongniucertmodule.TnLogCertificationActivity
import java.util.concurrent.TimeUnit

/**
 *Created by liuyang on 2018/10/15.
 */
class CashCertificationActivity : BindActivity<ActivityCashCertificationBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_cash_certification

    private val cmlog: String by lazy {
        "{\\\"data\\\":{\\\"task_id\\\":\\\"TASKYYS100000201811281915270661530397\\\",\\\"channel_src\\\":\\\"中国移动\\\",\\\"channel_attr\\\":\\\"江西\\\",\\\"cid\\\":\\\"362502199502025019\\\",\\\"name\\\":\\\"吴鑫鑫\\\",\\\"mobile\\\":\\\"18770153013\\\",\\\"created_time\\\":\\\"2018-11-28 19:15:27\\\",\\\"task_data\\\":{\\\"base_info\\\":{\\\"user_name\\\":\\\"**鑫\\\",\\\"user_sex\\\":\\\"未知\\\",\\\"user_number\\\":\\\"18770153013\\\",\\\"cert_num\\\":\\\"未知\\\",\\\"cert_addr\\\":\\\"江西省抚州市临川区上顿渡镇石鼓村吴杨组10号\\\",\\\"user_contact_no\\\":\\\"未知\\\",\\\"user_email\\\":\\\"未知\\\",\\\"post_code\\\":null},\\\"account_info\\\":{\\\"account_balance\\\":\\\"-2148\\\",\\\"current_fee\\\":\\\"2484\\\",\\\"credit_level\\\":\\\"2\\\",\\\"mobile_status\\\":\\\"正常\\\",\\\"net_time\\\":\\\"2015-06-02\\\",\\\"net_age\\\":\\\"41\\\",\\\"real_info\\\":\\\"已登记\\\",\\\"credit_point\\\":\\\"1036\\\",\\\"sim_card\\\":null,\\\"puk_code\\\":null,\\\"credit_effective_time\\\":null,\\\"credit_score\\\":null,\\\"land_level\\\":null,\\\"roam_state\\\":null,\\\"balance_available\\\":null,\\\"balance_unavailable\\\":null,\\\"prepay_available\\\":null,\\\"prom_available\\\":null,\\\"prepay_unavailable\\\":null,\\\"prom_unavailable\\\":null},\\\"package_info\\\":{\\\"brand_name\\\":\\\"动感地带\\\",\\\"pay_type\\\":null,\\\"package_detail\\\":[]},\\\"call_info\\\":[{\\\"total_call_time\\\":\\\"65\\\",\\\"total_call_count\\\":\\\"3\\\",\\\"total_fee\\\":\\\"0\\\",\\\"call_cycle\\\":\\\"2018-11\\\",\\\"call_record\\\":[{\\\"call_start_time\\\":\\\"2018-11-06 13:11:36\\\",\\\"call_address\\\":\\\"上海市.上海市\\\",\\\"call_type_name\\\":\\\"被叫\\\",\\\"call_other_number\\\":\\\"0085288321261\\\",\\\"call_time\\\":\\\"11\\\",\\\"call_land_type\\\":\\\"国内长途\\\",\\\"call_roam_cost\\\":null,\\\"call_long_distance\\\":null,\\\"call_discount\\\":null,\\\"call_cost\\\":\\\"0\\\"},{\\\"call_start_time\\\":\\\"2018-11-19 20:23:20\\\",\\\"call_address\\\":\\\"上海市.上海市\\\",\\\"call_type_name\\\":\\\"被叫\\\",\\\"call_other_number\\\":\\\"02161894054\\\",\\\"call_time\\\":\\\"33\\\",\\\"call_land_type\\\":\\\"国内长途\\\",\\\"call_roam_cost\\\":null,\\\"call_long_distance\\\":null,\\\"call_discount\\\":null,\\\"call_cost\\\":\\\"0\\\"},{\\\"call_start_time\\\":\\\"2018-11-22 11:00:17\\\",\\\"call_address\\\":\\\"上海市.上海市\\\",\\\"call_type_name\\\":\\\"被叫\\\",\\\"call_other_number\\\":\\\"15902108103\\\",\\\"call_time\\\":\\\"21\\\",\\\"call_land_type\\\":\\\"国内长途\\\",\\\"call_roam_cost\\\":null,\\\"call_long_distance\\\":null,\\\"call_discount\\\":null,\\\"call_cost\\\":\\\"0\\\"}]},{\\\"total_call_time\\\":\\\"268\\\",\\\"total_call_count\\\":\\\"6\\\",\\\"total_fee\\\":\\\"0\\\",\\\"call_cycle\\\":\\\"2018-10\\\",\\\"call_record\\\":[{\\\"call_start_time\\\":\\\"2018-10-01 12:40:20\\\",\\\"call_address\\\":\\\"上海市.上海市\\\",\\\"call_type_name\\\":\\\"主叫\\\",\\\"call_other_number\\\":\\\"18770948758\\\",\\\"call_time\\\":\\\"164\\\",\\\"call_land_type\\\":\\\"国内长途\\\",\\\"call_roam_cost\\\":null,\\\"call_long_distance\\\":null,\\\"call_discount\\\":null,\\\"call_cost\\\":\\\"0\\\"},{\\\"call_start_time\\\":\\\"2018-10-02 08:05:05\\\",\\\"call_address\\\":\\\"上海市.上海市\\\",\\\"call_type_name\\\":\\\"主叫\\\",\\\"call_other_number\\\":\\\"13918542575\\\",\\\"call_time\\\":\\\"57\\\",\\\"call_land_type\\\":\\\"国内长途\\\",\\\"call_roam_cost\\\":null,\\\"call_long_distance\\\":null,\\\"call_discount\\\":null,\\\"call_cost\\\":\\\"0\\\"},{\\\"call_start_time\\\":\\\"2018-10-04 10:31:17\\\",\\\"call_address\\\":\\\"上海市.上海市\\\",\\\"call_type_name\\\":\\\"被叫\\\",\\\"call_other_number\\\":\\\"10016\\\",\\\"call_time\\\":\\\"1\\\",\\\"call_land_type\\\":\\\"国内长途\\\",\\\"call_roam_cost\\\":null,\\\"call_long_distance\\\":null,\\\"call_discount\\\":null,\\\"call_cost\\\":\\\"0\\\"},{\\\"call_start_time\\\":\\\"2018-10-15 13:02:44\\\",\\\"call_address\\\":\\\"上海市.上海市\\\",\\\"call_type_name\\\":\\\"被叫\\\",\\\"call_other_number\\\":\\\"05532161624\\\",\\\"call_time\\\":\\\"10\\\",\\\"call_land_type\\\":\\\"国内长途\\\",\\\"call_roam_cost\\\":null,\\\"call_long_distance\\\":null,\\\"call_discount\\\":null,\\\"call_cost\\\":\\\"0\\\"},{\\\"call_start_time\\\":\\\"2018-10-16 11:38:42\\\",\\\"call_address\\\":\\\"上海市.上海市\\\",\\\"call_type_name\\\":\\\"被叫\\\",\\\"call_other_number\\\":\\\"18460460615\\\",\\\"call_time\\\":\\\"10\\\",\\\"call_land_type\\\":\\\"国内长途\\\",\\\"call_roam_cost\\\":null,\\\"call_long_distance\\\":null,\\\"call_discount\\\":null,\\\"call_cost\\\":\\\"0\\\"},{\\\"call_start_time\\\":\\\"2018-10-18 12:41:12\\\",\\\"call_address\\\":\\\"上海市.上海市\\\",\\\"call_type_name\\\":\\\"被叫\\\",\\\"call_other_number\\\":\\\"4001095555\\\",\\\"call_time\\\":\\\"26\\\",\\\"call_land_type\\\":\\\"国内长途\\\",\\\"call_roam_cost\\\":null,\\\"call_long_distance\\\":null,\\\"call_discount\\\":null,\\\"call_cost\\\":\\\"0\\\"}]},{\\\"total_call_time\\\":\\\"677\\\",\\\"total_call_count\\\":\\\"7\\\",\\\"total_fee\\\":\\\"0\\\",\\\"call_cycle\\\":\\\"2018-09\\\",\\\"call_record\\\":[{\\\"call_start_time\\\":\\\"2018-09-12 20:59:02\\\",\\\"call_address\\\":\\\"上海市.上海市\\\",\\\"call_type_name\\\":\\\"主叫\\\",\\\"call_other_number\\\":\\\"10086\\\",\\\"call_time\\\":\\\"107\\\",\\\"call_land_type\\\":\\\"国内长途\\\",\\\"call_roam_cost\\\":null,\\\"call_long_distance\\\":null,\\\"call_discount\\\":null,\\\"call_cost\\\":\\\"0\\\"},{\\\"call_start_time\\\":\\\"2018-09-18 15:51:20\\\",\\\"call_address\\\":\\\"上海市.上海市\\\",\\\"call_type_name\\\":\\\"被叫\\\",\\\"call_other_number\\\":\\\"079182099625\\\",\\\"call_time\\\":\\\"9\\\",\\\"call_land_type\\\":\\\"国内长途\\\",\\\"call_roam_cost\\\":null,\\\"call_long_distance\\\":null,\\\"call_discount\\\":null,\\\"call_cost\\\":\\\"0\\\"},{\\\"call_start_time\\\":\\\"2018-09-23 19:25:20\\\",\\\"call_address\\\":\\\"上海市.上海市\\\",\\\"call_type_name\\\":\\\"被叫\\\",\\\"call_other_number\\\":\\\"13120639487\\\",\\\"call_time\\\":\\\"142\\\",\\\"call_land_type\\\":\\\"国内长途\\\",\\\"call_roam_cost\\\":null,\\\"call_long_distance\\\":null,\\\"call_discount\\\":null,\\\"call_cost\\\":\\\"0\\\"},{\\\"call_start_time\\\":\\\"2018-09-26 09:04:15\\\",\\\"call_address\\\":\\\"上海市.上海市\\\",\\\"call_type_name\\\":\\\"被叫\\\",\\\"call_other_number\\\":\\\"10105555\\\",\\\"call_time\\\":\\\"281\\\",\\\"call_land_type\\\":\\\"国内长途\\\",\\\"call_roam_cost\\\":null,\\\"call_long_distance\\\":null,\\\"call_discount\\\":null,\\\"call_cost\\\":\\\"0\\\"},{\\\"call_start_time\\\":\\\"2018-09-28 19:41:36\\\",\\\"call_address\\\":\\\"上海市.上海市\\\",\\\"call_type_name\\\":\\\"主叫\\\",\\\"call_other_number\\\":\\\"18930393340\\\",\\\"call_time\\\":\\\"30\\\",\\\"call_land_type\\\":\\\"国内长途\\\",\\\"call_roam_cost\\\":null,\\\"call_long_distance\\\":null,\\\"call_discount\\\":null,\\\"call_cost\\\":\\\"0\\\"},{\\\"call_start_time\\\":\\\"2018-09-28 20:23:10\\\",\\\"call_address\\\":\\\"上海市.上海市\\\",\\\"call_type_name\\\":\\\"被叫\\\",\\\"call_other_number\\\":\\\"18930393340\\\",\\\"call_time\\\":\\\"99\\\",\\\"call_land_type\\\":\\\"国内长途\\\",\\\"call_roam_cost\\\":null,\\\"call_long_distance\\\":null,\\\"call_discount\\\":null,\\\"call_cost\\\":\\\"0\\\"},{\\\"call_start_time\\\":\\\"2018-09-29 18:22:05\\\",\\\"call_address\\\":\\\"上海市.上海市\\\",\\\"call_type_name\\\":\\\"被叫\\\",\\\"call_other_number\\\":\\\"95774439\\\",\\\"call_time\\\":\\\"9\\\",\\\"call_land_type\\\":\\\"国内长途\\\",\\\"call_roam_cost\\\":null,\\\"call_long_distance\\\":null,\\\"call_discount\\\":null,\\\"call_cost\\\":\\\"0\\\"}]},{\\\"total_call_time\\\":\\\"87\\\",\\\"total_call_count\\\":\\\"3\\\",\\\"total_fee\\\":\\\"0\\\",\\\"call_cycle\\\":\\\"2018-07\\\",\\\"call_record\\\":[{\\\"call_start_time\\\":\\\"2018-07-07 10:16:02\\\",\\\"call_address\\\":\\\"上海市.上海市\\\",\\\"call_type_name\\\":\\\"被叫\\\",\\\"call_other_number\\\":\\\"0017147073350\\\",\\\"call_time\\\":\\\"29\\\",\\\"call_land_type\\\":\\\"国内长途\\\",\\\"call_roam_cost\\\":null,\\\"call_long_distance\\\":null,\\\"call_discount\\\":null,\\\"call_cost\\\":\\\"0\\\"},{\\\"call_start_time\\\":\\\"2018-07-09 15:49:40\\\",\\\"call_address\\\":\\\"上海市.上海市\\\",\\\"call_type_name\\\":\\\"被叫\\\",\\\"call_other_number\\\":\\\"02160421900\\\",\\\"call_time\\\":\\\"46\\\",\\\"call_land_type\\\":\\\"国内长途\\\",\\\"call_roam_cost\\\":null,\\\"call_long_distance\\\":null,\\\"call_discount\\\":null,\\\"call_cost\\\":\\\"0\\\"},{\\\"call_start_time\\\":\\\"2018-07-11 12:52:04\\\",\\\"call_address\\\":\\\"上海市.上海市\\\",\\\"call_type_name\\\":\\\"被叫\\\",\\\"call_other_number\\\":\\\"125909888218\\\",\\\"call_time\\\":\\\"12\\\",\\\"call_land_type\\\":\\\"国内长途\\\",\\\"call_roam_cost\\\":null,\\\"call_long_distance\\\":null,\\\"call_discount\\\":null,\\\"call_cost\\\":\\\"0\\\"}]}],\\\"sms_info\\\":[{\\\"total_msg_cost\\\":\\\"0\\\",\\\"total_msg_count\\\":\\\"2\\\",\\\"msg_cycle\\\":\\\"2018-11\\\",\\\"sms_record\\\":[{\\\"msg_start_time\\\":\\\"2018-11-22 21:12:55\\\",\\\"msg_type\\\":\\\"发送\\\",\\\"msg_other_num\\\":\\\"10086005\\\",\\\"msg_channel\\\":\\\"短信\\\",\\\"msg_biz_name\\\":null,\\\"msg_address\\\":\\\"江西省.宜春市\\\",\\\"msg_fee\\\":null,\\\"msg_discount\\\":null,\\\"msg_cost\\\":\\\"0\\\",\\\"msg_remark\\\":null},{\\\"msg_start_time\\\":\\\"2018-11-22 21:13:19\\\",\\\"msg_type\\\":\\\"发送\\\",\\\"msg_other_num\\\":\\\"10086004\\\",\\\"msg_channel\\\":\\\"短信\\\",\\\"msg_biz_name\\\":null,\\\"msg_address\\\":\\\"江西省.宜春市\\\",\\\"msg_fee\\\":null,\\\"msg_discount\\\":null,\\\"msg_cost\\\":\\\"0\\\",\\\"msg_remark\\\":null}]},{\\\"total_msg_cost\\\":\\\"0\\\",\\\"total_msg_count\\\":\\\"6\\\",\\\"msg_cycle\\\":\\\"2018-09\\\",\\\"sms_record\\\":[{\\\"msg_start_time\\\":\\\"2018-09-12 16:26:11\\\",\\\"msg_type\\\":\\\"发送\\\",\\\"msg_other_num\\\":\\\"10086001\\\",\\\"msg_channel\\\":\\\"短信\\\",\\\"msg_biz_name\\\":null,\\\"msg_address\\\":\\\"江西省.宜春市\\\",\\\"msg_fee\\\":null,\\\"msg_discount\\\":null,\\\"msg_cost\\\":\\\"0\\\",\\\"msg_remark\\\":null},{\\\"msg_start_time\\\":\\\"2018-09-12 16:27:34\\\",\\\"msg_type\\\":\\\"发送\\\",\\\"msg_other_num\\\":\\\"10086001\\\",\\\"msg_channel\\\":\\\"短信\\\",\\\"msg_biz_name\\\":null,\\\"msg_address\\\":\\\"江西省.宜春市\\\",\\\"msg_fee\\\":null,\\\"msg_discount\\\":null,\\\"msg_cost\\\":\\\"0\\\",\\\"msg_remark\\\":null},{\\\"msg_start_time\\\":\\\"2018-09-12 16:27:49\\\",\\\"msg_type\\\":\\\"发送\\\",\\\"msg_other_num\\\":\\\"10086002\\\",\\\"msg_channel\\\":\\\"短信\\\",\\\"msg_biz_name\\\":null,\\\"msg_address\\\":\\\"江西省.宜春市\\\",\\\"msg_fee\\\":null,\\\"msg_discount\\\":null,\\\"msg_cost\\\":\\\"0\\\",\\\"msg_remark\\\":null},{\\\"msg_start_time\\\":\\\"2018-09-12 16:28:34\\\",\\\"msg_type\\\":\\\"发送\\\",\\\"msg_other_num\\\":\\\"10086007\\\",\\\"msg_channel\\\":\\\"短信\\\",\\\"msg_biz_name\\\":null,\\\"msg_address\\\":\\\"江西省.宜春市\\\",\\\"msg_fee\\\":null,\\\"msg_discount\\\":null,\\\"msg_cost\\\":\\\"0\\\",\\\"msg_remark\\\":null},{\\\"msg_start_time\\\":\\\"2018-09-12 16:29:24\\\",\\\"msg_type\\\":\\\"发送\\\",\\\"msg_other_num\\\":\\\"10086004\\\",\\\"msg_channel\\\":\\\"短信\\\",\\\"msg_biz_name\\\":null,\\\"msg_address\\\":\\\"江西省.宜春市\\\",\\\"msg_fee\\\":null,\\\"msg_discount\\\":null,\\\"msg_cost\\\":\\\"0\\\",\\\"msg_remark\\\":null},{\\\"msg_start_time\\\":\\\"2018-09-12 16:29:49\\\",\\\"msg_type\\\":\\\"发送\\\",\\\"msg_other_num\\\":\\\"10086003\\\",\\\"msg_channel\\\":\\\"短信\\\",\\\"msg_biz_name\\\":null,\\\"msg_address\\\":\\\"江西省.宜春市\\\",\\\"msg_fee\\\":null,\\\"msg_discount\\\":null,\\\"msg_cost\\\":\\\"0\\\",\\\"msg_remark\\\":null}]}],\\\"payment_info\\\":[{\\\"pay_date\\\":\\\"2018-09-01\\\",\\\"pay_fee\\\":\\\"9980\\\",\\\"pay_channel\\\":\\\"其他\\\",\\\"pay_type\\\":\\\"其他充值\\\"},{\\\"pay_date\\\":\\\"2018-09-01\\\",\\\"pay_fee\\\":\\\"20\\\",\\\"pay_channel\\\":\\\"其他\\\",\\\"pay_type\\\":\\\"其他充值\\\"},{\\\"pay_date\\\":\\\"2018-08-12\\\",\\\"pay_fee\\\":\\\"1000\\\",\\\"pay_channel\\\":\\\"其他\\\",\\\"pay_type\\\":\\\"其他充值\\\"}],\\\"bill_info\\\":[{\\\"bill_cycle\\\":\\\"2018-11\\\",\\\"bill_fee\\\":\\\"2484\\\",\\\"bill_discount\\\":null,\\\"bill_total\\\":\\\"2484\\\",\\\"breach_amount\\\":null,\\\"paid_amount\\\":null,\\\"unpaid_amount\\\":null,\\\"bill_record\\\":[{\\\"fee_name\\\":\\\"包月使用费\\\",\\\"fee_category\\\":\\\"固定费用\\\",\\\"fee_amount\\\":\\\"2484\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"语音通信费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"上网费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"短彩信\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"增值业务费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"代收费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"其它费用\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null}],\\\"usage_detail\\\":[]},{\\\"bill_cycle\\\":\\\"2018-10\\\",\\\"bill_fee\\\":\\\"2852\\\",\\\"bill_discount\\\":null,\\\"bill_total\\\":\\\"2852\\\",\\\"breach_amount\\\":null,\\\"paid_amount\\\":null,\\\"unpaid_amount\\\":null,\\\"bill_record\\\":[{\\\"fee_name\\\":\\\"包月使用费\\\",\\\"fee_category\\\":\\\"固定费用\\\",\\\"fee_amount\\\":\\\"2852\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"语音通信费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"上网费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"短彩信\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"增值业务费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"代收费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"其它费用\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null}],\\\"usage_detail\\\":[]},{\\\"bill_cycle\\\":\\\"2018-09\\\",\\\"bill_fee\\\":\\\"2760\\\",\\\"bill_discount\\\":null,\\\"bill_total\\\":\\\"2760\\\",\\\"breach_amount\\\":null,\\\"paid_amount\\\":null,\\\"unpaid_amount\\\":null,\\\"bill_record\\\":[{\\\"fee_name\\\":\\\"包月使用费\\\",\\\"fee_category\\\":\\\"固定费用\\\",\\\"fee_amount\\\":\\\"2760\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"语音通信费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"上网费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"短彩信\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"增值业务费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"代收费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"其它费用\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null}],\\\"usage_detail\\\":[]},{\\\"bill_cycle\\\":\\\"2018-08\\\",\\\"bill_fee\\\":\\\"0\\\",\\\"bill_discount\\\":null,\\\"bill_total\\\":\\\"0\\\",\\\"breach_amount\\\":null,\\\"paid_amount\\\":null,\\\"unpaid_amount\\\":null,\\\"bill_record\\\":[{\\\"fee_name\\\":\\\"固定费用\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"语音通信费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"上网费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"短彩信\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"增值业务费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"代收费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"其它费用\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null}],\\\"usage_detail\\\":[]},{\\\"bill_cycle\\\":\\\"2018-07\\\",\\\"bill_fee\\\":\\\"2852\\\",\\\"bill_discount\\\":null,\\\"bill_total\\\":\\\"2852\\\",\\\"breach_amount\\\":null,\\\"paid_amount\\\":null,\\\"unpaid_amount\\\":null,\\\"bill_record\\\":[{\\\"fee_name\\\":\\\"包月使用费\\\",\\\"fee_category\\\":\\\"固定费用\\\",\\\"fee_amount\\\":\\\"2852\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"语音通信费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"上网费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"短彩信\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"增值业务费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"代收费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"其它费用\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null}],\\\"usage_detail\\\":[]},{\\\"bill_cycle\\\":\\\"2018-06\\\",\\\"bill_fee\\\":\\\"2760\\\",\\\"bill_discount\\\":null,\\\"bill_total\\\":\\\"2760\\\",\\\"breach_amount\\\":null,\\\"paid_amount\\\":null,\\\"unpaid_amount\\\":null,\\\"bill_record\\\":[{\\\"fee_name\\\":\\\"包月使用费\\\",\\\"fee_category\\\":\\\"固定费用\\\",\\\"fee_amount\\\":\\\"2760\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"语音通信费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"上网费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"短彩信\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"增值业务费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"代收费\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":\\\"其它费用\\\",\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null},{\\\"fee_name\\\":null,\\\"fee_category\\\":null,\\\"fee_amount\\\":\\\"0\\\",\\\"fee_number\\\":null}],\\\"usage_detail\\\":[]}],\\\"point_info\\\":{\\\"point_record\\\":[],\\\"point_detail\\\":[]},\\\"family_info\\\":[]},\\\"lost_data\\\":null},\\\"createTime\\\":1543891351418,\\\"query\\\":{\\\"account\\\":\\\"dcc_cash\\\",\\\"task_id\\\":\\\"TASKYYS100000201811281915270661530397\\\",\\\"sign\\\":null},\\\"status\\\":{\\\"subCode\\\":\\\"0\\\",\\\"src\\\":\\\"tongdun\\\",\\\"code\\\":\\\"0200\\\",\\\"msg\\\":\\\"成功\\\",\\\"processExeStates\\\":[{\\\"srcName\\\":\\\"tongdun\\\",\\\"success\\\":1,\\\"failure\\\":0,\\\"details\\\":[\\\"code:0200,subCode:0,msg:成功\\\"]}]}}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
    }

    override fun onResume() {
        super.onResume()
        initVm()
        refreshCertStatus()
        initClick()
    }

    var aa = 7

    private fun refreshCertStatus() {
        binding.asTongniuVm?.let {
            if (it.status.get() == UserCertStatus.INCOMPLETE) {
                val passport = App.get().passportRepository.getCurrentPassport()!!
                getTNLogReport(passport)
                        .zipWith(Single.timer(1, TimeUnit.SECONDS))
                        .flatMap {
                            if (null != it) {
                                if (it.first.endorserOrder.status.contains("COMPELETED")) {
                                    worhavah.certs.tools.CertOperations.onTNLogSuccessGot(it.first.tongniuData.toString())
                                    worhavah.certs.tools.CertOperations.certed()
                                    worhavah.certs.tools.CertOperations.clearTNCertCache()
                                    initVm()
                                } else if (it.first.endorserOrder.status.contains("INVALID")) {
                                    toast("获取报告失败")
                                    worhavah.certs.tools.CertOperations.certPrefs.certTNLogState.set(
                                            worhavah.certs.tools.UserCertStatus.NONE.name)
                                    worhavah.certs.tools.CertOperations.clearTNCertCache()
                                    aa = 0
                                    initVm()
                                }
                            }
                            App.get().chainGateway.getCertData(passport.address, ChainGateway.TN_COMMUNICATION_LOG).check()
                        }
                        .doFinally {
                            aa += -1
                            if (aa > 0) {
                                refreshCertStatus()
                            }
                        }
                        .io_main()
                        .subscribeBy(
                                onSuccess = {
                                    val content = it.content
                                    if (0L != content.expired) {
                                        worhavah.certs.tools.CertOperations.saveTnLogCertExpired(content.expired)
                                    }
                                },
                                onError = {
                                    toast("获取报告失败")
                                    worhavah.certs.tools.CertOperations.certPrefs.certTNLogState.set(
                                            worhavah.certs.tools.UserCertStatus.NONE.name)
                                    worhavah.certs.tools.CertOperations.clearTNCertCache()
                                    aa = 0
                                    initVm()
                                })
            }
        }
    }

    private fun getTNLogReport(passport: Passport): Single<TNcert1newreport> {
        require(passport.authKey != null)
        val address = passport.address
        val privateKey = passport.authKey!!.getPrivateKey()
        val orderId = worhavah.certs.tools.CertOperations.certPrefs.certTNLogOrderId.get()

        return worhavah.certs.tools.CertOperations.tnCertApi.TNgetReport(
                address = address,
                orderId = orderId,

                signature = ParamSignatureUtil.sign(
                        privateKey, mapOf(
                        "address" to address,
                        "orderId" to orderId.toString()))
        ).compose(Result.checked())
    }

    private fun initClick() {
        binding.cert!!.apply {
            userinfoCall.observe(this@CashCertificationActivity, Observer {
                navigateTo(UserInfoCertificationActivity::class.java)
            })
            tipsCall.observe(this@CashCertificationActivity, Observer {
                toast("DCC不足?")
            })
            commitCall.observe(this@CashCertificationActivity, Observer {
                doApply()
            })
        }
    }

    private fun getRelationIndex(relation: String): String {
        return (UserInfoCertificationActivity.RelationList.indexOf(relation) + 1).toString()
    }

    private fun doApply() {
        val tnCertNonce = worhavah.certs.tools.CertOperations.getTnCertNonce()
        if (tnCertNonce == null) {
            toast("TnNonce is null ")
            return
        }
        val tnCmData = worhavah.certs.tools.CertOperations.getTnLogPhoneNo()!!
        val idData = CertOperations.getCertIdData()!!

        ScfOperations
                .withScfTokenInCurrentPassport("") {
                    App.get().scfApi.mobileNumber(it, tnCmData, idData.id, idData.name, tnCertNonce)
                }
                .flatMap {
                    ScfOperations
                            .withScfTokenInCurrentPassport {
                                App.get().scfApi.createLoanOrder(it)
                            }
                }
                .map {
                    val certIdPics = CertOperations.getCertIdPics()!!
                    val bankData = CertOperations.getCertBankCardData()!!
                    val tnCmLog = worhavah.certs.tools.CertOperations.certPrefs.certTNLogData.get()!!
                    val infoCert = App.get().passportRepository.getUserInfoCert()!!
                    val certInfo = infoCert.toBean(CertificationInfo::class.java)

                    val list = mutableListOf<CashLoanRequest.ExtraPersonalInfo.ContactInfo>()
                    val contact1 = CashLoanRequest.ExtraPersonalInfo.ContactInfo(getRelationIndex(certInfo.Contacts1Relation!!), certInfo.Contacts1Name!!, certInfo.Contacts1Phone!!)
                    val contact2 = CashLoanRequest.ExtraPersonalInfo.ContactInfo(getRelationIndex(certInfo.Contacts2Relation!!), certInfo.Contacts2Name!!, certInfo.Contacts2Phone!!)
                    val contact3 = CashLoanRequest.ExtraPersonalInfo.ContactInfo(getRelationIndex(certInfo.Contacts3Relation!!), certInfo.Contacts3Name!!, certInfo.Contacts3Phone!!)
                    list.add(contact1)
                    list.add(contact2)
                    list.add(contact3)

                    val index = CashLoanRequest.Index(it.id)
                    val idInfo = CashLoanRequest.IdCertInfo(idData.name, idData.id)
                    val bankInfo = CashLoanRequest.BankCardCertInfo(bankData.bankCardNo, bankData.phoneNo)
                    val tnCmInfo = CashLoanRequest.CommunicationLogCertInfo(tnCmData, tnCmLog/*cmlog*/.replace("\\", ""))
                    val extraInfo = CashLoanRequest.ExtraPersonalInfo(
                            maritalStatus = (UserInfoCertificationActivity.MarriageList.indexOf(certInfo.MarriageStatus) + 1).toString(),
                            residentialProvince = certInfo.ResideProvince!!,
                            residentialCity = certInfo.ResideCity!!,
                            residentialDistrict = certInfo.ResideArea!!,
                            residentialAddress = certInfo.ResideAddress!!,
                            loanUsage = (UserInfoCertificationActivity.LoanPurposeList.indexOf(certInfo.LoanPurpose!!) + 1).toString(),
                            workingType = (UserInfoCertificationActivity.WorkCategoryList.indexOf(certInfo.WorkCategory!!) + 1).toString(),
                            workingIndustry = (UserInfoCertificationActivity.WorkIndustryList.indexOf(certInfo.WorkIndustry!!) + 1).toString(),
                            workingYears = certInfo.WorkYear!!,
                            companyProvince = certInfo.CompanyProvince!!,
                            companyCity = certInfo.CompanyCity!!,
                            companyDistrict = certInfo.CompanyArea!!,
                            companyAddress = certInfo.CompanyAddress!!,
                            companyName = certInfo.CompanyName!!,
                            companyTel = certInfo.CompanyPhone,
                            contactInfoList = list
                    )
                    val request = CashLoanRequest(
                            index = index,
                            idCertInfo = idInfo,
                            bankCardCertInfo = bankInfo,
                            communicationLogCertInfo = tnCmInfo,
                            extraPersonalInfo = extraInfo)

                    certIdPics to request
                }
                .flatMap {
                    val pic = it.first
                    val data = it.second
                    ScfOperations.withScfTokenInCurrentPassport {
                        App.get().scfApi.tnApply(
                                token = it,
                                data = data.toJson(),
                                idCardFrontPic = CertApi.uploadFilePart(pic.first, "idCardFrontPic.jpg", "image/jpeg", "idCardFrontPic"),
                                idCardBackPic = CertApi.uploadFilePart(pic.second, "idCardBackPic.jpg", "image/jpeg", "idCardBackPic"),
                                facePic = CertApi.uploadFilePart(pic.third, "facePic.jpg", "image/jpeg", "facePic")
                        )
                    }
                }
                .io_main()
                .doOnError {
                    if (it is DccChainServiceException) {
                        toast(it.message ?: "系统错误")
                    } else {
                        toast("系统错误")
                    }
                }
                .withLoading()
                .subscribeBy {
                    finish()
                }
    }

    private fun initVm() {
        binding.asIdVm = obtainAuthStatus(CertificationType.ID)
        binding.asBankVm = obtainAuthStatus(CertificationType.BANK)
        binding.asTongniuVm = obtainAuthStatus(CertificationType.TONGNIU)
        binding.cert = getViewModel<CashCertificationVm>()
                .apply {
                    val idCertPassed = CertOperations.isIdCertPassed()
                    val bankCertPassed = CertOperations.isBankCertPassed()
                    val tnstatus = worhavah.certs.tools.CertOperations.getTNLogUserStatus()
                    val tnCmStatus = tnstatus == worhavah.certs.tools.UserCertStatus.DONE
                    val certStatus = getCertStatus()
                    val status = idCertPassed && bankCertPassed && tnCmStatus && certStatus
                    btnStatus.set(status)
                    isCert.set(certStatus)
                }
        binding.vm = getViewModel()
    }

    private fun getCertStatus(): Boolean {
        val infoCert = App.get().passportRepository.getUserInfoCert()
        return infoCert?.let {
            val data = it.toBean(CertificationInfo::class.java)
            data.isCert()
        } ?: false

    }

    private fun obtainAuthStatus(certificationType: CertificationType): AuthenticationStatusVm? {
        return ViewModelProviders.of(this)[certificationType.name, AuthenticationStatusVm::class.java]
                .apply {
                    title.set(getCertTypeTitle(certificationType))
                    authDetail.set(getDescription(certificationType))
                    this.status.set(CertOperations.getCertStatus(certificationType))
                    this.certificationType.set(certificationType)
                    this.performOperationEvent.observe(this@CashCertificationActivity, Observer {
                        it?.let {
                            val type = it.first
                            val certStatus = it.second
                            if (type != null && certStatus != null) {
                                this@CashCertificationActivity.performOperation(type, certStatus)
                            }
                        }
                    })
                }
    }


    private fun getCertTypeTitle(certificationType: CertificationType): String {
        return when (certificationType) {
            CertificationType.ID -> getString(R.string.id_verification)
            CertificationType.BANK -> getString(R.string.bank_account_verification)
            CertificationType.TONGNIU -> "同牛运营商认证"
            else -> ""
        }
    }

    private fun getDescription(certificationType: CertificationType): String {
        return when (certificationType) {
            CertificationType.ID -> getString(R.string.verify_your_legal_documentation)
            CertificationType.BANK -> getString(R.string.for_quick_approvalto_improve)
            CertificationType.TONGNIU -> "用于现金借贷审核"
            else -> ""
        }
    }

    private fun performOperation(certificationType: CertificationType, status: UserCertStatus) {
        when (certificationType) {
            CertificationType.ID -> {
                if (status == UserCertStatus.DONE) {
                    navigateTo(IdCertificationActivity::class.java) {
                        putExtra("type", CERT_TYPE_CASHLOAN)
                    }
                } else {
                    navigateTo(SubmitIdActivity::class.java) {
                        putExtra("type", CERT_TYPE_CASHLOAN)
                    }
                }
            }

            CertificationType.BANK ->
                if (status == UserCertStatus.DONE) {
                    navigateTo(BankCardCertificationActivity::class.java) {
                        putExtra("type", CERT_TYPE_CASHLOAN)
                    }
                } else {
                    PassportOperations.ensureCaValidity(this) {
                        navigateTo(SubmitBankCardActivity::class.java) {
                            putExtra("type", CERT_TYPE_CASHLOAN)
                        }
                    }
                }
            CertificationType.TONGNIU -> {
                when (status) {
                    UserCertStatus.DONE, UserCertStatus.TIMEOUT -> {
                        // navigateTo(TnLogCertificationActivity::class.java)
                        startActivity(Intent(this, TnLogCertificationActivity::class.java))
                    }
                    UserCertStatus.NONE -> {
                        PassportOperations.ensureCaValidity(this) {
                            startActivity(Intent(this, SubmitTNLogActivity::class.java))
                        }
                    }
                    UserCertStatus.INCOMPLETE -> {

                    }
                    else -> {

                    }
                }

            }
            else -> {
            }
        }
    }

    companion object {
        const val CERT_TYPE_CASHLOAN = "cert_type_cashloan"
    }


}
