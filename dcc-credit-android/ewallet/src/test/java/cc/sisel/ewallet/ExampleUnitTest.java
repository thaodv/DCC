package cc.sisel.ewallet;

import org.junit.Test;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void ss(){

//        String oriStr = "0xe8b5e51f"; // data= 0.0000
//        String oriStr = "0x5af3107a4000"; // value = 0.0001
//        String oriStr = "0x16345785d8a0000"; // value = 0.1000
        String oriStr = "0x000000000000000000000000000000000000000000000000002386f26fc10000"; // value = 0.0010


        BigInteger bigInteger = Numeric.toBigInt(oriStr);
        String sss = new BigDecimal(bigInteger).scaleByPowerOfTen(-18).setScale(4, RoundingMode.DOWN).toPlainString();

        System.out.println(sss);
    

    }

}
