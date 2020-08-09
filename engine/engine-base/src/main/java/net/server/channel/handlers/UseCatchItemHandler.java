package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.autoban.AutoBanManager;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import constants.inventory.ItemConstants;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.UseCatchItemPacket;
import net.server.channel.packet.reader.UseCatchItemReader;
import server.MapleItemInformationProvider;
import server.life.MapleMonster;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.monster.CatchMonsterFailure;
import tools.packet.monster.CatchMonsterWithItem;
import tools.packet.stat.EnableActions;

public final class UseCatchItemHandler extends AbstractPacketHandler<UseCatchItemPacket> {
   @Override
   public Class<UseCatchItemReader> getReaderClass() {
      return UseCatchItemReader.class;
   }

   @Override
   public void handlePacket(UseCatchItemPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      AutoBanManager abm = chr.getAutoBanManager();
      abm.setTimestamp(5, Server.getInstance().getCurrentTimestamp(), 4);
      int monsterId = packet.monsterId();
      int itemId = packet.itemId();

      MapleMonster mob = chr.getMap().getMonsterByOid(monsterId);
      if (chr.getInventory(ItemConstants.getInventoryType(itemId)).countById(itemId) <= 0) {
         return;
      }
      if (mob == null) {
         return;
      }
      switch (itemId) {
         case 2270000 -> usePheromonePerfume(client, chr, monsterId, itemId, mob);
         case 2270001 -> usePouch(client, chr, abm, monsterId, itemId, mob);
         case 2270002 -> useElementRock(client, chr, abm, monsterId, itemId, mob);
         case 2270003 -> useCliffsMagicCane(client, chr, monsterId, itemId, mob);
         case 2270005 -> useFirstTransparentMarble(client, chr, monsterId, itemId, mob);
         case 2270006 -> useSecondTransparentMarble(client, chr, monsterId, itemId, mob);
         case 2270007 -> useThirdTransparentMarble(client, chr, monsterId, itemId, mob);
         case 2270004 -> usePurificationMarble(client, chr, monsterId, itemId, mob);
         case 2270008 -> useFishNet(client, chr, abm, monsterId, itemId, mob);
         default -> defaultCatchItem(client, chr, abm, monsterId, itemId, mob);
      }
   }

   private void defaultCatchItem(MapleClient client, MapleCharacter chr, AutoBanManager abm, int monsterId, int itemId, MapleMonster mob) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      int itemGanho = ii.getCreateItem(itemId);
      int mobItem = ii.getMobItem(itemId);

