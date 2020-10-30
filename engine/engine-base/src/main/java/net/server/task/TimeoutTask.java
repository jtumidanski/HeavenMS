package net.server.task;

import java.util.Collection;

import client.MapleCharacter;
import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import config.YamlConfig;
import net.server.world.World;

public class TimeoutTask extends BaseTask implements Runnable {
   @Override
   public void run() {
      long time = System.currentTimeMillis();
      Collection<MapleCharacter> chars = world.getPlayerStorage().getAllCharacters();
      for (MapleCharacter chr : chars) {
         if (time - chr.getClient().getLastPacket() > YamlConfig.config.server.TIMEOUT_DURATION) {
            LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.DCS, chr.getName() + " auto-disconnected due to inactivity.");
            chr.getClient().disconnect(true, chr.getCashShop().isOpened());
         }
      }
   }

   public TimeoutTask(World world) {
      super(world);
   }
}
