package com.example.demo_springboot;

import com.thebeastshop.forest.springboot.annotation.ForestScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@ForestScan(basePackages = {"com.example.demo_springboot.api"})
@SpringBootApplication
public class AliPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(AliPayApplication.class, args);
    }


}
