package com.run.core.alipay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.run.core.alipay.model.AccOrg;
import com.run.core.alipay.mapper.AccOrgMapper;
import com.run.core.alipay.service.IAccOrgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 支付宝生活缴费机构 服务实现类
 * </p>
 *
 * @author AleeX
 * @since 2019-09-18
 */
@Service
public class AccOrgServiceImpl extends ServiceImpl<AccOrgMapper, AccOrg> implements IAccOrgService {

    @Override
    public AccOrg getOrgByNo(String orgNo) {

        QueryWrapper<AccOrg> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("org_no",orgNo);

        return this.baseMapper.selectOne(queryWrapper);
    }
}
