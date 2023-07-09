package com.hpe.findlover.realm;

import com.hpe.findlover.model.UserBasic;
import com.hpe.findlover.service.UserDetailService;
import com.hpe.findlover.service.UserService;
import com.hpe.findlover.util.Constant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

public class UserRealm extends AuthorizingRealm {
    private Logger logger = LogManager.getLogger(UserRealm.class);
    @Autowired
    private UserService userService;
    @Autowired
    public UserDetailService userDetailService;

	/**
	 * 用户身份认证
	 *
	 * @param token 封装了用户身份信息
	 * @return
	 * @throws AuthenticationException
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// 获取用户的输入的账号.
		String username = (String) token.getPrincipal();
		// 通过username从数据库中查找 User对象
		UserBasic userBasic;
		if ((userBasic = userService.selectByEmail(username)) == null) {
			throw new UnknownAccountException();
		} else if (userBasic.getStatus() == Constant.USER_LOCKED_STATUS) {
			throw new LockedAccountException();
		} else if (userBasic.getStatus() == Constant.USER_DISABLED_STATUS) {
			throw new DisabledAccountException();
		}
		// 加密交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，盐值为当前用户用户名
		return new SimpleAuthenticationInfo(username, userBasic.getPassword(),ByteSource.Util.bytes(userBasic.getEmail()), getName());
	}

	/*权限配置类*/
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		return null;
	}
}
