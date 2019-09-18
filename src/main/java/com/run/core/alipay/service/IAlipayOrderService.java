package com.run.core.alipay.service;

import com.run.core.alipay.model.AlipayOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 支付宝生活支付账单 服务类
 * </p>
 *
 * @author AleeX
 * @since 2019-09-18
 */
public interface IAlipayOrderService extends IService<AlipayOrder> {
    void addOrder(AlipayOrder alipayOrder);

    AlipayOrder getAlipayOrderByBankSerial(String bankSerial,String orgNo);
}
