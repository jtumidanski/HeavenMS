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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.mina.core.session.IoSession;

import client.BuddyList;
import client.BuddylistEntry;
import client.CharacterNameAndId;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleDisease;
import client.MapleFamily;
import client.MapleFamilyEntry;
import client.MapleKeyBinding;
import client.SkillFactory;
import client.database.administrator.DueyPackageAdministrator;
import client.database.provider.DueyPackageProvider;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import constants.GameConstants;
import constants.ScriptableNPCConstants;
import constants.ServerConstants;
import net.AbstractMaplePacketHandler;
import net.server.PlayerBuffValueHolder;
import net.server.Server;
import net.server.channel.Channel;
import net.server.channel.CharacterIdChannelPair;
import net.server.coordinator.MapleEventRecallCoordinator;
import net.server.coordinator.MapleSessionCoordinator;
import net.server.guild.MapleAlliance;
import net.server.guild.MapleGuild;
import net.server.world.MaplePartyCharacter;
import net.server.world.PartyOperation;
import net.server.world.World;
import scripting.event.EventInstanceManager;
import server.life.MobSkill;
import tools.DatabaseConnection;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packets.Wedding;

public final class PlayerLoggedinHandler extends AbstractMaplePacketHandler {

   private static Set<Integer> attemptingLoginAccounts = new HashSet<>();

   private void showDueyNotification(MapleClient c, MapleCharacter player) {
      DatabaseConnection.withConnection(connection ->
            DueyPackageProvider.getInstance().getPackageTypeForCharacter(connection, player.getId())
                  .ifPresent(type -> {
                     DueyPackageAdministrator.getInstance().uncheck(connection, player.getId());
                     c.announce(MaplePacketCreator.sendDueyParcelNotification(type == 1));
                  }));
   }

   private static List<Pair<Long, PlayerBuffValueHolder>> getLocalStartTimes(List<PlayerBuffValueHolder> lpbvl) {
      List<Pair<Long, PlayerBuffValueHolder>> timedBuffs = new ArrayList<>();
      long currentServerTime = currentServerTime();

      for (PlayerBuffValueHolder pb : lpbvl) {
         timedBuffs.add(new Pair<>(currentServerTime - pb.usedTime, pb));
      }

      timedBuffs.sort(Comparator.comparing(Pair::getLeft));

      return timedBuffs;
   }

   private boolean tryAcquireAccount(int accId) {
      synchronized (attemptingLoginAccounts) {
         if (attemptingLoginAccounts.contains(accId)) {
            return false;
         }

         attemptingLoginAccounts.add(accId);
         return true;
      }
   }

   private void releaseAccount(int accId) {
      synchronized (attemptingLoginAccounts) {
         attemptingLoginAccounts.remove(accId);
      }
   }

   @Override
   public final boolean validateState(MapleClient c) {
      return !c.isLoggedIn();
   }

   @Override
   public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
      final int cid = slea.readInt();
      final Server server = Server.getInstance();

