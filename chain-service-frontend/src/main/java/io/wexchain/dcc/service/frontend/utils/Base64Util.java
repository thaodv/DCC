/**
 * 
 */
package io.wexchain.dcc.service.frontend.utils;


import org.apache.commons.io.IOUtils;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;


/**
 * <p>
 * </p>
 * 
 * @author yanyi
 */
public class Base64Util {


    public static String picFileToBase64(MultipartFile file){
        ByteArrayOutputStream outputStream =new ByteArrayOutputStream();
        try {
            IOUtils.copy(file.getInputStream(),outputStream);
            byte[] bytes = outputStream.toByteArray();
            return new String(Base64Utils.encode(bytes), "utf-8");
        } catch (Exception e) {
        }
        return null;
    }

}
