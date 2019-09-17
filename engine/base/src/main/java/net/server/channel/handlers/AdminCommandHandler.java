/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation version 3 as published by
 the Free Software Foundation. You may not use, modify or distribute
 this program under any other version of the GNU Affero General Public
 License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.server.channel.handlers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.processor.BanProcessor;
import net.server.AbstractPacketHandler;
import net.server.PacketReader;
import net.server.channel.packet.command.admin.BanPlayerPacket;
import net.server.channel.packet.command.admin.BaseAdminCommandPacket;
import net.server.channel.packet.command.admin.BlockPlayerCommandPacket;
import net.server.channel.packet.command.admin.ChangeMapPacket;
import net.server.channel.packet.command.admin.DeleteInventoryByTypePacket;
import net.server.channel.packet.command.admin.EnteringMapPacket;
import net.server.channel.packet.command.admin.HidePacket;
import net.server.channel.packet.command.admin.KillMonsterPacket;
import net.server.channel.packet.command.admin.MonsterHpPacket;
import net.server.channel.packet.command.admin.PlayerWarnPacket;
import net.server.channel.packet.command.admin.QuestResetPacket;
import net.server.channel.packet.command.admin.SetPlayerExpPacket;
import net.server.channel.packet.command.admin.SummonMonsterPacket;
import net.server.channel.packet.command.admin.SummonMonstersItemPacket;
import net.server.channel.packet.command.admin.TestingPacket;
import net.server.channel.packet.reader.AdminCommandReader;
import server.MapleItemInformationProvider;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.quest.MapleQuest;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.Randomizer;
import tools.ServerNoticeType;
import tools.StringUtil;

public final class AdminCommandHandler extends AbstractPacketHandler<BaseAdminCommandPacket> {
   @Override
   public Class<? extends PacketReader<BaseAdminCommandPacket>> getReaderClass() {
      return AdminCommandReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      return client.getPlayer().isGM();
   }

   @Override
   public void handlePacket(BaseAdminCommandPacket packet, MapleClient client) {
      if (packet instanceof SummonMonstersItemPacket) {
         summonMonsters(client, ((SummonMonstersItemPacket) packet).summonItemId());
      } else if (packet instanceof DeleteInventoryByTypePacket) {
         deleteInventory(client, ((DeleteInventoryByTypePacket) packet).inventoryType());
      } else if (packet instanceof SetPlayerExpPacket) {
         setExp(client, ((SetPlayerExpPacket) packet).amount());
      } else if (packet instanceof BanPlayerPacket) {
         banPlayer(client);
      } else if (packet instanceof BlockPlayerCommandPacket) {
         blockCommand(client, ((BlockPlayerCommandPacket) packet).victim(), ((BlockPlayerCommandPacket) packet).theType(),
               ((BlockPlayerCommandPacket) packet).duration(), ((BlockPlayerCommandPacket) packet).description());
      } else if (packet instanceof HidePacket) {
         hidePlayer(client, ((HidePacket) packet).hide());
      } else if (packet instanceof EnteringMapPacket) {
         enteringAMap(client, ((EnteringMapPacket) packet).theType());
      } else if (packet instanceof ChangeMapPacket) {
         changeMapCommand(client, ((ChangeMapPacket) packet).victim(), ((ChangeMapPacket) packet).mapId());
      } else if (packet instanceof KillMonsterPacket) {
         killMonster(client, ((KillMonsterPacket) packet).mobToKill(), ((KillMonsterPacket) packet).amount());
      } else if (packet instanceof QuestResetPacket) {
         questReset(client, ((QuestResetPacket) packet).questId());
      } else if (packet instanceof SummonMonsterPacket) {
         summon(client, ((SummonMonsterPacket) packet).mobId(), ((SummonMonsterPacket) packet).quantity());
      } else if (packet instanceof MonsterHpPacket) {
         monsterHpBroadcast(client, ((MonsterHpPacket) packet).mobHp());
      } else if (packet instanceof PlayerWarnPacket) {
         warnCommand(client, ((PlayerWarnPacket) packet).victim(), ((PlayerWarnPacket) packet).message());
      } else if (packet instanceof TestingPacket) {
         testingPacket(((TestingPacket) packet).printableInt());
      } else {
         System.out.println("New GM packet encountered (MODE : " + packet.mode() + ": " + packet.toString());
      }
   }

