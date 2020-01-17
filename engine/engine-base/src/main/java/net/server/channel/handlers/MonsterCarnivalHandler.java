package net.server.channel.handlers;

import java.awt.Point;
import java.util.List;
import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleAbnormalStatus;
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
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.monster.carnival.MonsterCarnivalMessage;
import tools.packet.monster.carnival.MonsterCarnivalPlayerSummoned;
import tools.packet.stat.EnableActions;

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
                     PacketCreator.announce(client, new MonsterCarnivalMessage((byte) 1));
                     PacketCreator.announce(client, new EnableActions());
                     return;
                  }

                  final MapleMonster mob = MapleLifeFactory.getMonster(mobs.get(num).left).orElseThrow();
                  MonsterCarnival monsterCarnival = client.getPlayer().getMonsterCarnival();
                  if (monsterCarnival != null) {
                     if (!monsterCarnival.canSummonR() && client.getPlayer().getTeam() == 0 || !monsterCarnival.canSummonB() && client.getPlayer().getTeam() == 1) {
                        PacketCreator.announce(client, new MonsterCarnivalMessage((byte) 2));
                        PacketCreator.announce(client, new EnableActions());
                        return;
                     }

                     if (client.getPlayer().getTeam() == 0) {
                        monsterCarnival.summonR();
                     } else {
                        monsterCarnival.summonB();
                     }

                     Point spawnPos = client.getPlayer().getMap().getRandomSP(client.getPlayer().getTeam());
                     mob.position_$eq(spawnPos);

                     client.getPlayer().getMap().addMonsterSpawn(mob, 1, client.getPlayer().getTeam());
                     client.getPlayer().getMap().addAllMonsterSpawn(mob, 1, client.getPlayer().getTeam());
                     PacketCreator.announce(client, new EnableActions());
                  }

                  neededCP = mobs.get(num).right;
               } else if (tab == 1) { //abnormal statuses
                  final List<Integer> skillIds = client.getPlayer().getMap().getSkillIds();
                  if (num >= skillIds.size()) {
                     MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("UNKNOWN_ERROR"));
                     PacketCreator.announce(client, new EnableActions());
                     return;
                  }
                  final MCSkill skill = MapleCarnivalFactory.getInstance().getSkill(skillIds.get(num)); //ugh wtf
                  if (skill == null || client.getPlayer().getCP() < skill.cpLoss) {
                     PacketCreator.announce(client, new MonsterCarnivalMessage((byte) 1));
                     PacketCreator.announce(client, new EnableActions());
                     return;
                  }
                  final MapleAbnormalStatus dis = skill.getDisease();
                  MapleParty enemies = client.getPlayer().getParty().orElseThrow().getEnemy();
                  if (skill.targetsAll) {
                     int hitChance = 0;
                     if (dis.getDisease() == 121 || dis.getDisease() == 122 || dis.getDisease() == 125 || dis.getDisease() == 126) {
                        hitChance = (int) (Math.random() * 100);
                     }
                     if (hitChance <= 80) {
                        enemies.getPartyMembers().parallelStream()
                              .map(MaplePartyCharacter::getPlayer)
                              .flatMap(Optional::stream)
                              .forEach(character -> {
                                 if (dis == null) {
                                    character.dispel();
                                 } else {
                                    character.giveAbnormalStatus(dis, skill.getSkill());
                                 }
                              });
                     }
                  } else {
                     int amount = enemies.getMembers().size() - 1;
                     int random = (int) Math.floor(Math.random() * amount);
                     MapleCharacter chrApp = client.getPlayer().getMap().getCharacterById(enemies.getMemberByPos(random).getId());
                     if (chrApp != null && chrApp.getMap().isCPQMap()) {
                        if (dis == null) {
                           chrApp.dispel();
                        } else {
                           chrApp.giveAbnormalStatus(dis, skill.getSkill());
                        }
                     }
                  }
                  neededCP = skill.cpLoss;
                  PacketCreator.announce(client, new EnableActions());
               } else if (tab == 2) { //protectors
                  final MCSkill skill = MapleCarnivalFactory.getInstance().getGuardian(num);
                  if (skill == null || client.getPlayer().getCP() < skill.cpLoss) {
                     PacketCreator.announce(client, new MonsterCarnivalMessage((byte) 1));
                     PacketCreator.announce(client, new EnableActions());
                     return;
                  }

                  MonsterCarnival monsterCarnival = client.getPlayer().getMonsterCarnival();
                  if (monsterCarnival != null) {
                     if (!monsterCarnival.canGuardianR() && client.getPlayer().getTeam() == 0 || !monsterCarnival.canGuardianB() && client.getPlayer().getTeam() == 1) {
                        PacketCreator.announce(client, new MonsterCarnivalMessage((byte) 2));
                        PacketCreator.announce(client, new EnableActions());
                        return;
                     }

                     int success = client.getPlayer().getMap().spawnGuardian(client.getPlayer().getTeam(), num);
                     if (success != 1) {
                        switch (success) {
                           case -1:
                              PacketCreator.announce(client, new MonsterCarnivalMessage((byte) 3));
                              break;
                           case 0:
                              PacketCreator.announce(client, new MonsterCarnivalMessage((byte) 4));
                              break;
                           default:
                              PacketCreator.announce(client, new MonsterCarnivalMessage((byte) 3));
                        }
                        PacketCreator.announce(client, new EnableActions());
                        return;
                     } else {
                        neededCP = skill.cpLoss;
                     }
                  }
               }
               client.getPlayer().gainCP(-neededCP);
               MasterBroadcaster.getInstance().sendToAllInMap(client.getPlayer().getMap(), new MonsterCarnivalPlayerSummoned(client.getPlayer().getName(), tab, num));
            } catch (Exception e) {
               e.printStackTrace();
            }
         } finally {
            client.releaseClient();
         }
      }
   }
}
