package org.example.beerProj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class BeerProj {

    public static void main(String[] args) {
        SpringApplication.run(BeerProj.class, args);
    }

}
