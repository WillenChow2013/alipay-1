package com.run.core.alipay.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import lombok.extern.slf4j.Slf4j;

/*
 * SFTPTool类的作用：创建sftp客户端，能连接到支付宝公共事业平台给事业单位的账号,把对账文件下载到本地
 * 
 * */
@Slf4j
public class SFTPTool {

	//创建连接
	public ChannelSftp getConnect(String username,String host,int sOnlineSftpPort,String password) {
		//创建sftp客户端连接到服务器
		ChannelSftp sftp = null;
		try {
			JSch jsch = new JSch();
			jsch.getSession(username, host, sOnlineSftpPort);
			Session sshSession = jsch.getSession(username, host, sOnlineSftpPort);
			System.out.println("SFTP：Session created.");
			sshSession.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			log.info("---------------------------------------------------------------------");
			log.info("开始连接SFTP服务器,host:[{}],name:[{}],port:[{}],password:[{}]",host,username,sOnlineSftpPort,password);
			log.info("---------------------------------------------------------------------");
			//连接sftp服务器
			sshSession.connect();
			log.info("---------------------------------------------------------------------");
			log.info("SFTP：Session connected.");
			log.info("SFTP：Opening Channel.");
			log.info("---------------------------------------------------------------------");
			Channel channel = sshSession.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
			log.info("---------------------------------------------------------------------");
			log.info("SFTP：Connected to " + host + ".");
			log.info("---------------------------------------------------------------------");
		} catch (Exception e) {
			log.info("---------------------------------------------------------------------");
			log.info("创建sftp客户端失败:" + e.getMessage());
			log.info("---------------------------------------------------------------------");
			e.printStackTrace();
		}
		return sftp;
	}

	/**
	 * SFTP文件下载
	 * 
	 * @param directory
	 *            SFTP目录
	 * @param downloadFile
	 *            文件名称
	 * @param saveFile
	 *            文件保存本地路径
	 * @param sftp
	 *            SFTP连接对象
	 */
	public void download(String directory, String downloadFile,
			String saveFile, ChannelSftp sftp) {
		try {
			sftp.cd(directory);
			File file = new File(saveFile + "/" + downloadFile);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			//下载SFTP服务器中downloadFile文件夹里面所有的文件到saveFile文件夹
			sftp.get(downloadFile, fileOutputStream);
			fileOutputStream.close();
		} catch (Exception e) {
			log.info("SFTP文件下载出错了！");
			e.printStackTrace();
		}
	}

	/**
	 * SFTP文件删除
	 * 
	 * @param directory
	 *            SFTP目录
	 * @param deleteFile
	 *            文件名称
	 * @param sftp
	 *            SFTP连接对象
	 */
	public void delete(String directory, String deleteFile, ChannelSftp sftp) {
		try {
			sftp.cd(directory);
			sftp.rm(deleteFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取文本信息
	 * 
	 * @param fileUrl
	 *            文件存储位置
	 * @param sftp
	 *            SFTP连接对象
	 * @return
	 */
	public String getTextInfo(String fileUrl, ChannelSftp sftp) {
		String info = "";
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		try {
			inputStream = sftp.get(fileUrl);
			bufferedReader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF-8"));
			String str = null;
			StringBuffer buffer = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str + "\n");
			}
			info = buffer.toString();
			return info;
		} catch (Exception e) {
			info = "erro";
		} finally {
			if (inputStream != null && bufferedReader != null) {
				try {
					bufferedReader.close();
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return info;
	}

	/**
	 * SFTP列出目录文件
	 * 
	 * @param directory
	 *            SFTP目录
	 * @param sftp
	 *            SFTP连接对象
	 * @return 文件集合
	 * @throws SftpException
	 */
	@SuppressWarnings("rawtypes")
	public Vector listFiles(String directory, ChannelSftp sftp)
			throws SftpException {
		return sftp.ls(directory);
	}

}
