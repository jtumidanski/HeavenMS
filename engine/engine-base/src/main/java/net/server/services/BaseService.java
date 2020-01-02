package net.server.services;

import config.YamlConfig;

public abstract class BaseService {
   protected static int getChannelSchedulerIndex(int mapId) {
      int section = 1000000000 / YamlConfig.config.server.CHANNEL_LOCKS;
      return mapId / section;
   }

   public abstract void dispose();
}