   private void summonMonsters(MapleClient c, int summonItemId) {
      int[][] toSpawn = MapleItemInformationProvider.getInstance().getSummonMobs(summonItemId);
      for (int[] toSpawnChild : toSpawn) {
         if (Randomizer.nextInt(100) < toSpawnChild[1]) {
            c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(toSpawnChild[0]), c.getPlayer().getPosition());
         }
      }
      c.announce(MaplePacketCreator.enableActions());
   }

   private void deleteInventory(MapleClient c, byte inventoryType) {
      MapleInventory in = c.getPlayer().getInventory(MapleInventoryType.getByType(inventoryType));
      for (short i = 1; i <= in.getSlotLimit(); i++) { //TODO What is the point of this loop?
         if (in.getItem(i) != null) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.getByType(inventoryType), i, in.getItem(i).getQuantity(), false);
         }
         return;
      }
   }

   private void setExp(MapleClient c, int amount) {
      c.getPlayer().setExp(amount);
   }

   private void banPlayer(MapleClient c) {
      c.getPlayer().yellowMessage("Please use !ban <IGN> <Reason>");
   }

   private void hidePlayer(MapleClient c, boolean hide) {
      c.getPlayer().hide(hide);
   }

   private void enteringAMap(MapleClient c, byte type) {
      switch (type) {
         case 0:// /u
            StringBuilder sb = new StringBuilder("USERS ON THIS MAP: ");
            for (MapleCharacter mc : c.getPlayer().getMap().getCharacters()) {
               sb.append(mc.getName());
               sb.append(" ");
            }
            MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.PINK_TEXT, sb.toString());
            break;
         case 12:// /uclip and entering a map
            break;
      }
   }

   private void killMonster(MapleClient c, int mobToKill, int amount) {
      List<MapleMapObject> monsterx = c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.MONSTER));
      for (int x = 0; x < amount; x++) {
         MapleMonster monster = (MapleMonster) monsterx.get(x);
         if (monster.getId() == mobToKill) {
            c.getPlayer().getMap().killMonster(monster, c.getPlayer(), true);
         }
      }
   }

   private void questReset(MapleClient c, int questId) {
      MapleQuest.getInstance(questId).reset(c.getPlayer());
   }

   private void summon(MapleClient c, int mobId, int quantity) {
      for (int i = 0; i < quantity; i++) {
         c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), c.getPlayer().getPosition());
      }
   }

   private void monsterHpBroadcast(MapleClient c, int mobHp) {
      MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.NOTICE, "Monsters HP");
      List<MapleMapObject> monsters = c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.MONSTER));
      for (MapleMapObject mobs : monsters) {
         MapleMonster monster = (MapleMonster) mobs;
         if (monster.getId() == mobHp) {
            MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.NOTICE, monster.getName() + ": " + monster.getHp());
         }
      }
   }

   private void testingPacket(int printableInt) {
      System.out.println(printableInt);
   }

   private void changeMapCommand(MapleClient c, String victim, int mapId) {
      c.getChannelServer().getPlayerStorage().getCharacterByName(victim)
            .ifPresent(character -> character.changeMap(c.getChannelServer().getMapFactory().getMap(mapId)));
   }

   private void warnCommand(MapleClient c, String victim, String message) {
      c.getChannelServer().getPlayerStorage().getCharacterByName(victim).ifPresentOrElse(target -> {
         MessageBroadcaster.getInstance().sendServerNotice(target, ServerNoticeType.POP_UP, message);
         c.announce(MaplePacketCreator.getGMEffect(0x1E, (byte) 1));
      }, () -> c.announce(MaplePacketCreator.getGMEffect(0x1E, (byte) 0)));
   }

   private void blockCommand(MapleClient c, String victim, int type, int duration, String description) {
      String reason = c.getPlayer().getName() + " used /ban to ban";

      Optional<MapleCharacter> target = c.getChannelServer().getPlayerStorage().getCharacterByName(victim);
      if (target.isPresent()) {
         String readableTargetName = StringUtil.makeMapleReadable(target.get().getName());
         String ip = target.get().getClient().getSession().getRemoteAddress().toString().split(":")[0];
         reason += readableTargetName + " (IP: " + ip + ")";
         if (duration == -1) {
            target.get().ban(description + " " + reason);
         } else {
            target.get().block(type, duration, description);
            target.get().sendPolice(duration, reason, 6000);
         }
         c.announce(MaplePacketCreator.getGMEffect(4, (byte) 0));
      } else if (BanProcessor.getInstance().ban(victim, reason, false)) {
         c.announce(MaplePacketCreator.getGMEffect(4, (byte) 0));
      } else {
         c.announce(MaplePacketCreator.getGMEffect(6, (byte) 1));
      }
   }
}
