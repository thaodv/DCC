package io.wexchain.cryptoasset.loan.ext.integration.ufs;

import com.weihui.basic.lang.common.domain.BaseResult;
import com.weihui.ufs.client.domain.FileNameInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

public interface UfsClient {

    /**
     * 上传文件至UFS
     */
    BaseResult uploadFile(File file, String ufsFilePath);

    /**
     * 流式上传至UFS
     */
    BaseResult uploadFile(InputStream ins, String ufsFilePath, long length);

    /**
     * 流式上传至UFS
     */
    BaseResult uploadFile(MultipartFile file, String ufsFilePath);

    /**
     * 获取可配置的服务器临时文件保存路径
     */
    String getTmpFilePath();

    /**
     * 文件UFS存储路径
     */
    String getUfsFilePath();

    /**
     * 下载文件
     */
    BaseResult downloadFile(OutputStream outputStream, String fullName);

    /**
     * 临时保存文件至服务器，以便后续上传UFS
     */
    void writeFile(MultipartFile file, String pathname) throws IOException, FileNotFoundException;

	List<FileNameInfo> list(String path);

}
