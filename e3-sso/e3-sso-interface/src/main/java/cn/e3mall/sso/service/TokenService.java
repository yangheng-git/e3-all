package cn.e3mall.sso.service;

import cn.e3mall.common.utils.E3Result;

/**根据token取用户信息 
 * 前台传入token ，从redis取用户信息、
 * @author yangheng
 *
 */
public interface TokenService {

    E3Result getUserByToken(String token);
}
