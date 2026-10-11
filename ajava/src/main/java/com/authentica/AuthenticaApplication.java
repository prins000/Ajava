package com.authentica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthenticaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticaApplication.class, args);
        System.out.println("==================================================================");
        System.out.println("🛡️  AUTHENTICA SCAM & THREAT DETECTION PLATFORM RUNNING");
        System.out.println("🌐 Web Dashboard: http://localhost:8080/");
        System.out.println("📊 REST API Base:  http://localhost:8080/api/v1");
        System.out.println("🗄️  H2 Console:    http://localhost:8080/h2-console");
        System.out.println("🔑 Default Admin:  admin@authentica.io / Admin@1234");
        System.out.println("👤 Default User:   user@authentica.io  / User@1234");
        System.out.println("==================================================================");
    }
}
