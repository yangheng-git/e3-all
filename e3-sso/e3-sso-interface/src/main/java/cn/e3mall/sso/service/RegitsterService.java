package cn.e3mall.sso.service;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbUser;

public interface RegitsterService {

    E3Result checkdata(String param , Integer type);
    
    E3Result regitster(TbUser user);
}
