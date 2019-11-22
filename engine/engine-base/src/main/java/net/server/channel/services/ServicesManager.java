package net.server.channel.services;

public class ServicesManager {
   private Service[] services;

   public ServicesManager() {
      ServiceType[] serviceTypes = ServiceType.values();

      services = new Service[serviceTypes.length];
      for (ServiceType type : serviceTypes) {
         services[type.ordinal()] = type.createService();
      }
   }

   public Service getAccess(ServiceType s) {
      return services[s.ordinal()];
   }

   public void shutdown() {
      for (int i = 0; i < ServiceType.values().length; i++) {
         services[i].dispose();
      }
      services = null;
   }
}
