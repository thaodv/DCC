package io.wexchain.dcc.service.frontend.ctrlr;


import io.wexchain.dcc.service.frontend.ctrlr.security.MemberDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityBaseController extends BaseController{

	protected MemberDetails getMember(){
		MemberDetails memberDetails = (MemberDetails) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
		return memberDetails;
	}

	protected Long getMemberId(){
		Long id = getMember().getId();
		return id;
	}


}
