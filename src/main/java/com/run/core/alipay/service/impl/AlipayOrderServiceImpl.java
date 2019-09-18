package com.run.core.alipay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.run.core.alipay.mapper.AccOrgMapper;
import com.run.core.alipay.model.AccOrg;
import com.run.core.alipay.model.AlipayOrder;
import com.run.core.alipay.mapper.AlipayOrderMapper;
import com.run.core.alipay.service.IAlipayOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 支付宝订单 服务实现类
 * </p>
 *
 * @author AleeX
 * @since 2019-09-18
 */
@Service
public class AlipayOrderServiceImpl extends ServiceImpl<AlipayOrderMapper, AlipayOrder> implements IAlipayOrderService {

    @Autowired
    private AccOrgMapper accOrgMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void addOrder(AlipayOrder alipayOrder) {

        this.baseMapper.insert(alipayOrder);

    }

    @Override
    public AlipayOrder getAlipayOrderByBankSerial(String bankSerial, String orgNo) {

        QueryWrapper<AccOrg> accOrgQueryWrapper = new QueryWrapper<>();
        accOrgQueryWrapper.eq("org_no",orgNo);
        AccOrg accOrg = accOrgMapper.selectOne(accOrgQueryWrapper);

        QueryWrapper<AlipayOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bank_serial",bankSerial);
        queryWrapper.eq("org_id",accOrg.getId());

        return this.baseMapper.selectOne(queryWrapper);
    }
}
