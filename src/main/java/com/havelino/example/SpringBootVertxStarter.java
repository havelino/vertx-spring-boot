/**
 * 
 */
package com.havelino.example;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.havelino.example.verticles.DatabaseVerticle;
import com.havelino.example.verticles.ServerVerticle;

import io.vertx.core.Vertx;

/**
 * @author hha0009
 *
 */
@SpringBootApplication
@Configuration
@ComponentScan(basePackages = { "com.havelino.example" })
public class SpringBootVertxStarter {
	
	
    private ServerVerticle serverVerticle = new ServerVerticle();

  
    private DatabaseVerticle serviceVerticle = new DatabaseVerticle();

	public static void main(String[] args) {
		SpringApplication.run(SpringBootVertxStarter.class, args);
	}
	
	@PostConstruct
	public void deployVerticle() {
		Vertx vertx= Vertx.vertx();
		 vertx.deployVerticle(serverVerticle);
	     vertx.deployVerticle(serviceVerticle);
	}

}
