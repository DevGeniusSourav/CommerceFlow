package com.commerceflow.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class ProductServiceApplication {

    private static final Logger log =
            LoggerFactory.getLogger(ProductServiceApplication.class);

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        log.info("Test");
        SpringApplication.run(ProductServiceApplication.class, args);
    }

}
