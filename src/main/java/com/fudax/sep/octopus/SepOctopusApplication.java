package com.fudax.sep.octopus;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author liuyi4
 */
@SpringBootApplication
@EnableAdminServer
@EnableDiscoveryClient
public class SepOctopusApplication {
    public static void main(String[] args) {
        SpringApplication.run(SepOctopusApplication.class, args);
    }

}
