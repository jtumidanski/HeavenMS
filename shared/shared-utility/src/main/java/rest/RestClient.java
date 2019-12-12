package rest;

import java.net.URI;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public final class RestClient<T> {
   private final Response.Status[] GET_SUCCESS_STATUSES = new Response.Status[]{Response.Status.OK};
   private final Response.Status[] CREATE_SUCCESS_STATUSES = new Response.Status[]{Response.Status.CREATED, Response.Status.ACCEPTED, Response.Status.NO_CONTENT};
   private final Response.Status[] UPDATE_SUCCESS_STATUSES = new Response.Status[]{Response.Status.OK, Response.Status.NO_CONTENT, Response.Status.ACCEPTED};
   private final Response.Status[] DELETE_SUCCESS_STATUSES = new Response.Status[]{Response.Status.OK, Response.Status.NO_CONTENT, Response.Status.ACCEPTED};

   private URI uri;

   private Class<T> responseClass;

   private BiConsumer<Integer, T> success;

   private Consumer<Integer> failure;

   public RestClient(URI uri) {
      this.uri = uri;
   }

   public RestClient(URI uri, Class<T> responseClass) {
      this.uri = uri;
      this.responseClass = responseClass;
   }

   /**
    * Sets a function to call on success of the http call.
    *
    * @param success the function
    * @return this
    */
   public RestClient<T> success(BiConsumer<Integer, T> success) {
      this.success = success;
      return this;
   }

   /**
    * Sets a function to call on failure of the http call.
    *
    * @param failure the function
    * @return this
    */
   public RestClient<T> failure(Consumer<Integer> failure) {
      this.failure = failure;
      return this;
   }

   /**
    * Invokes a get http call.
    */
   public void get() {
      invokeMethod(uri, "GET", null, GET_SUCCESS_STATUSES);
   }

   /**
    * Invokes a create http call.
    *
    * @param entity the body of the http call
    */
   public void create(Object entity) {
      invokeMethod(uri, "POST", entity, CREATE_SUCCESS_STATUSES);
   }

   /**
    * Invokes a update http call.
    *
    * @param entity the body of the http call
    */
   public void update(Object entity) {
      invokeMethod(uri, "PATCH", entity, UPDATE_SUCCESS_STATUSES);
   }

   /**
    * Invokes a delete http call.
    */
   public void delete() {
      invokeMethod(uri, "DELETE", DELETE_SUCCESS_STATUSES);
   }

   /**
    * If a success function is declared, call it, passing the response code, and concrete response object (if desired).
    *
    * @param response the response object
    */
   private void applySuccess(Response response) {
      if (success != null) {
         if (responseClass != null && responseClass.equals(Void.class)) {
            success.accept(response.getStatus(), null);
         } else {
            success.accept(response.getStatus(), response.readEntity(responseClass));
         }
      }
   }

   /**
    * If a failure function is declared, call it, passing the response code.
    *
    * @param responseCode the REST response code
    */
   private void applyFailure(int responseCode) {
      if (failure != null) {
         failure.accept(responseCode);
      }
   }

   /**
    * Gets a builder for a supplied URI.
    *
    * @param path the base path to initialize the builder
    * @return the builder
    */
   private Invocation.Builder getBase(URI path) {
      Client client = ClientBuilder.newClient();
      WebTarget webTarget = client.target(path);
      return webTarget.request(MediaType.APPLICATION_JSON);
   }

   /**
    * Determines if a status is in a set of statuses.
    *
    * @param status   the status to find
    * @param statuses the set of statuses
    * @return true if the status is found
    */
   private boolean statusIn(int status, Response.Status[] statuses) {
      return Arrays.stream(statuses).anyMatch(value -> value.getStatusCode() == status);
   }

   /**
    * Invokes a method at the path supplied.
    *
    * @param path                 the path to invoke
    * @param method               the http method to invoke
    * @param entity               the body of the invocation
    * @param successResponseCodes a set of response codes which indicate a successful call
    */
   private void invokeMethod(URI path, String method, Object entity, Response.Status[] successResponseCodes) {
      Response response;

      try {
         response = getBase(path).method(method, Entity.entity(entity, MediaType.APPLICATION_JSON));
      } catch (ProcessingException exception) {
         applyFailure(Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
         return;
      }

      if (statusIn(response.getStatus(), successResponseCodes)) {
         applySuccess(response);
      } else {
         applyFailure(response.getStatus());
      }
   }

   /**
    * Invokes a method at the path supplied.
    *
    * @param path                 the path to invoke
    * @param method               the http method to invoke
    * @param successResponseCodes a set of response codes which indicate a successful call
    */
   private void invokeMethod(URI path, String method, Response.Status[] successResponseCodes) {
      try {
         Response response = getBase(path).method(method);
         evaluateResponse(response, successResponseCodes);
      } catch (ProcessingException exception) {
         applyFailure(Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
      }
   }

   /**
    * Evaluates the http response object to determine if it was a successful call
    *
    * @param response             the response object
    * @param successResponseCodes a set of response codes which indicate success
    */
   private void evaluateResponse(Response response, Response.Status[] successResponseCodes) {
      if (statusIn(response.getStatus(), successResponseCodes)) {
         applySuccess(response);
      } else {
         applyFailure(response.getStatus());
      }
   }
}
