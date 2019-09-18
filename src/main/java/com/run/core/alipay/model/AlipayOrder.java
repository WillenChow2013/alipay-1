package com.run.core.alipay.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * undefined
 * </p>
 *
 * @author AleeX
 * @since 2019-09-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="AlipayOrder对象", description="支付账单")
public class AlipayOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "机构ID")
    private Integer orgId;

    @ApiModelProperty(value = "用户编码")
    private String consNo;

    @ApiModelProperty(value = "用户名称")
    private String consName;

    @ApiModelProperty(value = "应收金额")
    private BigDecimal rcvblAmt;

    @ApiModelProperty(value = "实收金额")
    private BigDecimal rcvedAmt;

    @ApiModelProperty(value = "应收违约金")
    private BigDecimal rcvblPenalty;

    @ApiModelProperty(value = "应收年月")
    private String rcvblYm;

    @ApiModelProperty(value = "收费水量")
    private BigDecimal tPq;

    @ApiModelProperty(value = "缴费流水号")
    private String bankSerial;

    @ApiModelProperty(value = "缴费日期")
    private String bankDate;

    @ApiModelProperty(value = "缴费时间")
    private String bankDateTime;

    @ApiModelProperty(value = "机构销账流水号")
    private String instSerial;

    @ApiModelProperty(value = "交易状态")
    private String status;


}
