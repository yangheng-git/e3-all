package cn.e3mall.sso.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.sso.service.TokenService;

/**
 * 根据token获取用户信息Controller
 * 
 * 如果前端使用jsonp跨域请求，需要进行修改，支持jsonp， 有两种方法
 * 方法1、手动拼js语句。 
 * 方法2、使用spring4.1以后支持的MappingJacksonValue
 * 
 * jsonp请求的标志是 会带有一个 callback的字符串参数。
 * @author yangheng
 *
 */
@Controller
public class TokenController {

    @Autowired
    private TokenService tokenService;
    
    /**
     * 方式1，手动拼js语句，
     * @param token
     * @param callback
     * @return
     */
    /*@RequestMapping(value="/user/token作废/{token}",produces="application/json;charset=utf-8")*/
    @RequestMapping(value="/user/token作废/{token}",produces=MediaType.APPLICATION_JSON_UTF8_VALUE )
    @ResponseBody
    public String  getUserByToken作废(@PathVariable String token ,String callback){
        
        E3Result result = tokenService.getUserByToken(token);
        //响应结果前，判断是否为jsonp请求
        if (StringUtils.isNotBlank(callback)) {
            //callback有值，表示是jsonp请求。 
            //把结果封装成一个js语句响应
            return  callback + "(" + JsonUtils.objectToJson(result) + ");";
        }
        return JsonUtils.objectToJson(result);
        
    }
    
    /**
     * 方式2，使用spring MappingJacksonValue
     * 
     * @param token
     * @param callback
     * @return
     */
    @RequestMapping("/user/token/{token}")
    @ResponseBody
    public Object getUserByToken(@PathVariable String token,String callback){
        E3Result result = tokenService.getUserByToken(token);
        //响应之前，判断是否为jsonp请求
        if(StringUtils.isNotBlank(callback)){
            //是jsonp请求，将结果封装为一个js语句。 
            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
            mappingJacksonValue.setJsonpFunction(callback);
            return mappingJacksonValue;
        }
        return result;
    }
}
