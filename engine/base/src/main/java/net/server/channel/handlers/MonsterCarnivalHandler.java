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

import java.awt.Point;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleDisease;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.MonsterCarnivalPacket;
import net.server.channel.packet.reader.MonsterCarnivalReader;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.partyquest.MapleCarnivalFactory;
import server.partyquest.MapleCarnivalFactory.MCSkill;
import server.partyquest.MonsterCarnival;
import tools.MaplePacketCreator;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.ServerNoticeType;
import tools.packet.stat.EnableActions;


/**
 * @author Drago/Dragohe4rt
 */

public final class MonsterCarnivalHandler extends AbstractPacketHandler<MonsterCarnivalPacket> {
   @Override
   public Class<MonsterCarnivalReader> getReaderClass() {
      return MonsterCarnivalReader.class;
   }

   @Override
   public void handlePacket(MonsterCarnivalPacket packet, MapleClient client) {
      if (client.tryAcquireClient()) {
         try {
            try {
               int tab = packet.tab();
               int num = packet.num();
               int neededCP = 0;
               if (tab == 0) {
                  final List<Pair<Integer, Integer>> mobs = client.getPlayer().getMap().getMobsToSpawn();
                  if (num >= mobs.size() || client.getPlayer().getCP() < mobs.get(num).right) {
                     client.announce(MaplePacketCreator.CPQMessage((byte) 1));
                     PacketCreator.announce(client, new EnableActions());
                     return;
                  }

                  final MapleMonster mob = MapleLifeFactory.getMonster(mobs.get(num).left);
                  MonsterCarnival mcpq = client.getPlayer().getMonsterCarnival();
                  if (mcpq != null) {
                     if (!mcpq.canSummonR() && client.getPlayer().getTeam() == 0 || !mcpq.canSummonB() && client.getPlayer().getTeam() == 1) {
                        client.announce(MaplePacketCreator.CPQMessage((byte) 2));
                        PacketCreator.announce(client, new EnableActions());
                        return;
                     }

                     if (client.getPlayer().getTeam() == 0) {
                        mcpq.summonR();
                     } else {
                        mcpq.summonB();
                     }

                     Point spawnPos = client.getPlayer().getMap().getRandomSP(client.getPlayer().getTeam());
                     mob.setPosition(spawnPos);

                     client.getPlayer().getMap().addMonsterSpawn(mob, 1, client.getPlayer().getTeam());
                     client.getPlayer().getMap().addAllMonsterSpawn(mob, 1, client.getPlayer().getTeam());
                     PacketCreator.announce(client, new EnableActions());
                  }

                  neededCP = mobs.get(num).right;
               } else if (tab == 1) { //debuffs
                  final List<Integer> skillid = client.getPlayer().getMap().getSkillIds();
                  if (num >= skillid.size()) {
                     MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.PINK_TEXT, "An unexpected error has occurred.");
                     PacketCreator.announce(client, new EnableActions());
                     return;
                  }
                  final MCSkill skill = MapleCarnivalFactory.getInstance().getSkill(skillid.get(num)); //ugh wtf
                  if (skill == null || client.getPlayer().getCP() < skill.cpLoss) {
                     client.announce(MaplePacketCreator.CPQMessage((byte) 1));
                     PacketCreator.announce(client, new EnableActions());
                     return;
                  }
                  final MapleDisease dis = skill.getDisease();
                  MapleParty enemies = client.getPlayer().getParty().getEnemy();
                  if (skill.targetsAll) {
                     int hitChance = 0;
                     if (dis.getDisease() == 121 || dis.getDisease() == 122 || dis.getDisease() == 125 || dis.getDisease() == 126) {
                        hitChance = (int) (Math.random() * 100);
                     }
                     if (hitChance <= 80) {
                        for (MaplePartyCharacter mpc : enemies.getPartyMembers()) {
                           MapleCharacter mc = mpc.getPlayer();
                           if (mc != null) {
                              if (dis == null) {
                                 mc.dispel();
                              } else {
                                 mc.giveDebuff(dis, skill.getSkill());
                              }
                           }
                        }
                     }
                  } else {
                     int amount = enemies.getMembers().size() - 1;
                     int randd = (int) Math.floor(Math.random() * amount);
                     MapleCharacter chrApp = client.getPlayer().getMap().getCharacterById(enemies.getMemberByPos(randd).getId());
                     if (chrApp != null && chrApp.getMap().isCPQMap()) {
                        if (dis == null) {
                           chrApp.dispel();
                        } else {
                           chrApp.giveDebuff(dis, skill.getSkill());
                        }
                     }
                  }
                  neededCP = skill.cpLoss;
                  PacketCreator.announce(client, new EnableActions());
               } else if (tab == 2) { //protectors
                  final MCSkill skill = MapleCarnivalFactory.getInstance().getGuardian(num);
                  if (skill == null || client.getPlayer().getCP() < skill.cpLoss) {
                     client.announce(MaplePacketCreator.CPQMessage((byte) 1));
                     PacketCreator.announce(client, new EnableActions());
                     return;
                  }

                  MonsterCarnival mcpq = client.getPlayer().getMonsterCarnival();
                  if (mcpq != null) {
                     if (!mcpq.canGuardianR() && client.getPlayer().getTeam() == 0 || !mcpq.canGuardianB() && client.getPlayer().getTeam() == 1) {
                        client.announce(MaplePacketCreator.CPQMessage((byte) 2));
                        PacketCreator.announce(client, new EnableActions());
                        return;
                     }

                     int success = client.getPlayer().getMap().spawnGuardian(client.getPlayer().getTeam(), num);
                     if (success != 1) {
                        switch (success) {
                           case -1:
                              client.announce(MaplePacketCreator.CPQMessage((byte) 3));
                              break;

                           case 0:
                              client.announce(MaplePacketCreator.CPQMessage((byte) 4));
                              break;

                           default:
                              client.announce(MaplePacketCreator.CPQMessage((byte) 3));
                        }
                        PacketCreator.announce(client, new EnableActions());
                        return;
                     } else {
                        neededCP = skill.cpLoss;
                     }
                  }
               }
               client.getPlayer().gainCP(-neededCP);
               MasterBroadcaster.getInstance().sendToAllInMap(client.getPlayer().getMap(), character -> MaplePacketCreator.playerSummoned(client.getPlayer().getName(), tab, num));
            } catch (Exception e) {
               e.printStackTrace();
            }
         } finally {
            client.releaseClient();
         }
      }
   }
}
