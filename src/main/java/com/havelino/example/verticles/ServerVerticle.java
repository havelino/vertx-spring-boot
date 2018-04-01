/**
 * 
 */
package com.havelino.example.verticles;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * @author hha0009
 *
 */
@Component
public class ServerVerticle extends AbstractVerticle {

	private void getAllArticlesHandler(RoutingContext routingContext) {

		HashMap<String, String> params= new HashMap<>();

		params.put("param1", "1");
		params.put("param2", "2");
		try {
			String parameters;

			parameters = new ObjectMapper().writeValueAsString(params);

			vertx.eventBus()
			.<String>send(DatabaseVerticle.GET_ALL_ARTICLES, parameters, result -> {
				if (result.succeeded()) {
					routingContext.response()
					.putHeader("content-type", "application/json")
					.setStatusCode(200)
					.end(result.result()
							.body());
				} else {
					routingContext.response()
					.setStatusCode(500)
					.end();
				}
			});

		} catch (JsonProcessingException e) {
			routingContext.response()
			.setStatusCode(500)
			.end();
		}
	}

	@Override
	public void start() throws Exception {
		super.start();

		Router router = Router.router(vertx);
		router.get("/list")
		.handler(this::getAllArticlesHandler);

		router.get("/").handler(routingContext -> {
			routingContext.response().end("<h1>Hello from my first " +
					"Vert.x 3 application</h1>");
		});
		 // Serve the non private static pages
	    //router.route().handler(StaticHandler.create());

		vertx.createHttpServer()
		.requestHandler(router::accept)
		.listen(config().getInteger("http.port", 8080));
	}
}
