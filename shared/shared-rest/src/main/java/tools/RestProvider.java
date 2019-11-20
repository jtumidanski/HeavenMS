package tools;


import java.net.URI;
import java.util.Optional;
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

public class RestProvider {
   private static RestProvider instance;

   public static RestProvider getInstance() {
      if (instance == null) {
         instance = new RestProvider();
      }
      return instance;
   }

   public static <T> void doNothing(T o) {
   }

   private RestProvider() {
   }

   private Invocation.Builder getBase(URI path) {
      Client client = ClientBuilder.newClient();
      WebTarget webTarget = client.target(path);
      return webTarget.request(MediaType.APPLICATION_JSON);
   }

   private boolean statusIn(int status, Response.Status... statuses) {
      for (Response.Status value : statuses) {
         if (status == value.getStatusCode()) {
            return true;
         }
      }
      return false;
   }

   public void update(URI path, Object entity, Consumer<Integer> onSuccess, Consumer<Integer> onFailure) {
      Invocation.Builder builder = getBase(path);
      Response response = builder.put(Entity.entity(entity, MediaType.APPLICATION_JSON));
      if (statusIn(response.getStatus(), Response.Status.OK, Response.Status.NO_CONTENT, Response.Status.ACCEPTED)) {
         onSuccess.accept(response.getStatus());
      } else if (onFailure != null) {
         onFailure.accept(response.getStatus());
      }
   }

   public <T> Optional<T> get(URI path, Class<T> responseClass) {
      Invocation.Builder builder = getBase(path);
      Response response;
      try {
         response = builder.get();
      } catch (ProcessingException exception) {
         return Optional.empty();
      }

      if (statusIn(response.getStatus(), Response.Status.OK)) {
         return Optional.of(response.readEntity(responseClass));
      } else {
         return Optional.empty();
      }
   }

   public <T> void get(URI path, Class<T> responseClass, Consumer<T> onSuccess, Runnable onFailure) {
      Invocation.Builder builder = getBase(path);
      Response response;
      try {
         response = builder.get();
      } catch (ProcessingException exception) {
         onFailure.run();
         return;
      }

      if (statusIn(response.getStatus(), Response.Status.OK)) {
         onSuccess.accept(response.readEntity(responseClass));
      } else {
         onFailure.run();
      }
   }

   public void delete(URI path, Consumer<Integer> onSuccess, Consumer<Integer> onFailure) {
      Invocation.Builder builder = getBase(path);
      Response response = builder.get();
      if (statusIn(response.getStatus(), Response.Status.OK, Response.Status.NO_CONTENT, Response.Status.ACCEPTED)) {
         onSuccess.accept(response.getStatus());
      } else if (onFailure != null) {
         onFailure.accept(response.getStatus());
      }
   }

   public <T> void post(URI path, Object entity, Class<T> responseClass, BiConsumer<Integer, T> onSuccess, Consumer<Integer> onFailure) {
      Invocation.Builder builder = getBase(path);
      Response response = builder.post(Entity.entity(entity, MediaType.APPLICATION_JSON));
      if (statusIn(response.getStatus(), Response.Status.CREATED, Response.Status.ACCEPTED, Response.Status.NO_CONTENT)) {
         onSuccess.accept(response.getStatus(), response.readEntity(responseClass));
      } else {
         onFailure.accept(response.getStatus());
      }
   }

   public void post(URI path, Object entity, Consumer<Integer> onSuccess, Consumer<Integer> onFailure) {
      Invocation.Builder builder = getBase(path);
      Response response = builder.post(Entity.entity(entity, MediaType.APPLICATION_JSON));
      if (statusIn(response.getStatus(), Response.Status.CREATED, Response.Status.ACCEPTED, Response.Status.NO_CONTENT)) {
         onSuccess.accept(response.getStatus());
      } else {
         onFailure.accept(response.getStatus());
      }
   }
}