package net.server.services;

public class Service<T extends BaseService> {

   private Class<T> cls;
   private BaseService service;

   public Service(Class<T> s) {
      try {
         cls = s;
         service = cls.getConstructor().newInstance();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public T getService() {
      return cls.cast(service);
   }

   public void dispose() {
      service.dispose();
      service = null;
   }

}