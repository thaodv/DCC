package io.wexchain.cryptoasset.loan.common.converter;

import org.dozer.DozerConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * BigDecimalConverter
 *
 * @author zhengpeng
 */
public class BigDecimalConverter extends DozerConverter<BigDecimal, BigDecimal> {

    public BigDecimalConverter() {
        super(BigDecimal.class, BigDecimal.class);
    }

    @Override
    public BigDecimal convertTo(BigDecimal source, BigDecimal destination) {
        return new BigDecimal(source.setScale(2, RoundingMode.DOWN).toString());
    }

    @Override
    public BigDecimal convertFrom(BigDecimal source, BigDecimal destination) {
        return new BigDecimal(source.setScale(2, RoundingMode.DOWN).toString());
    }
}
