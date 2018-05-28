package io.wexchain.cryptoasset.loan.service.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

/**
 * AmountScaleUtil
 *
 * @author zhengpeng
 */
public class AmountScaleUtil {

    private static final BigDecimal WEXYUN_SCALE = new BigDecimal("100");
    private static final BigDecimal CAH_SCALE = new BigDecimal("10").pow(18);

    public static BigDecimal cal2Wexyun(BigDecimal source) {
        if (source == null) {
            return BigDecimal.ZERO;
        }
        return source.multiply(WEXYUN_SCALE).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal wexyun2Cal(BigDecimal source) {
        if (source == null) {
            return BigDecimal.ZERO;
        }
        return source.divide(WEXYUN_SCALE, 4, BigDecimal.ROUND_HALF_UP);
    }

    public static BigInteger cal2Cah(BigDecimal source) {
        if (source == null) {
            return BigInteger.ZERO;
        }
        return source.multiply(CAH_SCALE).toBigInteger();
    }

    public static BigDecimal cah2Cal(BigInteger source) {
        if (source == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(source).divide(CAH_SCALE, 4, BigDecimal.ROUND_HALF_UP);
    }

}
