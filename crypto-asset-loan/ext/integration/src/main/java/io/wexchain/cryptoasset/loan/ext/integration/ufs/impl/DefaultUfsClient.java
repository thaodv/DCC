package io.wexchain.cryptoasset.loan.ext.integration.ufs.impl;

import com.weihui.basic.lang.common.domain.BaseResult;
import com.weihui.ufs.client.UFSClient;
import com.weihui.ufs.client.ctx.FileContext;
import com.weihui.ufs.client.ctx.FileFileContext;
import com.weihui.ufs.client.ctx.InputStreamFileContext;
import com.weihui.ufs.client.ctx.OutputStreamFileContext;
import com.weihui.ufs.client.domain.FileNameInfo;
import com.weihui.ufs.client.exception.CallFailException;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import io.wexchain.cryptoasset.loan.api.constant.CalErrorCode;
import io.wexchain.cryptoasset.loan.common.function.ThrowingFunction;
import io.wexchain.cryptoasset.loan.ext.integration.ufs.UfsClient;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

/**
 * DefaultUfsClient
 *
 * @author zhengpeng
 */
public class DefaultUfsClient implements UfsClient {

	private static final Logger logger = LoggerFactory.getLogger(DefaultUfsClient.class);

	/**
	 * ufs客户端
	 */
	private UFSClient ufsClient;

	/**
	 * 临时保存路径
	 */
	private String tmpFilePath;

	/**
	 * ufs文件存储路径
	 */
	private String ufsFilePath;

	@Override
	public BaseResult downloadFile(OutputStream outputStream, String fullName) {
		try {
			OutputStreamFileContext context = new OutputStreamFileContext("/", fullName, outputStream);
			return new BaseResult(ufsClient.getFile(context));
		} catch (Exception e) {
			logger.error("下载文件异常[{}]", fullName);
			return new BaseResult(false, e.getMessage());
		}
	}

	@Override
	public BaseResult uploadFile(final InputStream ins, final String ufsFilePath, final long length) {
		final InputStreamFileContext fileContext = new InputStreamFileContext(ufsFilePath, ins, length);
		return internalUploadFile((Void v) -> ufsClient.putFile(fileContext), fileContext);
	}

	@Override
	public BaseResult uploadFile(MultipartFile file, String ufsFilePath) {
		try {
			return uploadFile(file.getInputStream(), ufsFilePath, file.getSize());
		} catch (IOException e) {
			throw new ErrorCodeException(CalErrorCode.UPLOAD_FILE_FAIL.name(),
					CalErrorCode.UPLOAD_FILE_FAIL.getDescription());
		}
	}

	@Override
	public BaseResult uploadFile(final File file, final String ufsFilePath) {
		final FileFileContext fileContext = new FileFileContext(ufsFilePath, file);
		return internalUploadFile((Void v) -> ufsClient.putFile(fileContext), fileContext);
	}

	private BaseResult internalUploadFile(final ThrowingFunction<Void, Boolean> invoker,
			final FileContext fileContext) {

		BaseResult uploadResult = new BaseResult(true);
		try {
			// 检查目录
			List<FileNameInfo> reconFilelist = ufsClient.list(ufsFilePath);
			// 如不存在新建
			if (null == reconFilelist || reconFilelist.isEmpty()) {
				ufsClient.mkdir(ufsFilePath);
			}
			// 先删除后上传
			ufsClient.removeFile(fileContext);
			// 上传文件至ufs
			Assert.isTrue(invoker.apply(null), "上传文件错误");
		} catch (Exception e) {
			logger.error(String.format("上传文件:%s 异常", fileContext.getFileName()), e);
			uploadResult.setSuccess(false);
		}
		return uploadResult;
	}

	@Override
	public void writeFile(MultipartFile file, String pathname) throws IOException, FileNotFoundException {
		BufferedOutputStream stream = null;
		try {
			File tempFile = new File(pathname);
			if (tempFile.exists()) {
				tempFile.delete();
			}
			byte[] bytes = file.getBytes();
			stream = new BufferedOutputStream(new FileOutputStream(tempFile));
			stream.write(bytes);
		} finally {
			if (stream != null)
				stream.close();
		}

	}

	public List<FileNameInfo> list(String path) {
		try {
			return ufsClient.list(path);
		} catch (CallFailException e) {
			throw new ContextedRuntimeException(e);
		}
	}

	@Override
	public String getTmpFilePath() {
		return this.tmpFilePath;
	}

	@Override
	public String getUfsFilePath() {
		return this.ufsFilePath;
	}

	public void setTmpFilePath(String tmpFilePath) {
		this.tmpFilePath = tmpFilePath;
	}

	public void setUfsFilePath(String ufsFilePath) {
		this.ufsFilePath = ufsFilePath;
	}

	public UFSClient getUfsClient() {
		return ufsClient;
	}

	public void setUfsClient(UFSClient ufsClient) {
		this.ufsClient = ufsClient;
	}

}
