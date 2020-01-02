package client.processor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import client.MapleCharacter;
import net.server.world.MapleParty;
import server.maps.MapleDoor;
import server.maps.MapleDoorObject;
import server.maps.spawner.DoorObjectSpawnAndDestroyer;
import server.processor.maps.MapleMapObjectProcessor;

public class PartyProcessor {
   private static PartyProcessor ourInstance = new PartyProcessor();

   public static PartyProcessor getInstance() {
      return ourInstance;
   }

   private PartyProcessor() {
   }


   public void addPartyPlayerDoor(MapleCharacter target) {
      MapleDoor targetDoor = target.getPlayerDoor();
      if (targetDoor != null) {
         target.applyPartyDoor(targetDoor, true);
      }
   }

   public void removePartyPlayerDoor(MapleParty party, MapleCharacter target) {
      target.removePartyDoor(party);
   }

   public void updatePartyTownDoors(MapleParty party, MapleCharacter target, MapleCharacter partyLeaver, List<MapleCharacter> partyMembers) {
      if (partyLeaver != null) {
         removePartyPlayerDoor(party, target);
      } else {
         addPartyPlayerDoor(target);
      }

      Map<Integer, MapleDoor> partyDoors = null;
      if (!partyMembers.isEmpty()) {
         partyDoors = party.getDoors();

         for (MapleCharacter character : partyMembers) {
            MapleDoor door = partyDoors.get(character.getId());
            if (door != null) {
               door.updateDoorPortal(character);
            }
         }

         for (MapleDoor door : partyDoors.values()) {
            for (MapleCharacter character : partyMembers) {
               MapleDoorObject mdo = door.getTownDoor();
               DoorObjectSpawnAndDestroyer.getInstance().sendDestroyData(mdo, character.getClient(), true);
               character.removeVisibleMapObject(mdo);
            }
         }

         if (partyLeaver != null) {
            Collection<MapleDoor> leaverDoors = partyLeaver.getDoors();
            for (MapleDoor door : leaverDoors) {
               for (MapleCharacter character : partyMembers) {
                  MapleDoorObject mdo = door.getTownDoor();
                  DoorObjectSpawnAndDestroyer.getInstance().sendDestroyData(mdo, character.getClient(), true);
                  character.removeVisibleMapObject(mdo);
               }
            }
         }

         List<Integer> histMembers = party.getMembersSortedByHistory();
         for (Integer characterId : histMembers) {
            MapleDoor door = partyDoors.get(characterId);

            if (door != null) {
               for (MapleCharacter character : partyMembers) {
                  MapleDoorObject mdo = door.getTownDoor();
                  MapleMapObjectProcessor.getInstance().sendSpawnData(mdo, character.getClient());
                  character.addVisibleMapObject(mdo);
               }
            }
         }
      }

      if (partyLeaver != null) {
         Collection<MapleDoor> leaverDoors = partyLeaver.getDoors();

         if (partyDoors != null) {
            for (MapleDoor door : partyDoors.values()) {
               MapleDoorObject mdo = door.getTownDoor();
               DoorObjectSpawnAndDestroyer.getInstance().sendDestroyData(mdo, partyLeaver.getClient(), true);
               partyLeaver.removeVisibleMapObject(mdo);
            }
         }

         for (MapleDoor door : leaverDoors) {
            MapleDoorObject mdo = door.getTownDoor();
            DoorObjectSpawnAndDestroyer.getInstance().sendDestroyData(mdo, partyLeaver.getClient(), true);
            partyLeaver.removeVisibleMapObject(mdo);
         }

         for (MapleDoor door : leaverDoors) {
            door.updateDoorPortal(partyLeaver);
            MapleDoorObject mdo = door.getTownDoor();
            MapleMapObjectProcessor.getInstance().sendSpawnData(mdo, partyLeaver.getClient());
            partyLeaver.addVisibleMapObject(mdo);
         }
      }
   }
}
