package io.wexchain.cryptoasset.loan.service.function.ufs;

import io.wexchain.cryptoasset.loan.api.model.UfsFile;
import io.wexchain.cryptoasset.loan.ext.integration.ufs.UfsClient;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

/**
 * UfsService
 *
 * @author zhengpeng
 */
@Service
public class UfsService {

    @Autowired
    private UfsClient ufsClient;

    public boolean validateUfsFile(UfsFile ufsFile) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ufsClient.downloadFile(bos, ufsFile.getFilePath());
        return checkDigest(bos.toByteArray(), ufsFile);
    }

    private boolean checkDigest(byte[] target, UfsFile ufsFile) {
        switch (ufsFile.getFileDigestAlg()) {
            case "SHA256":
                String d1 = DigestUtils.sha256Hex(target);
                return d1.equalsIgnoreCase(ufsFile.getFileDigest());
            default:
                return false;
        }
    }
}
