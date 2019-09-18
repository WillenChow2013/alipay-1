package com.run.core.alipay.service;

import com.run.core.alipay.model.AccOrg;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 支付宝生活缴费机构 服务类
 * </p>
 *
 * @author AleeX
 * @since 2019-09-18
 */
public interface IAccOrgService extends IService<AccOrg> {

    AccOrg getOrgByNo(String orgNo);

}
