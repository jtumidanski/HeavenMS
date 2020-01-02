package net.server.services.task.world;

import net.server.audit.locks.MonitoredLockType;
import net.server.services.BaseScheduler;
import net.server.services.BaseService;

public class CharacterSaveService extends BaseService {
   CharacterSaveScheduler chrSaveScheduler = new CharacterSaveScheduler();

   @Override
   public void dispose() {
      if (chrSaveScheduler != null) {
         chrSaveScheduler.dispose();
         chrSaveScheduler = null;
      }
   }

   public void registerSaveCharacter(int characterId, Runnable runAction) {
      chrSaveScheduler.registerSaveCharacter(characterId, runAction);
   }

   private class CharacterSaveScheduler extends BaseScheduler {

      public CharacterSaveScheduler() {
         super(MonitoredLockType.WORLD_SAVE_CHARACTERS);
      }

      public void registerSaveCharacter(Integer characterId, Runnable runAction) {
         registerEntry(characterId, runAction, 0);
      }

      public void unregisterSaveCharacter(Integer characterId) {
         interruptEntry(characterId);
      }

   }

}
