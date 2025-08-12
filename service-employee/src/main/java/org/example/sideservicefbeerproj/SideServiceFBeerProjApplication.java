package org.example.sideservicefbeerproj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SideServiceFBeerProjApplication {

    public static void main(String[] args) {
        SpringApplication.run(SideServiceFBeerProjApplication.class, args);
    }

}
