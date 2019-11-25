package net.server.services;

public class ServicesManager {
   private Service[] services;

   public ServicesManager(ServiceType serviceBundle) {
      Enum[] serviceTypes = serviceBundle.enumValues();

      services = new Service[serviceTypes.length];
      for (Enum type : serviceTypes) {
         services[type.ordinal()] = ((ServiceType) type).createService();
      }
   }

   public Service getAccess(ServiceType s) {
      return services[s.ordinal()];
   }

   public void shutdown() {
      for (int i = 0; i < services.length; i++) {
         services[i].dispose();
      }
      services = null;
   }
}
