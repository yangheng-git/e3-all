package cn.e3mall.sso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.RegitsterService;

/**注册功能Controller
 * @author yangheng
 *
 */
@Controller
public class RegitsterController {
    
    @Autowired
    private RegitsterService regitsterService;

    /**展示注册页面
     * @return
     */
    @RequestMapping("/page/register")
    public String showRegister(){
        return "register";
    }
    
    /**前端发来ajax请求。 判断数据的完整性。
     * 
     * @param param
     * @param type
     * @return e3Result 
     * 如果填写的数据已村子，返回false
     */
    @RequestMapping("/user/check/{param}/{type}")
    @ResponseBody
    public E3Result checkDate(@PathVariable String param, @PathVariable Integer type ){
        E3Result e3Result = regitsterService.checkdata(param, type);
        return e3Result;
    }
    
    
    /*注册
     * @param user
     * @return
     */
    @RequestMapping(value="/user/regitster",method=RequestMethod.POST)
    @ResponseBody
    public E3Result regitster(TbUser user){
        E3Result e3Result = regitsterService.regitster(user);
        return e3Result;
    }
}
