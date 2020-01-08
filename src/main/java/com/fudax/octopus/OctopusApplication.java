package com.fudax.octopus;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAdminServer
@SpringBootApplication
public class OctopusApplication {

    public static void main(String[] args) {
        SpringApplication.run(OctopusApplication.class, args);
    }


}
