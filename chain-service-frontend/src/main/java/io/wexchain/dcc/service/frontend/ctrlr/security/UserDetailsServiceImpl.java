package io.wexchain.dcc.service.frontend.ctrlr.security;

import io.wexchain.dcc.service.frontend.model.request.RegisterRequest;
import io.wexchain.dcc.service.frontend.service.wexyun.MemberService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;


public class UserDetailsServiceImpl implements UserDetailsService {
	private MemberService memberService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		try {
			String memberId = memberService.getByIdentity(username);
			if(memberId == null){
				RegisterRequest registerRequest = new RegisterRequest();
				registerRequest.setLoginName(username);
				memberId = memberService.register(registerRequest);
			}
			List<GrantedAuthority> authorities = new ArrayList<>();
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
			return new MemberDetails(Long.parseLong(memberId), username,
					"", true,
					authorities);
		} catch (EmptyResultDataAccessException e) {
			throw new UsernameNotFoundException("用户名或密码不正确");
		}

	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}
}
