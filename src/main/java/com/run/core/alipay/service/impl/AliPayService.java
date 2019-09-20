package com.run.core.alipay.service.impl;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.run.core.alipay.model.AccOrg;
import com.run.core.alipay.model.AlipayOrder;
import com.run.core.alipay.service.IAccOrgService;
import com.run.core.alipay.service.IAlipayOrderService;
import com.run.core.alipay.utils.HttpTools;
import com.run.core.alipay.utils.RSAUtils;
import com.run.core.alipay.utils.SFTPTool;
import com.run.core.alipay.utils.Tools;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;

/**
 * <p>
 * 支付宝订单 服务实现类
 * socket 调用
 * </p>
 *
 * @author AleeX
 * @since 2019-09-18
 */
@Slf4j
@Service
public class AliPayService {

    @Autowired
    private IAccOrgService accOrgService;

    @Autowired
    private IAlipayOrderService alipayOrderService;

    @Value("${pay-file.save-path}")
    private String savePath;

    @Autowired
    private Sid sid;

    /**
     * <p>
     * 公用事业平台进行欠费查询时调用此接口 200001
     * </p>
     *
     * @param msgFrame 消息体
     * @return
     */
    public String service200001(String msgFrame) {
        JSONObject result = new JSONObject();

        JSONObject frame = JSONObject.fromObject(msgFrame);
        JSONObject body = new JSONObject();

        frame.getJSONObject("head").put("msgTime", Tools.toTimeFormat());
        result.put("head", frame.getJSONObject("head"));

        AccOrg accOrg = accOrgService.getOrgByNo(frame.getJSONObject("body").getString("acctOrgNo"));

        //支付宝传入的查询参数->客户编码
        String queryValue = frame.getJSONObject("body").getString("queryValue");

        String rtnCode = "9999";//成功状态码
        String rtnMsg = "查询成功";//返回的消息
        String consNo = queryValue;//用户编码
        String consName = "";//用户名称
        String capitalNo = "";//资金编号：（可缺省，机构存在特定资金划拨需求时使用)
        String addr = "";//用户地址
        String orgNo = accOrg.getOrgNo();//单位编码，用户所属营业厅，即该用户由哪个营业厅的抄表员负责抄表，
        String orgName = accOrg.getOrgName();//单位编码，用户所属营业厅，即该用户由哪个营业厅的抄表员负责抄表，
        String acctOrgNo = frame.getJSONObject("body").getString("acctOrgNo");//清算单位
        String totalOweAmt = "0";//合计欠费金额(分)
        String totalRcvblAmt = "0";//合计应收金额(分)
        String totalRcvedAmt = "0";// 合计实收金额(分)
        String totalPenalty = "0";// 合计违约金(分)
        String prepayAmt = "0";// 账户余额(分)
        String recordCount = "0";

        JSONArray details = new JSONArray();


        JSONObject postJson = new JSONObject();
        postJson.put("consNo", queryValue);


        String queryResult = "";

        try {
            // 请求参数进行RSA对称加密
            queryResult = HttpTools.post(accOrg.getQueryUrl(), RSAUtils.privateEncrypt(postJson.toString()));

            JSONObject queryResultJson = JSONObject.fromObject(RSAUtils.privateDecrypt(queryResult));

            if (queryResultJson == null)
                throw new Exception("查询失败");
            try {
                String status = queryResultJson.getString("status");


                if (!"200".equals(status))
                    throw new Exception("查询失败");
                JSONObject rtnData = queryResultJson.getJSONObject("data");

                if (rtnData == null || StringUtils.isBlank(rtnData.getString("consNo")) || !consNo.equals(rtnData.getString("consNo")))
                    throw new Exception("查询的用户不存在");
                try {
                    consName = rtnData.getString("consName");
                    addr = rtnData.getString("addr");
                    BigDecimal amt100 = new BigDecimal("100");
                    JSONObject detail = new JSONObject();
                    prepayAmt = new BigDecimal(rtnData.getString("prepayAmt")).multiply(amt100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() + "";
                    detail.put("rcvblAmtId", "1");// 不确定 应收标识
                    detail.put("consNo", consNo);// 用户户号
                    detail.put("consName", consName);// 户名
                    detail.put("orgNo", orgNo);
                    detail.put("orgName", orgName);
                    detail.put("acctOrgNo", acctOrgNo);
                    detail.put("rcvblYm", rtnData.getString("rcvblYm"));
                    detail.put("tPq", rtnData.getString("tPq"));
                    totalRcvblAmt = new BigDecimal(rtnData.getString("rcvblAmt")).multiply(amt100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() + "";
                    detail.put("rcvblAmt", totalRcvblAmt);// 应收金额
                    detail.put("rcvedAmt", "0");// 实收金费
                    totalPenalty = new BigDecimal(rtnData.getString("rcvblPenalty")).multiply(amt100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() + "";
                    detail.put("rcvblPenalty", totalPenalty);//应收违约金（滞纳金）


                    int intTotalOweAmt = new BigDecimal(rtnData.getString("oweAmt")).multiply(amt100).subtract(new BigDecimal(prepayAmt)).setScale(0,BigDecimal.ROUND_HALF_UP).intValue();
                    totalOweAmt = intTotalOweAmt > 0 ? (intTotalOweAmt + "") : "0";
                    detail.put("oweAmt", new BigDecimal(rtnData.getString("oweAmt")).multiply(amt100).setScale(0,BigDecimal.ROUND_HALF_UP).intValue());// 欠费小计
                    recordCount = "1";
                    details.add(detail);

                } catch (Exception e2) {
                    throw new Exception(e2.getMessage());
                }


            } catch (Exception e1) {

                throw new Exception(e1.getMessage());
            }

        } catch (Exception e) {
            rtnCode = "1002";
            rtnMsg = "查询号码不合法";
            log.info("查询接口报错：[{}]",e.getMessage());
        }

        body.put("rtnCode", rtnCode);
        body.put("rtnMsg", rtnMsg);
        body.put("consNo", consNo);
        body.put("consName", consName);
        body.put("capitalNo", capitalNo);
        body.put("addr", addr);
        body.put("orgNo", orgNo);
        body.put("orgName", orgName);
        body.put("acctOrgNo", acctOrgNo);
        body.put("consType", "");
        body.put("totalOweAmt", totalOweAmt);
        body.put("totalRcvblAmt", totalRcvblAmt);
        body.put("totalRcvedAmt", totalRcvedAmt);
        body.put("totalPenalty", totalPenalty);
        body.put("prepayAmt", prepayAmt);
        body.put("recordCount", recordCount);

        body.put("rcvblDet", details);

        result.put("body", body);

        return result.toString();
    }


    /**
     * <p>
     * 公用事业平台进行缴费时调用此接口 200002
     * </p>
     *
     * @param msgFrame
     * @return
     */
    public String service200002(String msgFrame) {
        JSONObject result = new JSONObject();
        String rtnCode = "9999";
        String rtnMsg = "交易成功!";
        String extend = "";
        JSONObject frame = JSONObject.fromObject(msgFrame);

        JSONObject body = frame.getJSONObject("body");


        frame.getJSONObject("head").put("msgTime", Tools.toTimeFormat());

        result.put("head", frame.getJSONObject("head"));

        String rcvDet[] = body.getString("rcvDet").split("\\|\\|\\$");

        if (rcvDet.length != 1) {
            rtnCode = "2005";
            rtnMsg = "业务状态异常";
        } else {
            String ds[] = rcvDet[0].split("\\|");
            String key = ds[0];

            //交易金额
            String payAmt = body.getString("rcvAmt");
            String amount = new BigDecimal(payAmt).divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "";

            AccOrg accOrg = accOrgService.getOrgByNo(body.getString("acctOrgNo"));

            if (accOrg == null) {
                rtnCode = "2005";
                rtnMsg = "业务状态异常";
            } else {
                JSONObject queryJson = new JSONObject();
                queryJson.put("consNo", key);
                try {
                    String queryResult = HttpTools.post(accOrg.getQueryUrl(), RSAUtils.privateEncrypt(queryJson.toString()));

                    JSONObject queryResultJson = JSONObject.fromObject(RSAUtils.privateDecrypt(queryResult));

                    if (queryResultJson == null || !"200".equals(queryResultJson.getString("status")) || queryResultJson.getJSONObject("data") == null) {
                        rtnCode = "2005";
                        rtnMsg = "业务状态异常";
                    } else {

                        JSONObject resultData = queryResultJson.getJSONObject("data");

                        double preAmt = new BigDecimal(amount).subtract(new BigDecimal(resultData.getString("rcvedAmt"))).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                        if (preAmt < 0) {
                            rtnCode = "2002";
                            rtnMsg = "缴费金额不等";
                        } else {
                            JSONObject payJson = new JSONObject();

                            AlipayOrder alipayOrder = new AlipayOrder();
                            alipayOrder.setOrgId(accOrg.getId());
                            alipayOrder.setConsNo(key);
                            alipayOrder.setConsName(resultData.getString("consName"));
                            alipayOrder.setBankDate(body.getString("bankDate"));
                            alipayOrder.setBankDateTime(Tools.toDateTimeString(Tools.getCurrentTime()));
                            alipayOrder.setRcvblAmt(new BigDecimal(resultData.getString("rcvblAmt")).setScale(2, BigDecimal.ROUND_HALF_UP));
                            alipayOrder.setRcvedAmt(new BigDecimal(amount).setScale(2, BigDecimal.ROUND_HALF_UP));
                            alipayOrder.setRcvblPenalty(new BigDecimal(resultData.getString("rcvblPenalty")).setScale(2, BigDecimal.ROUND_HALF_UP));
                            alipayOrder.setRcvblYm(resultData.getString("rcvblYm"));
                            alipayOrder.setTPq(new BigDecimal(resultData.getString("tPq")).setScale(2, BigDecimal.ROUND_HALF_UP));
                            alipayOrder.setBankSerial(body.getString("bankSerial"));
                            alipayOrder.setInstSerial(sid.nextShort());

                            payJson.put("consNo", key);//客户编码
                            payJson.put("amount", amount);//缴费金额
                            //交易流水号
                            String bankSerial = body.getString("bankSerial");
                            payJson.put("bankSerial", bankSerial);
                            //缴费时间
                            payJson.put("payDateTime", body.getString("bankDateTime"));

                            try {
                                String payResult = HttpTools.post(accOrg.getPayUrl(), RSAUtils.privateEncrypt(payJson.toString()));
                                JSONObject payResultJson = JSONObject.fromObject(RSAUtils.privateDecrypt(payResult));

                                if (payResultJson == null || !"200".equals(payResultJson.getString("status")))
                                    throw new Exception("缴费失败");
                                alipayOrder.setStatus("1");

                            } catch (Exception e) {
                                alipayOrder.setStatus("-1");
                            }

                            alipayOrderService.addOrder(alipayOrder);
                        }

                    }

                } catch (Exception e) {
                    log.info("机构销账失败：{}", e.getMessage());
                    rtnCode = "2005";
                    rtnMsg = "业务状态异常";
                }
            }
        }


        JSONObject rtnBody = new JSONObject();

        rtnBody.put("rtnCode", rtnCode);
        rtnBody.put("rtnMsg", rtnMsg);
        rtnBody.put("extend", "");

        result.put("body", rtnBody);


        return result.toString();
    }

    /**
     * 公用事业平台发起文本推送时调用此接口 200012
     *
     * @param msgFrame
     * @return
     */
    public String service200012(String msgFrame) {
        JSONObject result = new JSONObject();
        JSONObject frame = JSONObject.fromObject(msgFrame);

        JSONObject body = frame.getJSONObject("body");

        frame.getJSONObject("head").put("msgTime", Tools.toTimeFormat());
        result.put("head", frame.getJSONObject("head"));

        String rtnCode = "9999";
        String rtnMsg = "交易成功";
        String instSerial = "";
        String extend = "";

        String bankSerial = body.getString("bankSerial");

        AlipayOrder alipayOrder = alipayOrderService.getAlipayOrderByBankSerial(bankSerial,body.getString("acctOrgNo"));

        if(alipayOrder == null){
            rtnCode = "3000";
            rtnMsg = "没有该条记录";
        }else if("-1".equals(alipayOrder.getStatus())){
            rtnCode = "3003";
            rtnMsg = "处理状态失败";
        }

        result.put("body", body);

        return result.toString();
    }

    /**
     * <p>
     *     公用事业平台发起文本推送时调用此接口 200011
     * </p>
     * @param msgFrame
     * @return
     */
    public String service200011(String msgFrame){

        JSONObject result = new JSONObject();
        JSONObject frame = JSONObject.fromObject(msgFrame);

        JSONObject body = frame.getJSONObject("body");

        frame.getJSONObject("head").put("msgTime", Tools.toTimeFormat());
        result.put("head", frame.getJSONObject("head"));

        String filename = body.getString("fileName");
        String filePath = body.getString("filePath");

        String rtnCode = "9999";
        String rtnMsg = "交易成功";
        String extend = "";

        AccOrg accOrg = accOrgService.getOrgByNo(body.getString("acctOrgNo"));

        File file = new File(savePath + "\\" + accOrg.getOrgNo() + "\\" + filename);

        if(file.exists()){
            rtnCode = "4004";
            rtnMsg = "文件已经处理";
        }else{

            try {
                SFTPTool sftpTool = new SFTPTool();
                ChannelSftp sftp = sftpTool.getConnect(accOrg.getFtpUserName(), accOrg.getFtpIp(), accOrg.getFtpPort(), accOrg.getFtpPassword());
                sftpTool.download(filePath, filename, savePath + "\\" + accOrg.getOrgNo() + "\\", sftp);
                sftp.disconnect();
                sftp.getSession().disconnect();
                sftp.exit();
                sftp.quit();
                rtnCode = "4005";
                rtnMsg = "对账报文发送成功";
            } catch (JSchException e) {
                log.info("对账单文件下载出错:{}",e.getMessage());
                rtnCode = "0009";
                rtnMsg = "系统异常";
            }

        }

        JSONObject rtnBody = new JSONObject();
        rtnBody.put("rtnCode", rtnCode);
        rtnBody.put("rtnMsg", rtnMsg);
        rtnBody.put("extend", extend);

        result.put("body",rtnBody);

        return result.toString();

    }

}

