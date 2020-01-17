package net.server.channel.handlers;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

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
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Randomizer;
import tools.ServerNoticeType;
import tools.StringUtil;
import tools.I18nMessage;
import tools.packet.stat.EnableActions;
import tools.packet.ui.GMEffect;

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

   private void summonMonsters(MapleClient client, int summonItemId) {
      int[][] toSpawn = MapleItemInformationProvider.getInstance().getSummonMobs(summonItemId);
      for (int[] toSpawnChild : toSpawn) {
         if (Randomizer.nextInt(100) < toSpawnChild[1]) {
            MapleLifeFactory.getMonster(toSpawnChild[0]).ifPresent(monster -> client.getPlayer().getMap().spawnMonsterOnGroundBelow(monster, client.getPlayer().position()));
         }
      }
      PacketCreator.announce(client, new EnableActions());
   }

   private void deleteInventory(MapleClient c, byte inventoryType) {
      MapleInventory in = c.getPlayer().getInventory(MapleInventoryType.getByType(inventoryType));
      for (short i = 1; i <= in.getSlotLimit(); i++) { //TODO What is the point of this loop?
         if (in.getItem(i) != null) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.getByType(inventoryType), i, in.getItem(i).quantity(), false);
         }
         return;
      }
   }

   private void setExp(MapleClient c, int amount) {
      c.getPlayer().setExp(amount);
   }

   private void banPlayer(MapleClient c) {
      MessageBroadcaster.getInstance().yellowMessage(c.getPlayer(), I18nMessage.from("BAN_COMMAND_SYNTAX"));
   }

   private void hidePlayer(MapleClient c, boolean hide) {
      c.getPlayer().hide(hide);
   }

   private void enteringAMap(MapleClient c, byte type) {
      switch (type) {
         case 0:// /u
            String names = c.getPlayer().getMap().getCharacters().stream()
                  .map(MapleCharacter::getName)
                  .collect(StringBuilder::new, (sb, s1) -> sb.append(" ").append(s1), (sb1, sb2) -> sb1.append(sb2.toString()))
                  .toString();
            MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("USERS_IN_MAP").with(names));
            break;
         case 12:
            break;
      }
   }

   private void killMonster(MapleClient c, int mobToKill, int amount) {
      List<MapleMapObject> monsters = c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().position(), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.MONSTER));
      for (int x = 0; x < amount; x++) {
         MapleMonster monster = (MapleMonster) monsters.get(x);
         if (monster.id() == mobToKill) {
            c.getPlayer().getMap().killMonster(monster, c.getPlayer(), true);
         }
      }
   }

   private void questReset(MapleClient c, int questId) {
      MapleQuest.getInstance(questId).reset(c.getPlayer());
   }

   private void summon(MapleClient client, int mobId, int quantity) {
      IntStream.range(0, quantity)
            .mapToObj(index -> MapleLifeFactory.getMonster(mobId))
            .flatMap(Optional::stream)
            .filter(Objects::nonNull)
            .forEach(monster -> client.getPlayer().getMap().spawnMonsterOnGroundBelow(monster, client.getPlayer().position()));
   }

   private void monsterHpBroadcast(MapleClient c, int mobHp) {
      MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.NOTICE, I18nMessage.from("MONSTER_HP_TITLE"));
      List<MapleMapObject> monsters = c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().position(), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.MONSTER));
      for (MapleMapObject mobs : monsters) {
         MapleMonster monster = (MapleMonster) mobs;
         if (monster.id() == mobHp) {
            MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.NOTICE, I18nMessage.from("MONSTER_HP_BODY").with(monster.getName(), monster.getHp()));
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
         PacketCreator.announce(c, new GMEffect(0x1E, (byte) 1));
      }, () -> PacketCreator.announce(c, new GMEffect(0x1E, (byte) 0)));
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
         PacketCreator.announce(c, new GMEffect(4, (byte) 0));
      } else if (BanProcessor.getInstance().ban(victim, reason, false)) {
         PacketCreator.announce(c, new GMEffect(4, (byte) 0));
      } else {
         PacketCreator.announce(c, new GMEffect(6, (byte) 1));
      }
   }
}
