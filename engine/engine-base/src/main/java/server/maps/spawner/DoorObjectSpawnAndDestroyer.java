package server.maps.spawner;

import java.awt.Point;

import client.MapleCharacter;
import client.MapleClient;
import server.maps.MapleDoorObject;
import server.maps.MapleMapObject;
import tools.PacketCreator;
import tools.packet.party.PartyPortal;
import tools.packet.spawn.RemoveDoor;
import tools.packet.spawn.SpawnDoor;
import tools.packet.spawn.SpawnPortal;

public class DoorObjectSpawnAndDestroyer implements MapObjectSpawnAndDestroyer<MapleDoorObject> {
   private static DoorObjectSpawnAndDestroyer instance;

   public static DoorObjectSpawnAndDestroyer getInstance() {
      if (instance == null) {
         instance = new DoorObjectSpawnAndDestroyer();
      }
      return instance;
   }

   private DoorObjectSpawnAndDestroyer() {
   }

   @Override
   public void sendSpawnData(MapleDoorObject object, MapleClient client) {
      sendSpawnData(object, client, true);
   }

   public void sendSpawnData(MapleDoorObject object, MapleClient client, boolean launched) {
      MapleCharacter chr = client.getPlayer();
      if (object.getFrom() == chr.getMapId()) {
         if (chr.getParty().isPresent() && (object.getOwnerId() == chr.getId() || chr.getParty().map(party -> party.isMember(object.getOwnerId())).orElse(false))) {
            PacketCreator.announce(client, new PartyPortal(object.getFrom(), object.getTo(), object.toPosition()));
         }

         PacketCreator.announce(chr, new SpawnPortal(object.getFrom(), object.getTo(), object.toPosition()));
         if (!object.inTown()) {
            PacketCreator.announce(chr, new SpawnDoor(object.getOwnerId(), object.position(), launched));
         }
      }
   }

   @Override
   public void sendDestroyData(MapleDoorObject object, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      if (object.getOriginMapId() == chr.getMapId()) {
         if (chr.getParty().isPresent() && (object.getOwnerId() == chr.getId() || chr.getParty().map(party -> party.isMember(object.getOwnerId())).orElse(false))) {
            PacketCreator.announce(client, new PartyPortal(999999999, 999999999, new Point(-1, -1)));
         }
         PacketCreator.announce(client, new RemoveDoor(object.getOwnerId(), object.inTown()));
      }
   }

   public void sendDestroyData(MapleDoorObject object, MapleClient client, boolean partyUpdate) {
      if (client != null && object.getOriginMapId() == client.getPlayer().getMapId()) {
         PacketCreator.announce(client, new PartyPortal(999999999, 999999999, new Point(-1, -1)));
         PacketCreator.announce(client, new RemoveDoor(object.getOwnerId(), object.inTown()));
      }
   }

   @Override
   public MapleDoorObject as(MapleMapObject object) {
      return (MapleDoorObject) object;
   }
}
