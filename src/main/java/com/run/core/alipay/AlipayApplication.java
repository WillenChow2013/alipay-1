package com.run.core.alipay;

import com.run.core.alipay.netty.NettyServerListener;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;

@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = "com.run.core.alipay.*,org.n3r.*")
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