      if (c.tryAcquireClient()) { // thanks MedicOP for assisting on concurrency protection here
         try {
            World world = server.getWorld(c.getWorld());
            if (world == null) {
               c.disconnect(true, false);
               return;
            }

            Channel channel = world.getChannel(c.getChannel());
            if (channel == null) {
               c.setChannel(1);
               channel = world.getChannel(c.getChannel());

               if (channel == null) {
                  c.disconnect(true, false);
                  return;
               }
            }

            MapleCharacter player = world.getPlayerStorage().getCharacterById(cid).orElse(null);
            boolean newcomer = false;

            IoSession session = c.getSession();
            String remoteHwid;
            if (player == null) {
               if (!server.validateCharacteridInTransition(session, cid)) {
                  c.disconnect(true, false);
                  return;
               }

               remoteHwid = MapleSessionCoordinator.getInstance().getGameSessionHwid(session);
               if (remoteHwid == null) {
                  c.disconnect(true, false);
                  return;
               }

               player = MapleCharacter.loadCharFromDB(cid, c, true);
               newcomer = true;
            } else {
               remoteHwid = player.getClient().getHWID();
            }

            if (player == null) { //If you are still getting null here then please just uninstall the game >.>, we dont need you fucking with the logs
               c.disconnect(true, false);
               return;
            }

            c.setPlayer(player);
            c.setAccID(player.getAccountID());

            boolean allowLogin = true;

                /*  is this check really necessary?
                if (state == MapleClient.LOGIN_SERVER_TRANSITION || state == MapleClient.LOGIN_NOTLOGGEDIN) {
                    List<String> charNames = c.loadCharacterNames(c.getWorld());
                    if(!newcomer) {
                        charNames.remove(player.getName());
                    }

                    for (String charName : charNames) {
                        if(wserv.getPlayerStorage().getCharacterByName(charName) != null) {
                            allowLogin = false;
                            break;
                        }
                    }
                }
                */

            int accId = c.getAccID();
            if (tryAcquireAccount(accId)) { // Sync this to prevent wrong login state for double loggedin handling
               try {
                  int state = c.getLoginState();
                  if (state != MapleClient.LOGIN_SERVER_TRANSITION || !allowLogin) {
                     c.setPlayer(null);
                     c.setAccID(0);

                     if (state == MapleClient.LOGIN_LOGGEDIN) {
                        c.disconnect(true, false);
                     } else {
                        c.announce(MaplePacketCreator.getAfterLoginError(7));
                     }

                     return;
                  }
                  c.updateLoginState(MapleClient.LOGIN_LOGGEDIN);
               } finally {
                  releaseAccount(accId);
               }
            } else {
               c.setPlayer(null);
               c.setAccID(0);
               c.announce(MaplePacketCreator.getAfterLoginError(10));
               return;
            }

            if (!newcomer) {
               c.setLanguage(player.getClient().getLanguage());
               c.setCharacterSlots((byte) player.getClient().getCharacterSlots());
               player.newClient(c);
            }

            int hwidLen = remoteHwid.length();
            session.setAttribute(MapleClient.CLIENT_HWID, remoteHwid);
            session.setAttribute(MapleClient.CLIENT_NIBBLEHWID, remoteHwid.substring(hwidLen - 8, hwidLen));
            c.setHWID(remoteHwid);

            channel.addPlayer(player);
            world.addPlayer(player);
            player.setEnteredChannelWorld();

            List<PlayerBuffValueHolder> buffs = server.getPlayerBuffStorage().getBuffsFromStorage(cid);
            if (buffs != null) {
               List<Pair<Long, PlayerBuffValueHolder>> timedBuffs = getLocalStartTimes(buffs);
               player.silentGiveBuffs(timedBuffs);
            }

            Map<MapleDisease, Pair<Long, MobSkill>> diseases = server.getPlayerBuffStorage().getDiseasesFromStorage(cid);
            if (diseases != null) {
               player.silentApplyDiseases(diseases);
            }

            c.announce(MaplePacketCreator.getCharInfo(player));
            if (!player.isHidden()) {
               if (player.isGM() && ServerConstants.USE_AUTOHIDE_GM) {
                  player.toggleHide(true);
               }
            }
            player.sendKeymap();
            player.sendMacros();

            // pot bindings being passed through other characters on the account detected thanks to Croosade dev team
            MapleKeyBinding autohpPot = player.getKeymap().get(91);
            player.announce(MaplePacketCreator.sendAutoHpPot(autohpPot != null ? autohpPot.getAction() : 0));

            MapleKeyBinding autompPot = player.getKeymap().get(92);
            player.announce(MaplePacketCreator.sendAutoMpPot(autompPot != null ? autompPot.getAction() : 0));

            player.getMap().addPlayer(player);
            player.visitMap(player.getMap());

            BuddyList bl = player.getBuddylist();
            int[] buddyIds = bl.getBuddyIds();
            world.loggedOn(player.getName(), player.getId(), c.getChannel(), buddyIds);
            for (CharacterIdChannelPair onlineBuddy : world.multiBuddyFind(player.getId(), buddyIds)) {
               BuddylistEntry ble = bl.get(onlineBuddy.getCharacterId());
               ble.setChannel(onlineBuddy.getChannel());
               bl.put(ble);
            }
            c.announce(MaplePacketCreator.updateBuddylist(bl.getBuddies()));

            c.announce(MaplePacketCreator.loadFamily(player));
            if (player.getFamilyId() > 0) {
               MapleFamily f = world.getFamily(player.getFamilyId());
               if (f != null) {
                  MapleFamilyEntry familyEntry = f.getEntryByID(player.getId());
                  if (familyEntry != null) {
                     familyEntry.setCharacter(player);
                     player.setFamilyEntry(familyEntry);
                  } else {
                     FilePrinter.printError(FilePrinter.FAMILY_ERROR, "Player " + player.getName() + "'s family doesn't have an entry for them. (" + f.getID() + ")");
                  }
                  c.announce(MaplePacketCreator.getFamilyInfo(familyEntry));
                  familyEntry.announceToSenior(MaplePacketCreator.sendFamilyLoginNotice(player.getName(), true), true);
               } else {
                  FilePrinter.printError(FilePrinter.FAMILY_ERROR, "Player " + player.getName() + " has an invalid family ID. (" + player.getFamilyId() + ")");
                  c.announce(MaplePacketCreator.getFamilyInfo(null));
               }
            } else {
               c.announce(MaplePacketCreator.getFamilyInfo(null));
            }
            if (player.getGuildId() > 0) {
               loggingInGuildOperations(c, server, player, newcomer);
            }

            player.showNote();
            if (player.getParty() != null) {
               loggingInPartyOperations(c, world, player);
            }

            MapleInventory eqpInv = player.getInventory(MapleInventoryType.EQUIPPED);
            eqpInv.lockInventory();
            try {
               for (Item it : eqpInv.list()) {
                  player.equippedItem((Equip) it);
               }
            } finally {
               eqpInv.unlockInventory();
            }

            c.announce(MaplePacketCreator.updateBuddylist(player.getBuddylist().getBuddies()));

            CharacterNameAndId pendingBuddyRequest = c.getPlayer().getBuddylist().pollPendingRequest();
            if (pendingBuddyRequest != null) {
               c.announce(MaplePacketCreator.requestBuddylistAdd(pendingBuddyRequest.getId(), c.getPlayer().getId(), pendingBuddyRequest.getName()));
            }

            c.announce(MaplePacketCreator.updateGender(player));
            player.checkMessenger();
            c.announce(MaplePacketCreator.enableReport());

            int skillId = 10000000 * player.getJobType() + 12;
            player.changeSkillLevel(SkillFactory.getSkill(skillId).orElseThrow(), (byte) (player.getLinkedLevel() / 10), 20, -1);
            player.checkBerserk(player.isHidden());

            if (newcomer) {
               for (MaplePet pet : player.getPets()) {
                  if (pet != null) {
                     world.registerPetHunger(player, player.getPetIndex(pet));
                  }
               }

               player.reloadQuestExpirations();

               if (player.isGM()) {
                  Server.getInstance().broadcastGMMessage(c.getWorld(), MaplePacketCreator.earnTitleMessage((player.gmLevel() < 6 ? "GM " : "Admin ") + player.getName() + " has logged in"));
               }

               if (diseases != null) {
                  for (Entry<MapleDisease, Pair<Long, MobSkill>> e : diseases.entrySet()) {
                     final List<Pair<MapleDisease, Integer>> debuff = Collections.singletonList(new Pair<>(e.getKey(), e.getValue().getRight().getX()));
                     c.announce(MaplePacketCreator.giveDebuff(debuff, e.getValue().getRight()));
                  }

                  player.announceDiseases();
               }
            } else {
               if (player.isRidingBattleship()) {
                  player.announceBattleshipHp();
               }
            }

            player.buffExpireTask();
            player.diseaseExpireTask();
            player.skillCooldownTask();
            player.expirationTask();
            player.questExpirationTask();
            if (GameConstants.hasSPTable(player.getJob()) && player.getJob().getId() != 2001) {
               player.createDragon();
            }

            player.commitExcludedItems();
            showDueyNotification(c, player);

            if (player.getMap().getHPDec() > 0) {
               player.resetHpDecreaseTask();
            }

            player.resetPlayerRates();
            if (ServerConstants.USE_ADD_RATES_BY_LEVEL) {
               player.setPlayerRates();
            }
            player.setWorldRates();
            player.updateCouponRates();

            player.receivePartyMemberHP();

            if (player.getPartnerId() > 0) {
               loggingInPartnerOperations(world, player);
            }

            if (newcomer) {
               EventInstanceManager eim = MapleEventRecallCoordinator.getInstance().recallEventInstance(cid);
               if (eim != null) {
                  eim.registerPlayer(player);
               }
            }

            if (ServerConstants.USE_NPCS_SCRIPTABLE) {
               c.announce(MaplePacketCreator.setNPCScriptable(ScriptableNPCConstants.SCRIPTABLE_NPCS));
            }

            if (newcomer) {
               player.setLoginTime(System.currentTimeMillis());
            }
         } catch (Exception e) {
            e.printStackTrace();
         } finally {
            c.releaseClient();
         }
      } else {
         c.announce(MaplePacketCreator.getAfterLoginError(10));
      }
   }

   private void loggingInPartnerOperations(World world, MapleCharacter player) {
      int partnerId = player.getPartnerId();
      world.getPlayerStorage().getCharacterById(partnerId).filter(partner -> !partner.isAwayFromWorld()).ifPresent(partner -> {
         player.announce(Wedding.OnNotifyWeddingPartnerTransfer(partnerId, partner.getMapId()));
         partner.announce(Wedding.OnNotifyWeddingPartnerTransfer(player.getId(), player.getMapId()));
      });
   }

   private void loggingInPartyOperations(MapleClient c, World wserv, MapleCharacter player) {
      MaplePartyCharacter pchar = player.getMPC();

      //Use this in case of enabling party HPbar HUD when logging in, however "you created a party" will appear on chat.
      //c.announce(MaplePacketCreator.partyCreated(pchar));

      pchar.setChannel(c.getChannel());
      pchar.setMapId(player.getMapId());
      pchar.setOnline(true);
      wserv.updateParty(player.getParty().getId(), PartyOperation.LOG_ONOFF, pchar);
      player.updatePartyMemberHP();
   }

   private void loggingInGuildOperations(MapleClient client, Server server, MapleCharacter player, boolean newcomer) {
      server.getGuild(player.getGuildId(), player.getWorld(), player).ifPresentOrElse(guild -> {
         guild.getMGC(player.getId()).setCharacter(player);
         player.setMGC(guild.getMGC(player.getId()));
         server.setGuildMemberOnline(player, true, client.getChannel());
         client.announce(MaplePacketCreator.showGuildInfo(player));
         loggingInAllianceOperations(client, server, player, newcomer);
      }, () -> {
         player.deleteGuild(player.getGuildId());
         player.getMGC().setGuildId(0);
         player.getMGC().setGuildRank(5);
      });
   }

   private void loggingInAllianceOperations(MapleClient client, Server server, MapleCharacter player, boolean newcomer) {
      int allianceId = player.getGuild().map(MapleGuild::getAllianceId).orElse(0);
      if (allianceId > 0) {
         server.getAlliance(allianceId)
               .or(() -> loadAlliance(server, player, allianceId))
               .ifPresent(alliance -> {
                  client.announce(MaplePacketCreator.updateAllianceInfo(alliance, client.getWorld()));
                  client.announce(MaplePacketCreator.allianceNotice(alliance.getId(), alliance.getNotice()));

                  if (newcomer) {
                     server.allianceMessage(allianceId, MaplePacketCreator.allianceMemberOnline(player, true), player.getId(), -1);
                  }
               });
      }
   }

   private Optional<MapleAlliance> loadAlliance(Server server, MapleCharacter player, int allianceId) {
      Optional<MapleAlliance> alliance = MapleAlliance.loadAlliance(allianceId);
      if (alliance.isPresent()) {
         server.addAlliance(allianceId, alliance.get());
         return alliance;
      } else {
         player.getGuild().ifPresent(guild -> guild.setAllianceId(0));
         return Optional.empty();
      }
   }
}
