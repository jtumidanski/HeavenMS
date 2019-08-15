package server.maps;

import java.util.Collection;

import client.MapleCharacter;

public class MapleDoorProcessor {
   private static MapleDoorProcessor ourInstance = new MapleDoorProcessor();

   public static MapleDoorProcessor getInstance() {
      return ourInstance;
   }

   private MapleDoorProcessor() {
   }

   public void attemptRemoveDoor(final MapleCharacter owner) {
      final MapleDoor destroyDoor = owner.getPlayerDoor();
      if (destroyDoor != null && destroyDoor.dispose()) {
         long effectTimeLeft = 3000 - destroyDoor.getElapsedDeployTime();   // portal deployment effect duration
         if (effectTimeLeft > 0) {
            MapleMap town = destroyDoor.getTown();
            town.getChannelServer().registerOverallAction(town.getId(), new Runnable() {
               @Override
               public void run() {
                  broadcastRemoveDoor(destroyDoor, owner);   // thanks BHB88 for noticing doors crashing players when instantly cancelling buff
               }
            }, effectTimeLeft);
         } else {
            broadcastRemoveDoor(destroyDoor, owner);
         }
      }
   }

   private void broadcastRemoveDoor(MapleDoor destroyDoor, MapleCharacter owner) {
      MapleDoorObject areaDoor = destroyDoor.getAreaDoor();
      MapleDoorObject townDoor = destroyDoor.getTownDoor();

      MapleMap target = destroyDoor.getTarget();
      MapleMap town = destroyDoor.getTown();

      Collection<MapleCharacter> targetChars = target.getCharacters();
      Collection<MapleCharacter> townChars = town.getCharacters();

      target.removeMapObject(areaDoor);
      town.removeMapObject(townDoor);

      for (MapleCharacter chr : targetChars) {
         areaDoor.sendDestroyData(chr.getClient());
      }

      for (MapleCharacter chr : townChars) {
         townDoor.sendDestroyData(chr.getClient());
      }

      owner.removePartyDoor(false);

      if (destroyDoor.getTownPortal().getId() == 0x80) {
         for (MapleCharacter chr : townChars) {
            MapleDoor door = chr.getMainTownDoor();
            if (door != null) {
               townDoor.sendSpawnData(chr.getClient());
            }
         }
      }
   }
}
