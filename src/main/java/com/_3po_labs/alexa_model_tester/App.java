package com._3po_labs.alexa_model_tester;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com._3po_labs.alexa_model_tester.config.AVSClientConfig;

@SpringBootApplication
public class App {

    public static void main(String[] args) throws Exception {

        ApplicationContext ctx = new AnnotationConfigApplicationContext(AVSClientConfig.class);
        SpringApplication.run(App.class, args);
    }
}
