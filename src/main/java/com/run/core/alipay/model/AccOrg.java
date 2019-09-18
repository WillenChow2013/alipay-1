package com.run.core.alipay.model;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
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
@ApiModel(value="AccOrg对象", description="缴费机构")
public class AccOrg implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "机构编码")
    private String orgNo;

    @ApiModelProperty(value = "机构名称")
    private String orgName;

    @ApiModelProperty(value = "欠费查询接口")
    private String queryUrl;

    @ApiModelProperty(value = "缴费接口")
    private String payUrl;

    @ApiModelProperty(value = "销账结果查询接口")
    private String confirmUrl;

    @ApiModelProperty(value = "FTP地址")
    private String ftpIp;

    @ApiModelProperty(value = "FTP端口")
    private int ftpPort;

    @ApiModelProperty(value = "FTP账户")
    private String ftpUserName;

    @ApiModelProperty(value = "FTP密码")
    private String ftpPassword;

    @ApiModelProperty(value = "入驻时间")
    private LocalDate createTime;


}
