package server.processor.maps;

import client.MapleCharacter;
import client.MapleClient;
import server.life.MapleMonster;
import server.life.MapleNPC;
import server.life.MaplePlayerNPC;
import server.maps.MapleDoorObject;
import server.maps.MapleDragon;
import server.maps.MapleHiredMerchant;
import server.maps.MapleKite;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleMiniGame;
import server.maps.MapleMist;
import server.maps.MaplePlayerShop;
import server.maps.MapleReactor;
import server.maps.MapleSummon;
import server.maps.spawner.CharacterSpawnAndDestroyer;
import server.maps.spawner.DoorObjectSpawnAndDestroyer;
import server.maps.spawner.DragonSpawnAndDestroyer;
import server.maps.spawner.HiredMerchantSpawnAndDestroyer;
import server.maps.spawner.KiteSpawnAndDestroyer;
import server.maps.spawner.MapItemSpawnAndDestroyer;
import server.maps.spawner.MiniGameSpawnAndDestroyer;
import server.maps.spawner.MistSpawnAndDestroyer;
import server.maps.spawner.MonsterSpawnAndDestroyer;
import server.maps.spawner.NPCSpawnAndDestroyer;
import server.maps.spawner.PlayerNPCSpawnAndDestroyer;
import server.maps.spawner.PlayerShopSpawnAndDestroyer;
import server.maps.spawner.ReactorSpawnAndDestroyer;
import server.maps.spawner.SpawnAndDestroyerRegistry;
import server.maps.spawner.SummonSpawnAndDestroyer;

public class MapleMapObjectProcessor {
   private static MapleMapObjectProcessor ourInstance = new MapleMapObjectProcessor();

   public static MapleMapObjectProcessor getInstance() {
      return ourInstance;
   }

   private SpawnAndDestroyerRegistry registry = new SpawnAndDestroyerRegistry();

   private MapleMapObjectProcessor() {
      setSpawnAndDestroyerHandlers();
   }

   protected void setSpawnAndDestroyerHandlers() {
      registry.setHandler(MapleCharacter.class, CharacterSpawnAndDestroyer.getInstance());
      registry.setHandler(MapleDoorObject.class, DoorObjectSpawnAndDestroyer.getInstance());
      registry.setHandler(MapleDragon.class, DragonSpawnAndDestroyer.getInstance());
      registry.setHandler(MapleHiredMerchant.class, HiredMerchantSpawnAndDestroyer.getInstance());
      registry.setHandler(MapleKite.class, KiteSpawnAndDestroyer.getInstance());
      registry.setHandler(MapleMapItem.class, MapItemSpawnAndDestroyer.getInstance());
      registry.setHandler(MapleMiniGame.class, MiniGameSpawnAndDestroyer.getInstance());
      registry.setHandler(MapleMist.class, MistSpawnAndDestroyer.getInstance());
      registry.setHandler(MapleMonster.class, MonsterSpawnAndDestroyer.getInstance());
      registry.setHandler(MapleNPC.class, NPCSpawnAndDestroyer.getInstance());
      registry.setHandler(MaplePlayerNPC.class, PlayerNPCSpawnAndDestroyer.getInstance());
      registry.setHandler(MaplePlayerShop.class, PlayerShopSpawnAndDestroyer.getInstance());
      registry.setHandler(MapleReactor.class, ReactorSpawnAndDestroyer.getInstance());
      registry.setHandler(MapleSummon.class, SummonSpawnAndDestroyer.getInstance());
   }

   public void updateMapObjectVisibility(MapleCharacter chr, MapleMapObject mo) {
      double rangedDistance = MapleMapProcessor.getInstance().getRangedDistance();
      if (!chr.isMapObjectVisible(mo)) { // object entered view range
         if (mo.getType() == MapleMapObjectType.SUMMON || mo.getPosition().distanceSq(chr.getPosition()) <= rangedDistance) {
            chr.addVisibleMapObject(mo);
            MapleMapObjectProcessor.getInstance().sendSpawnData(mo, chr.getClient());
         }
      } else if (mo.getType() != MapleMapObjectType.SUMMON && mo.getPosition().distanceSq(chr.getPosition()) > rangedDistance) {
         chr.removeVisibleMapObject(mo);
         MapleMapObjectProcessor.getInstance().sendDestroyData(mo, chr.getClient());
      }
   }

   public void sendSpawnData(MapleMapObject mapObject, MapleClient client) {
      registry.getHandler(mapObject.getClass()).ifPresent(handler -> handler.sendSpawnData(handler.as(mapObject), client));
   }

   public void sendDestroyData(MapleMapObject mapObject, MapleClient client) {
      registry.getHandler(mapObject.getClass()).ifPresent(handler -> handler.sendDestroyData(handler.as(mapObject), client));
   }
}
