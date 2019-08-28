package server.processor.maps;

import client.MapleCharacter;
import server.maps.MapleDoor;
import server.maps.MapleDoorObject;
import server.maps.MapleMap;

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
            // thanks BHB88 for noticing doors crashing players when instantly cancelling buff
            town.getChannelServer().registerOverallAction(town.getId(), () -> broadcastRemoveDoor(destroyDoor, owner), effectTimeLeft);
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

      target.removeMapObject(areaDoor);
      town.removeMapObject(townDoor);

      target.getCharacters().forEach(character -> areaDoor.sendDestroyData(character.getClient()));
      town.getCharacters().forEach(character -> townDoor.sendDestroyData(character.getClient()));


      owner.removePartyDoor(false);

      if (destroyDoor.getTownPortal().getId() == 0x80) {
         town.getCharacters().stream()
               .filter(character -> character.getMainTownDoor() != null)
               .forEach(character -> townDoor.sendSpawnData(character.getClient()));
      }
   }
}
