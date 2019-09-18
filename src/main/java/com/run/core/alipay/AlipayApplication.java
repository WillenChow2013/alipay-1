package com.run.core.alipay;

import com.run.core.alipay.netty.NettyServerListener;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import javax.annotation.Resource;

@SpringBootApplication(scanBasePackages = "com.run.core.alipay.*")
@MapperScan("com.run.core.alipay.mapper")
public class AlipayApplication implements CommandLineRunner {
    @Resource
    private NettyServerListener nettyServerListener;

    public static void main(String[] args) {
        SpringApplication.run(AlipayApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        nettyServerListener.start();
    }
}
