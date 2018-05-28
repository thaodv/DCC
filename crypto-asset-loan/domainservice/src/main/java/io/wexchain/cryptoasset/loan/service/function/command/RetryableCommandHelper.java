package io.wexchain.cryptoasset.loan.service.function.command;


import io.wexchain.cryptoasset.loan.common.constant.GeneralCommandStatus;
import io.wexchain.cryptoasset.loan.domain.RetryableCommand;

/**
 * RetryableCommandHelper
 *
 * @author zhengpeng
 */
public class RetryableCommandHelper {

    public static boolean isCreated(RetryableCommand cmd) {
        return GeneralCommandStatus.CREATED.name().equals(cmd.getStatus());
    }

    public static boolean isProcessing(RetryableCommand cmd) {
        return GeneralCommandStatus.PROCESSING.name().equals(cmd.getStatus());
    }

    public static boolean isSuccess(RetryableCommand cmd) {
        return GeneralCommandStatus.SUCCESS.name().equals(cmd.getStatus());
    }

    public static boolean isFailure(RetryableCommand cmd) {
        return GeneralCommandStatus.FAILURE.name().equals(cmd.getStatus());
    }


}
