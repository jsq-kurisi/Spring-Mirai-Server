package net.lz1998.pbbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
@EnableAutoConfiguration
public class SpringMiraiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringMiraiServerApplication.class, args);
    }

}
