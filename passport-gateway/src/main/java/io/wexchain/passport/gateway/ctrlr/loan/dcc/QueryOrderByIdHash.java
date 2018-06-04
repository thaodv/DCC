package io.wexchain.passport.gateway.ctrlr.loan.dcc;

import com.wexmarket.topia.commons.pagination.PageParam;
import org.springframework.util.Base64Utils;

import javax.validation.constraints.NotBlank;
import java.io.UnsupportedEncodingException;

public class QueryOrderByIdHash extends PageParam {

	@NotBlank
	private String idHashBase64;

	public String getIdHashBase64() {
		return idHashBase64;
	}

	public void setIdHashBase64(String idHashBase64) {
		this.idHashBase64 = idHashBase64;
	}

	public byte[] getIdHash(){
		return Base64Utils.decodeFromUrlSafeString(idHashBase64);
	}
}
