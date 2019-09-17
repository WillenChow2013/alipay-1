package com.run.core.alipay.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;


/**
 * 报文组装
 * @author aleex007
 *
 */
@Component
@Slf4j
public class Compose {
	private static final byte BLOCK_START_SIGN = 0x68;
	private static String ENCODING = "UTF-8";

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public byte[] doCanProcess(Map paraMap, String serverCode) throws Exception {

		Gson juJson = new Gson();

		Map map = new HashMap();
		Map headMap = new HashMap();

		String servCode = serverCode;
		headMap.put("version", "1.0.1");

		// 对接测试的时候有支付宝提供的常量
		headMap.put("source", "GSCQQIANNENG");

		// 测试环境是longshine
		headMap.put("desIfno", "LONGSHINE");

		// 服务编码
		headMap.put("servCode", servCode);

		headMap.put("msgId", "ghftest" + Tools.toTimeFormat());

		headMap.put("msgTime", Tools.toTimeFormat());
		headMap.put("extend", "");
		map.put("head", headMap);
		map.put("body", paraMap);
		String message = juJson.toJson(map).replace("\\", "");
		log.info("测试报文为:" + message);
		ByteDataBuffer bdb = new ByteDataBuffer();
		bdb.setInBigEndian(false);
		bdb.writeInt8((byte) 0x68); // 开始字节

		int len = Integer.parseInt(getLen(message, 4));

		// 用于计算数据域的长度是否大于4字节
		bdb.writeInt32(len); // 报文长度
		bdb.writeString(servCode, 6);// 服务编码
		bdb.writeBytes(message.getBytes()); // 报文frame
		bdb.writeInt8((byte) 0x16); // 结束字节
		return bdb.getBytes();

	}

	/**
	 * 计算报文长度，不足四位左补零
	 * 
	 * @param text    报文信息
	 * @param needlen 报文长度规定的字符数
	 * @return
	 */
	public static String getLen(String text, int needlen) {
		if (text != null) {
			int len;
			try {
				len = text.getBytes("utf-8").length;
				String lenStr = String.valueOf(len);
				StringBuffer sb = new StringBuffer(lenStr);
				while (sb.length() < needlen) {
					sb.insert(0, "0");

				}
				return sb.toString();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "static-access", "unchecked" })
	public static List doCanProcess(ByteDataBuffer obj) throws Exception {
		List list = new ArrayList();
		Map bodyMap = new HashMap();
		Map headMap = new HashMap();
		ByteDataBuffer databuf = obj;
		databuf.setEncoding(ENCODING);
		databuf.setInBigEndian(false);
		int totalLen = 0; // 长度
		byte sign = databuf.readInt8();
		if (sign != BLOCK_START_SIGN) {
			System.out.println("无法找到起始标记!");
		}
		totalLen = databuf.readInt32();
		databuf.readString(6);
		byte[] dataBytes = new byte[totalLen];
		databuf.readBytes(dataBytes);
		String message = new String(dataBytes);
		// 报文是json格式，把json报文转换成Map类型的
		JsonToMap gson = new JsonToMap();
		Map messageMap = gson.toMap(message);
		bodyMap = (Map) messageMap.get("body");
		headMap = (Map) messageMap.get("head");
		list.add(headMap);
		list.add(bodyMap);

		return list;
	}

}
