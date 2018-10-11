package io.wexchain.cryptoasset.loan.service.processor.order.auditing.advancer.apply;

import com.wexyun.open.api.enums.UploadFileType;
import io.wexchain.cryptoasset.loan.domain.AuditingOrder;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.domain.UnretryableCommand;
import io.wexchain.cryptoasset.loan.ext.integration.ufs.UfsClient;
import io.wexchain.cryptoasset.loan.service.function.command.CommandIndex;
import io.wexchain.cryptoasset.loan.service.function.command.UnretryableCommandFunction;
import io.wexchain.cryptoasset.loan.service.function.wexyun.WexyunLoanClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.function.Function;


public class UploadMedia implements Function<LoanOrder, UploadResult> {

	@Autowired
	private UnretryableCommandFunction unretryableCommandFunction;

	@Autowired
	private WexyunLoanClient wexyunLoanClient;

	@Autowired
	private UfsClient ufsClient;

	/**
	 * 认证项
	 */
	private String paramKey;

	/**
	 * 云金融材料key
	 */
	private String wexyunMaterialKey;

	@Override
	public UploadResult apply(LoanOrder loanOrder) {

		CommandIndex commandIndex = new CommandIndex(AuditingOrder.TYPE_REF, loanOrder.getId(), paramKey);
		UnretryableCommand command = unretryableCommandFunction.prepareCommand(commandIndex, null);
		if (StringUtils.isNotEmpty(command.getMemo())) {
			return new UploadResult(paramKey, wexyunMaterialKey, command.getMemo());
		}

		String ufsPath = loanOrder.getExtParam().get(paramKey);
		byte[] ufsFileByteArray = getUfsFileByteArray(ufsPath);
		File ufsFile = createFile(ufsPath, ufsFileByteArray);
		String token = wexyunLoanClient.uploadFile(ufsFile, UploadFileType.BORROW_APTITUDE);
		ufsFile.delete();
		command = unretryableCommandFunction.updateMemo(command, token);
		return new UploadResult(paramKey, wexyunMaterialKey, command.getMemo());
	}

	@Required
	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}

	@Required
	public void setWexyunMaterialKey(String wexyunMaterialKey) {
		this.wexyunMaterialKey = wexyunMaterialKey;
	}

	private String getFileName(String path) {
		int index = path.lastIndexOf(".");
		String ext = path.substring(index);
		return UUID.randomUUID().toString() + ext;
	}

	private File createFile(String ufsPath, byte[] ufsFileBytes) {
		try {
			String filePath = System.getProperty("java.io.tmpdir") + "/" + getFileName(ufsPath);
			File file = new File(filePath);
			FileUtils.writeByteArrayToFile(file, ufsFileBytes);
			return file;
		} catch (IOException e) {
			throw new ContextedRuntimeException(e);
		}
	}

	private byte[] getUfsFileByteArray(String ufsFilePath) {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			ufsClient.downloadFile(bos, ufsFilePath);
			return bos.toByteArray();
		} catch (IOException e) {
			throw new ContextedRuntimeException(e);
		}
	}

}