      if (itemGanho != 0 && mobItem == mob.id()) {
         int timeCatch = ii.getUseDelay(itemId);
         int mobHp = ii.getMobHP(itemId);

         if (timeCatch != 0 && (abm.getLastSpam(10) + timeCatch) < currentServerTime()) {
            if (mobHp != 0 && mob.getHp() < ((mob.getMaxHp() / 100) * mobHp)) {
               MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new CatchMonsterWithItem(monsterId, itemId, (byte) 1));
               mob.getMap().killMonster(mob, null, false);
               MapleInventoryManipulator.removeById(client, MapleInventoryType.USE, itemId, 1, true, true);
               MapleInventoryManipulator.addById(client, itemGanho, (short) 1, "", -1);
            } else if (mob.id() != 9500336) {
               if (mobHp != 0) {
                  abm.spam(10);
                  PacketCreator.announce(client, new CatchMonsterFailure(0));
               }
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("FISHING_CANNOT_USE_NET_YET"));
            }
         }
      }
      PacketCreator.announce(client, new EnableActions());
   }

   private void useFishNet(MapleClient client, MapleCharacter chr, AutoBanManager abm, int monsterId, int itemId, MapleMonster mob) {
      if (mob.id() == 9500336) {
         if ((abm.getLastSpam(10) + 3000) < currentServerTime()) {
            abm.spam(10);
            MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new CatchMonsterWithItem(monsterId, itemId, (byte) 1));
            mob.getMap().killMonster(mob, null, false);
            MapleInventoryManipulator.removeById(client, MapleInventoryType.USE, itemId, 1, true, true);
            MapleInventoryManipulator.addById(client, 2022323, (short) 1, "", -1);
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("FISHING_CANNOT_USE_NET_YET"));
         }
         PacketCreator.announce(client, new EnableActions());
      }
   }

   private void usePurificationMarble(MapleClient client, MapleCharacter chr, int monsterId, int itemId, MapleMonster mob) {
      if (mob.id() == 9300175) {
         if (mob.getHp() < ((mob.getMaxHp() / 10) * 4)) {
            MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new CatchMonsterWithItem(monsterId, itemId, (byte) 1));
            mob.getMap().killMonster(mob, null, false);
            MapleInventoryManipulator.removeById(client, MapleInventoryType.USE, itemId, 1, true, true);
            MapleInventoryManipulator.addById(client, 4001169, (short) 1, "", -1);
         } else {
            PacketCreator.announce(client, new CatchMonsterFailure(0));
         }
      }
      PacketCreator.announce(client, new EnableActions());
   }

   private void useThirdTransparentMarble(MapleClient client, MapleCharacter chr, int monsterId, int itemId, MapleMonster mob) {
      if (mob.id() == 9300191) {
         if (mob.getHp() < ((mob.getMaxHp() / 10) * 3)) {
            MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new CatchMonsterWithItem(monsterId, itemId, (byte) 1));
            mob.getMap().killMonster(mob, null, false);
            MapleInventoryManipulator.removeById(client, MapleInventoryType.USE, itemId, 1, true, true);
            MapleInventoryManipulator.addById(client, 2109003, (short) 1, "", -1);
         } else {
            PacketCreator.announce(client, new CatchMonsterFailure(0));
         }
      }
      PacketCreator.announce(client, new EnableActions());
   }

   private void useSecondTransparentMarble(MapleClient client, MapleCharacter chr, int monsterId, int itemId, MapleMonster mob) {
      if (mob.id() == 9300189) {
         if (mob.getHp() < ((mob.getMaxHp() / 10) * 3)) {
            MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new CatchMonsterWithItem(monsterId, itemId, (byte) 1));
            mob.getMap().killMonster(mob, null, false);
            MapleInventoryManipulator.removeById(client, MapleInventoryType.USE, itemId, 1, true, true);
            MapleInventoryManipulator.addById(client, 2109002, (short) 1, "", -1);
         } else {
            PacketCreator.announce(client, new CatchMonsterFailure(0));
         }
      }
      PacketCreator.announce(client, new EnableActions());
   }

   private void useFirstTransparentMarble(MapleClient client, MapleCharacter chr, int monsterId, int itemId, MapleMonster mob) {
      if (mob.id() == 9300187) {
         if (mob.getHp() < ((mob.getMaxHp() / 10) * 3)) {
            MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new CatchMonsterWithItem(monsterId, itemId, (byte) 1));
            mob.getMap().killMonster(mob, null, false);
            MapleInventoryManipulator.removeById(client, MapleInventoryType.USE, itemId, 1, true, true);
            MapleInventoryManipulator.addById(client, 2109001, (short) 1, "", -1);
         } else {
            PacketCreator.announce(client, new CatchMonsterFailure(0));
         }
      }
      PacketCreator.announce(client, new EnableActions());
   }

   private void useCliffsMagicCane(MapleClient client, MapleCharacter chr, int monsterId, int itemId, MapleMonster mob) {
      if (mob.id() == 9500320) {
         if (mob.getHp() < ((mob.getMaxHp() / 10) * 4)) {
            MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new CatchMonsterWithItem(monsterId, itemId, (byte) 1));
            mob.getMap().killMonster(mob, null, false);
            MapleInventoryManipulator.removeById(client, MapleInventoryType.USE, itemId, 1, true, true);
            MapleInventoryManipulator.addById(client, 4031887, (short) 1, "", -1);
         } else {
            PacketCreator.announce(client, new CatchMonsterFailure(0));
         }
      }
      PacketCreator.announce(client, new EnableActions());
   }

   private void useElementRock(MapleClient client, MapleCharacter chr, AutoBanManager abm, int monsterId, int itemId, MapleMonster mob) {
      if (mob.id() == 9300157) {
         if ((abm.getLastSpam(10) + 800) < currentServerTime()) {
            if (mob.getHp() < ((mob.getMaxHp() / 10) * 4)) {
               if (chr.canHold(4031868, 1)) {
                  if (Math.random() < 0.5) { // 50% chance
                     MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new CatchMonsterWithItem(monsterId, itemId, (byte) 1));
                     mob.getMap().killMonster(mob, null, false);
                     MapleInventoryManipulator.removeById(client, MapleInventoryType.USE, itemId, 1, true, true);
                     MapleInventoryManipulator.addById(client, 4031868, (short) 1, "", -1);
                  } else {
                     MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new CatchMonsterWithItem(monsterId, itemId, (byte) 0));
                  }
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("ETC_SPACE_NEEDED"));
               }

               abm.spam(10);
            } else {
               PacketCreator.announce(client, new CatchMonsterFailure(0));
            }
         }
         PacketCreator.announce(client, new EnableActions());
      }
   }

   private void usePouch(MapleClient client, MapleCharacter chr, AutoBanManager abm, int monsterId, int itemId, MapleMonster mob) {
      if (mob.id() == 9500197) {
         if ((abm.getLastSpam(10) + 1000) < currentServerTime()) {
            if (mob.getHp() < ((mob.getMaxHp() / 10) * 4)) {
               MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new CatchMonsterWithItem(monsterId, itemId, (byte) 1));
               mob.getMap().killMonster(mob, null, false);
               MapleInventoryManipulator.removeById(client, MapleInventoryType.USE, itemId, 1, true, true);
               MapleInventoryManipulator.addById(client, 4031830, (short) 1, "", -1);
            } else {
               abm.spam(10);
               PacketCreator.announce(client, new CatchMonsterFailure(0));
            }
         }
         PacketCreator.announce(client, new EnableActions());
      }
   }

   private void usePheromonePerfume(MapleClient client, MapleCharacter chr, int monsterId, int itemId, MapleMonster mob) {
      if (mob.id() == 9300101) {
         MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new CatchMonsterWithItem(monsterId, itemId, (byte) 1));
         mob.getMap().killMonster(mob, null, false);
         MapleInventoryManipulator.removeById(client, MapleInventoryType.USE, itemId, 1, true, true);
         MapleInventoryManipulator.addById(client, 1902000, (short) 1, "", -1);
      }
      PacketCreator.announce(client, new EnableActions());
   }
}
