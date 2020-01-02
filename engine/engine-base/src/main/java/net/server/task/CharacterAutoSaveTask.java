package net.server.task;

import client.MapleCharacter;
import config.YamlConfig;
import net.server.PlayerStorage;
import net.server.world.World;

public class CharacterAutoSaveTask extends BaseTask implements Runnable {

   public CharacterAutoSaveTask(World world) {
      super(world);
   }

   @Override
   public void run() {
      if (!YamlConfig.config.server.USE_AUTOSAVE) return;

      PlayerStorage ps = world.getPlayerStorage();
      for (MapleCharacter chr : ps.getAllCharacters()) {
         if (chr != null && chr.isLoggedIn()) {
            chr.saveCharToDB(false);
         }
      }
   }
}
