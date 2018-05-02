package io.wexchain.dcc.service.frontend.common.constants;


/**
 * <p>excel参数列表</p>
 * @author yanyi
 */
public class ExcelParamConstants {

    public static final String[] USER_SETTLEMENT_ORDER_HEADER = new String[]{
            "用户ID_#_6000",
            "时间_#_6000",
            "订单号_#_6000",
            "订单ID_#_6000",
            "金额(元)_#_6000",
            "类型_#_6000",
            "状态_#_6000",
    };
    public static final String[] USER_SETTLEMENT_ORDER_FIELD = new String[]{
            "userCode",
            "createdTime",
            "requestNo",
            "id",
            "amount",
            "type",
            "status",
    };
    public static final String[] EXCHANGE_SETTLEMENT_ORDER_HEADER = new String[]{
            "交易所简称_#_6000",
            "时间_#_6000",
            "订单号_#_6000",
            "订单ID_#_6000",
            "金额(元)_#_6000",
            "类型_#_6000",
            "状态_#_6000",
    };
    public static final String[] EXCHANGE_SETTLEMENT_ORDER_FIELD = new String[]{
            "exchangeName",
            "createdTime",
            "requestNo",
            "id",
            "amount",
            "type",
            "status",
    };

    public static final String[] MIGRATION_ORDER_HEADER = new String[]{
            "资金池编号_#_6000",
            "提交时间_#_6000",
            "订单号_#_6000",
            "调拨类型_#_6000",
            "金额(元)_#_6000",
            "状态_#_6000",
    };
    public static final String[] MIGRATION_ORDER_FIELD = new String[]{
            "fundPoolCode",
            "createdTime",
            "requestNo",
            "type",
            "amount",
            "status",
    };
}
