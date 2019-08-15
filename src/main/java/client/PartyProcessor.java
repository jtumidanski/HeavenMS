package client;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.server.world.MapleParty;
import server.maps.MapleDoor;

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

         for (MapleCharacter pchr : partyMembers) {
            MapleDoor door = partyDoors.get(pchr.getId());
            if (door != null) {
               door.updateDoorPortal(pchr);
            }
         }

         for (MapleDoor door : partyDoors.values()) {
            for (MapleCharacter pchar : partyMembers) {
               door.getTownDoor().sendDestroyData(pchar.getClient(), true);
            }
         }

         if (partyLeaver != null) {
            Collection<MapleDoor> leaverDoors = partyLeaver.getDoors();
            for (MapleDoor door : leaverDoors) {
               for (MapleCharacter pchar : partyMembers) {
                  door.getTownDoor().sendDestroyData(pchar.getClient(), true);
               }
            }
         }

         List<Integer> histMembers = party.getMembersSortedByHistory();
         for (Integer chrid : histMembers) {
            MapleDoor door = partyDoors.get(chrid);

            if (door != null) {
               for (MapleCharacter pchar : partyMembers) {
                  door.getTownDoor().sendSpawnData(pchar.getClient());
               }
            }
         }
      }

      if (partyLeaver != null) {
         Collection<MapleDoor> leaverDoors = partyLeaver.getDoors();

         if (partyDoors != null) {
            for (MapleDoor door : partyDoors.values()) {
               door.getTownDoor().sendDestroyData(partyLeaver.getClient(), true);
            }
         }

         for (MapleDoor door : leaverDoors) {
            door.getTownDoor().sendDestroyData(partyLeaver.getClient(), true);
         }

         for (MapleDoor door : leaverDoors) {
            door.updateDoorPortal(partyLeaver);
            door.getTownDoor().sendSpawnData(partyLeaver.getClient());
         }
      }
   }
}
