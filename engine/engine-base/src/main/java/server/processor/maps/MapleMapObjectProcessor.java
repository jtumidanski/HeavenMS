package server.processor.maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import server.life.MapleMonster;
import server.life.MapleNPC;
import server.life.MaplePlayerNPC;
import server.maps.AnimatedMapleMapObject;
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
import tools.data.output.MaplePacketLittleEndianWriter;

public class MapleMapObjectProcessor {
   private static MapleMapObjectProcessor ourInstance = new MapleMapObjectProcessor();

   public static MapleMapObjectProcessor getInstance() {
      return ourInstance;
   }

   private SpawnAndDestroyerRegistry registry = new SpawnAndDestroyerRegistry();

   private static final byte[] idleMovementPacketData;

   public static long getIdleMovementDataLength() {
      return 15;
   }

   static {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter((int) getIdleMovementDataLength());
      mplew.write(1); //movement command count
      mplew.write(0);
      mplew.writeShort(-1); //x
      mplew.writeShort(-1); //y
      mplew.writeShort(0); //xwobble
      mplew.writeShort(0); //ywobble
      mplew.writeShort(0); //fh
      mplew.write(-1); //stance
      mplew.writeShort(0); //duration
      idleMovementPacketData = mplew.getPacket();
   }

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
         if (mo.type() == MapleMapObjectType.SUMMON || mo.position().distanceSq(chr.position()) <= rangedDistance) {
            chr.addVisibleMapObject(mo);
            MapleMapObjectProcessor.getInstance().sendSpawnData(mo, chr.getClient());
         }
      } else if (mo.type() != MapleMapObjectType.SUMMON && mo.position().distanceSq(chr.position()) > rangedDistance) {
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

   public List<Byte> getIdleMovementBytes(AnimatedMapleMapObject object) {
      byte[] movementData = adjustIdleMovementData(object);
      List<Byte> result = new ArrayList<>();
      for (byte bit : movementData) {
         result.add(bit);
      }
      return result;
   }

   private byte[] adjustIdleMovementData(AnimatedMapleMapObject object) {
      byte[] movementData = Arrays.copyOf(idleMovementPacketData, idleMovementPacketData.length);
      //seems wasteful to create a whole packet writer when only a few values are changed
      int x = object.position().x;
      int y = object.position().y;
      movementData[2] = (byte) (x & 0xFF); //x
      movementData[3] = (byte) (x >> 8 & 0xFF);
      movementData[4] = (byte) (y & 0xFF); //y
      movementData[5] = (byte) (y >> 8 & 0xFF);
      movementData[12] = (byte) (object.stance() & 0xFF);
      return movementData;
   }

   public MapleDragon createDragon(MapleCharacter character) {
      MapleDragon dragon = new MapleDragon(character.getId(), character.position(), character.stance());
      MapleMapObjectProcessor.getInstance().sendSpawnData(dragon, character.getClient());
      return dragon;
   }
}
