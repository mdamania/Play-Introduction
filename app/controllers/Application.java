package controllers;

import java.util.List;

import models.Profile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.Logger;
import play.cache.Cached;
import play.libs.Akka;
import play.libs.F;
import play.libs.Json;
import play.libs.F.Function;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import scala.concurrent.ExecutionContext;
import actions.GetProfileAction;
import actions.UpdateProfileAction;

public class Application extends Controller {

	ExecutionContext executionContext = Akka.system().dispatchers()
			.lookup("play.akka.actor.my-context");

	@With(value = { GetProfileAction.class, UpdateProfileAction.class })
	@Cached(key = "indexPage")
	public Result index() {
		Logger.info("rendering index");
		return ok(views.html.index.render("Hello World"));
	}

	public Promise<Result> testPromise() {
		Logger.info("Action Thread : " + Thread.currentThread().getName());
		Promise<Integer> promiseOfInt = Promise.promise(
				new Function0<Integer>() {
					public Integer apply() {
						Logger.info("Thread executing promise: "
								+ Thread.currentThread().getName());
						return executeDBCall();
					}
				}, executionContext);

		return promiseOfInt.map(new Function<Integer, Result>() {
			public Result apply(Integer i) {
				Logger.info("Promise mapping thread: "
						+ Thread.currentThread().getName());
				return ok("Got result: " + i);
			}
		});
	}

	public Promise<Result> get(String id) {
		Promise<WSResponse> profileResponse = WS.url(
				"http://localhost:9000/profile/" + id).get();
		return profileResponse.map(new Function<WSResponse, Result>() {
			public Result apply(WSResponse arg0) throws Throwable {
				return ok(arg0.asJson());
			}
		});
	}

	public Promise<Result> delete(String id) {
		Promise<WSResponse> profileResponse = WS.url(
				"http://localhost:9000/profile/" + id).delete();
		return profileResponse.map(new Function<WSResponse, Result>() {
			public Result apply(WSResponse arg0) throws Throwable {
				return ok(arg0.asJson());
			}
		});
	}
	
	public Integer executeDBCall() {
		return 1;
	}

	@SuppressWarnings("unchecked")
	public Promise<Result> parallelProxy() {
		Logger.info("Start method : parallelProxy");

		Promise<WSResponse> google = WS.url("http://www.google.co.in").get();
		Promise<WSResponse> yahoo = WS.url("http://www.yahoo.com").get();
		Promise<WSResponse> bing = WS.url("http://www.bing.com").get();

		Promise<List<WSResponse>> responses = Promise.sequence(google, yahoo,
				bing);
		Promise<Result> result = responses
				.map(new Function<List<WSResponse>, Result>() {
					public Result apply(List<WSResponse> responseList)
							throws Throwable {
						String searchResponse = null;
						Logger.info("Got Response");
						for (WSResponse response : responseList) {
							searchResponse = searchResponse
									+ response.getBody() + "</br>";
						}
						return ok(searchResponse).as("text/html");
					}
				});

		Logger.info("Exit method : parallelProxy");
		return result;
	}

	public Promise<Result> testProxy(String url) {
		Promise<WSResponse> profileResponse = WS.url(url).get();
		Logger.info("Fired request: " + profileResponse);
		Promise<Result> result = profileResponse
				.map(new Function<WSResponse, Result>() {
					public Result apply(WSResponse arg0) throws Throwable {
						Logger.info("Proxy is ready, respond");
						return ok(arg0.getBody()).as("text/html");
					}
				});
		Logger.info("Exit method : proxy");
		return result;
	}
}
