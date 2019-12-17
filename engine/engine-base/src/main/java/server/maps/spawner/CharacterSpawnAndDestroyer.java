package server.maps.spawner;

import java.util.Collections;
import java.util.List;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.processor.ChairProcessor;
import server.maps.MapleMapObject;
import tools.PacketCreator;
import tools.Pair;
import tools.packet.buff.GiveForeignBuff;
import tools.packet.buff.GiveForeignChairSkillEffect;
import tools.packet.remove.RemovePlayer;
import tools.packet.spawn.SpawnPlayer;

public class CharacterSpawnAndDestroyer implements MapObjectSpawnAndDestroyer<MapleCharacter> {
   private static CharacterSpawnAndDestroyer instance;

   public static CharacterSpawnAndDestroyer getInstance() {
      if (instance == null) {
         instance = new CharacterSpawnAndDestroyer();
      }
      return instance;
   }

   private CharacterSpawnAndDestroyer() {
   }

   @Override
   public void sendDestroyData(MapleCharacter object, MapleClient client) {
      PacketCreator.announce(client, new RemovePlayer(object.objectId()));
   }

   @Override
   public void sendSpawnData(MapleCharacter object, MapleClient client) {
      if (!object.isHidden() || client.getPlayer().gmLevel() > 1) {
         PacketCreator.announce(client, new SpawnPlayer(client, object, false));

         if (object.hasBuffFromSourceIdDirty(ChairProcessor.getInstance().getJobMapChair(object.getJob()))) { // mustn't effLock, chrLock this function
            PacketCreator.announce(client, new GiveForeignChairSkillEffect(object.getId()));
         }
      }

      if (object.isHidden()) {
         List<Pair<MapleBuffStat, Integer>> dsstat = Collections.singletonList(new Pair<>(MapleBuffStat.DARKSIGHT, 0));
         object.getMap().broadcastGMMessage(object, new GiveForeignBuff(object.getId(), dsstat), false);
      }
   }

   @Override
   public MapleCharacter as(MapleMapObject object) {
      return (MapleCharacter) object;
   }
}