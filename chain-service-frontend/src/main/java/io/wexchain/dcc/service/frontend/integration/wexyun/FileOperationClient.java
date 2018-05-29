package io.wexchain.dcc.service.frontend.integration.wexyun;


import java.io.InputStream;

public interface FileOperationClient {

    InputStream download(String filePath);
}
