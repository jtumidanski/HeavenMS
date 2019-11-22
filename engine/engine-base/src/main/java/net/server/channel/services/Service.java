package net.server.channel.services;

import net.server.channel.services.task.BaseService;

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