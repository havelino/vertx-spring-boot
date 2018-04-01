/**
 * 
 */
package com.havelino.example.verticles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;

/**
 * @author hha0009
 *
 */

class myOwnException extends Exception{

	private static final long serialVersionUID = -3739418679348886333L;

	private String devMessage;


	public myOwnException(String message, String devMessage) {
		super(message);
		this.devMessage = devMessage;
	}

	public String getDevMessage() {
		return devMessage;
	}

	public void setDevMessage(String devMessage) {
		this.devMessage = devMessage;
	}

	@Override
	public String toString() {
		return "myOwnException [ message= "+getMessage()+"devMessage=" + devMessage + "]";
	}
}

@Component
public class DatabaseVerticle extends AbstractVerticle {

	public static final String GET_ALL_ARTICLES = "topic_name";

	private final ObjectMapper mapper = Json.mapper;

	private List<String> dataTest= new ArrayList<>();

	@Override
	public void start() throws Exception {
		dataTest = Arrays.asList("Hola", "mundo", "a ui");

		super.start();
		vertx.eventBus()
		.<String>consumer(GET_ALL_ARTICLES)
		.handler(getAllArticleService(dataTest));
	}

	private Handler<Message<String>> getAllArticleService(List<String> dataTest) {
		return msg -> vertx.<String>executeBlocking(future -> {
			System.out.println(msg.address()+" : "+msg.body()+" : "+msg.getClass().getName());
			
			try {
				HashMap<String, String> params=mapper.readValue(msg.body(), new TypeReference<HashMap<String, String>>(){});
				
				for (String p : params.keySet()) {
					System.out.println(p+" : "+params.get(p));
				}
			
				future.complete(mapper.writeValueAsString(dataTest));
			} catch (JsonProcessingException e) {
				System.out.println("Failed to serialize result");
				future.fail(new myOwnException("Error", "{\"error\":\""+e.getMessage()+"\"}"));
			} catch (IOException e) {
				future.fail(new myOwnException("Error", "{\"error\":\""+e.getMessage()+"\"}"));
				future.fail(e);
			}
		}, result -> {
			if (result.succeeded()) {
				msg.reply(result.result());
			} else {

				Throwable ex = result.cause();
				if(ex instanceof myOwnException) {
					myOwnException myExc = (myOwnException) ex;

					msg.reply(myExc.getDevMessage());

				}else {
					msg.reply(ex.getCause().toString());
				}


			}
		});
	}


}