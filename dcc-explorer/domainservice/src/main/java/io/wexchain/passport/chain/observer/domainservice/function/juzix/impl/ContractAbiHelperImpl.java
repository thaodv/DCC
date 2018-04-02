
package io.wexchain.passport.chain.observer.domainservice.function.juzix.impl;

import io.wexchain.passport.chain.observer.domainservice.function.juzix.ContractAbiHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * ContractAbiHelperImpl
 *
 * @author zhengpeng
 */


public class ContractAbiHelperImpl implements ContractAbiHelper {

    private Map<String, String> abiMap = new HashMap<>();

    private Resource[] abiFiles;

    private String certServiceAddress

    public void init() {
        if (CollectionUtils.isNotEmpty(contractAbiPathList)) {
            for (String abiPath : contractAbiPathList) {
                try {
                    File abiFile = new File(abiPath);
                    String abi = FileUtils.readFileToString(abiFile, "UTF-8");
                    String name = abiFile.getName();
                    contractAbiMap.put(name.substring(4, name.lastIndexOf(".")), abi);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getAbi(String contractAddress) {
        return abiMap.get(contractAddress);
    }
}

