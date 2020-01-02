package net.server.channel.handlers;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.mina.core.session.IoSession;

import client.CharacterNameAndId;
import client.KeyBinding;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleAbnormalStatus;
import client.MapleFamily;
import client.MapleFamilyEntry;
import client.MapleMount;
import client.SkillFactory;
import database.administrator.DueyPackageAdministrator;
import database.provider.DueyPackageProvider;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.processor.BuddyListProcessor;
import client.processor.CharacterProcessor;
import config.YamlConfig;
import constants.game.GameConstants;
import constants.game.ScriptableNPCConstants;
import net.server.AbstractPacketHandler;
import net.server.PlayerBuffValueHolder;
import net.server.Server;
import net.server.channel.Channel;
import net.server.channel.packet.PlayerLoggedInPacket;
import net.server.channel.packet.reader.PlayerLoggedInReader;
import net.server.coordinator.session.MapleSessionCoordinator;
import net.server.coordinator.world.MapleEventRecallCoordinator;
import net.server.guild.MapleAlliance;
import net.server.guild.MapleGuild;
import net.server.processor.MapleAllianceProcessor;
import net.server.processor.MapleGuildProcessor;
import net.server.processor.MaplePartyProcessor;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import net.server.world.PartyOperation;
import net.server.world.World;
import scala.Option;
import scripting.event.EventInstanceManager;
import server.life.MobSkill;
import database.DatabaseConnection;
import tools.FilePrinter;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.packet.AfterLoginError;
import tools.packet.EnableReport;
import tools.packet.SetNPCScriptable;
import tools.packet.alliance.AllianceMemberOnline;
import tools.packet.alliance.AllianceNotice;
import tools.packet.alliance.UpdateAllianceInfo;
import tools.packet.buddy.RequestAddBuddy;
import tools.packet.buddy.UpdateBuddyList;
import tools.packet.buff.GiveAbnormalStatus;
import tools.packet.character.SetAutoHpPot;
import tools.packet.character.SetAutoMpPot;
import tools.packet.character.UpdateGender;
import tools.packet.character.UpdateMount;
import tools.packet.family.FamilyLogonNotice;
import tools.packet.family.GetFamilyInfo;
import tools.packet.family.LoadFamily;
import tools.packet.field.set.GetCharacterInfo;
import tools.packet.foreigneffect.ShowTitleEarned;
import tools.packet.guild.ShowGuildInfo;
import tools.packet.parcel.DueyParcelNotification;
import tools.packet.wedding.WeddingPartnerTransfer;

public final class PlayerLoggedInHandler extends AbstractPacketHandler<PlayerLoggedInPacket> {
   private static Set<Integer> attemptingLoginAccounts = new HashSet<>();

   @Override
   public Class<PlayerLoggedInReader> getReaderClass() {
      return PlayerLoggedInReader.class;
   }

   private void showDueyNotification(MapleClient c, MapleCharacter player) {
      DatabaseConnection.getInstance().withConnection(connection ->
            DueyPackageProvider.getInstance().getPackageTypeForCharacter(connection, player.getId())
                  .ifPresent(type -> {
                     DueyPackageAdministrator.getInstance().uncheck(connection, player.getId());
                     PacketCreator.announce(c, new DueyParcelNotification(type == 1));
                  }));
   }

   private List<Pair<Long, PlayerBuffValueHolder>> getLocalStartTimes(List<PlayerBuffValueHolder> playerBuffs) {
      long currentServerTime = currentServerTime();
      return playerBuffs.stream()
            .map(playerBuffValueHolder -> new Pair<>(currentServerTime - playerBuffValueHolder.usedTime, playerBuffValueHolder))
            .sorted(Comparator.comparing(Pair::getLeft))
            .collect(Collectors.toList());
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
   public final boolean validateState(MapleClient client) {
      return !client.isLoggedIn();
   }


   @Override
   public void handlePacket(PlayerLoggedInPacket packet, MapleClient client) {
      final Server server = Server.getInstance();

      if (client.tryAcquireClient()) {
         try {
            World world = server.getWorld(client.getWorld());
            if (world == null) {
               client.disconnect(true, false);
               return;
            }

            Channel channel = world.getChannel(client.getChannel());
            if (channel == null) {
               client.setChannel(1);
               channel = world.getChannel(client.getChannel());

               if (channel == null) {
                  client.disconnect(true, false);
                  return;
               }
            }

            MapleCharacter player = world.getPlayerStorage().getCharacterById(packet.characterId()).orElse(null);
            IoSession session = client.getSession();
            String remoteHwid;

            if (player == null) {
               remoteHwid = MapleSessionCoordinator.getInstance().pickLoginSessionHwid(session);
               if (remoteHwid == null) {
                  client.disconnect(true, false);
                  return;
               }
            } else {
               remoteHwid = player.getClient().getHWID();
            }

            int hwidLen = remoteHwid.length();
            session.setAttribute(MapleClient.CLIENT_HWID, remoteHwid);
            session.setAttribute(MapleClient.CLIENT_NIBBLE_HWID, remoteHwid.substring(hwidLen - 8, hwidLen));
            client.setHWID(remoteHwid);

            if (!server.validateCharacterIdInTransition(client, packet.characterId())) {
               client.disconnect(true, false);
               return;
            }

            boolean newcomer = false;
            if (player == null) {
               player = CharacterProcessor.getInstance().loadCharFromDB(packet.characterId(), client, true);
               newcomer = true;

               if (player == null) { //If you are still getting null here then please just uninstall the game >.>, we dont need you fucking with the logs
                  client.disconnect(true, false);
                  return;
               }
            }

            client.setPlayer(player);
            client.setAccID(player.getAccountID());

            boolean allowLogin = true;

                /*  is this check really necessary?
                if (state == MapleClient.LOGIN_SERVER_TRANSITION || state == MapleClient.LOGIN_NOT_LOGGED_IN) {
                    List<String> charNames = c.loadCharacterNames(c.getWorld());
                    if(!newcomer) {
                        charNames.remove(player.getName());
                    }

                    for (String charName : charNames) {
                        if(world.getPlayerStorage().getCharacterByName(charName) != null) {
                            allowLogin = false;
                            break;
                        }
                    }
                }
                */

            int accId = client.getAccID();
            if (tryAcquireAccount(accId)) { // Sync this to prevent wrong login state for double logged in handling
               try {
                  int state = client.getLoginState();
                  if (state != MapleClient.LOGIN_SERVER_TRANSITION || !allowLogin) {
                     client.setPlayer(null);
                     client.setAccID(0);

                     if (state == MapleClient.LOGIN_LOGGED_IN) {
                        client.disconnect(true, false);
                     } else {
                        PacketCreator.announce(client, new AfterLoginError(7));
                     }

                     return;
                  }
                  client.updateLoginState(MapleClient.LOGIN_LOGGED_IN);
               } finally {
                  releaseAccount(accId);
               }
            } else {
               client.setPlayer(null);
               client.setAccID(0);
               PacketCreator.announce(client, new AfterLoginError(10));
               return;
            }

            if (!newcomer) {
               client.setLanguage(player.getClient().getLanguage());
               client.setCharacterSlots((byte) player.getClient().getCharacterSlots());
               player.newClient(client);
            }

            channel.addPlayer(player);
            world.addPlayer(player);
            player.setEnteredChannelWorld();

            List<PlayerBuffValueHolder> buffs = server.getPlayerBuffStorage().getBuffsFromStorage(packet.characterId());
            if (buffs != null) {
               List<Pair<Long, PlayerBuffValueHolder>> timedBuffs = getLocalStartTimes(buffs);
               player.silentGiveBuffs(timedBuffs);
            }

            Map<MapleAbnormalStatus, Pair<Long, MobSkill>> diseases = server.getPlayerBuffStorage().getDiseasesFromStorage(packet.characterId());
            if (diseases != null) {
               player.silentApplyDiseases(diseases);
            }

            PacketCreator.announce(client, new GetCharacterInfo(player));
            if (!player.isHidden()) {
               if (player.isGM() && YamlConfig.config.server.USE_AUTOHIDE_GM) {
                  player.toggleHide(true);
               }
            }
            player.sendKeymap();
            player.sendQuickMap();
            player.sendMacros();

            KeyBinding autoHpPot = player.getKeymap().get(91);
            PacketCreator.announce(player, new SetAutoHpPot(autoHpPot != null ? autoHpPot.action() : 0));

            KeyBinding autoMpPot = player.getKeymap().get(92);
            PacketCreator.announce(player, new SetAutoMpPot(autoMpPot != null ? autoMpPot.action() : 0));

            player.getMap().addPlayer(player);
            player.visitMap(player.getMap());

            BuddyListProcessor.getInstance().onLogin(player);

            PacketCreator.announce(client, new LoadFamily());
            if (player.getFamilyId() > 0) {
               MapleFamily f = world.getFamily(player.getFamilyId());
               if (f != null) {
                  MapleFamilyEntry familyEntry = f.getEntryByID(player.getId());
                  if (familyEntry != null) {
                     familyEntry.setCharacter(player);
                     player.setFamilyEntry(familyEntry);
                     PacketCreator.announce(client, new GetFamilyInfo(familyEntry));
                     byte[] familyLoginNotice = PacketCreator.create(new FamilyLogonNotice(player.getName(), true));
                     MasterBroadcaster.getInstance().sendToSenior(familyEntry.getSenior(), character -> familyLoginNotice, true);
                  } else {
                     FilePrinter.printError(FilePrinter.FAMILY_ERROR, "Player " + player.getName() + "'s family doesn't have an entry for them. (" + f.getID() + ")");
                  }
               } else {
                  FilePrinter.printError(FilePrinter.FAMILY_ERROR, "Player " + player.getName() + " has an invalid family ID. (" + player.getFamilyId() + ")");
                  PacketCreator.announce(client, new GetFamilyInfo(null));
               }
            } else {
               PacketCreator.announce(client, new GetFamilyInfo(null));
            }
            if (player.getGuildId() > 0) {
               loggingInGuildOperations(client, server, player, newcomer);
            }

            player.showNote();
            if (player.getParty().isPresent()) {
               loggingInPartyOperations(client, player, player.getParty().get());
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

            PacketCreator.announce(client, new UpdateBuddyList(player.getBuddyList().getBuddies()));

            Option<CharacterNameAndId> pendingBuddyRequest = client.getPlayer().getBuddyList().pollPendingRequest();
            if (pendingBuddyRequest.isDefined()) {
               PacketCreator.announce(client, new RequestAddBuddy(pendingBuddyRequest.get().id(), client.getPlayer().getId(), pendingBuddyRequest.get().name()));
            }

            PacketCreator.announce(client, new UpdateGender(player.getGender()));
            player.checkMessenger();
            PacketCreator.announce(client, new EnableReport());

            int skillId = 10000000 * player.getJobType() + 12;
            player.changeSkillLevel(SkillFactory.getSkill(skillId).orElseThrow(), (byte) (player.getLinkedLevel() / 10), 20, -1);
            player.checkBerserk(player.isHidden());

            if (newcomer) {
               for (MaplePet pet : player.getPets()) {
                  if (pet != null) {
                     world.registerPetHunger(player, player.getPetIndex(pet));
                  }
               }

               MapleMount mount = player.getMount();
               if (mount.itemId() != 0) {
                  PacketCreator.announce(player, new UpdateMount(player.getId(), mount.level(), mount.exp(), mount.tiredness(), false));
               }

               player.reloadQuestExpiration();

               if (player.isGM()) {
                  Server.getInstance().broadcastGMMessage(client.getWorld(), PacketCreator.create(new ShowTitleEarned((player.gmLevel() < 6 ? "GM " : "Admin ") + player.getName() + " has logged in")));
               }

               if (diseases != null) {
                  for (Entry<MapleAbnormalStatus, Pair<Long, MobSkill>> e : diseases.entrySet()) {
                     final List<Pair<MapleAbnormalStatus, Integer>> abnormalStatuses = Collections.singletonList(new Pair<>(e.getKey(), e.getValue().getRight().x()));
                     PacketCreator.announce(client, new GiveAbnormalStatus(abnormalStatuses, e.getValue().getRight()));
                  }
               }
            } else {
               if (player.isRidingBattleship()) {
                  player.announceBattleshipHp();
               }
            }

            player.buffExpireTask();
            player.diseaseExpireTask();
            player.skillCoolDownTask();
            player.expirationTask();
            player.questExpirationTask();
            if (GameConstants.hasSPTable(player.getJob()) && player.getJob().getId() != 2001) {
               player.createDragon();
            }

            player.commitExcludedItems();
            showDueyNotification(client, player);

            if (player.getMap().getHPDec() > 0) {
               player.resetHpDecreaseTask();
            }

            player.resetPlayerRates();
            if (YamlConfig.config.server.USE_ADD_RATES_BY_LEVEL) {
               player.setPlayerRates();
            }
            player.setWorldRates();
            player.updateCouponRates();

            player.receivePartyMemberHP();

            if (player.getPartnerId() > 0) {
               loggingInPartnerOperations(world, player);
            }

            if (newcomer) {
               EventInstanceManager eim = MapleEventRecallCoordinator.getInstance().recallEventInstance(packet.characterId());
               if (eim != null) {
                  eim.registerPlayer(player);
               }
            }

            if (YamlConfig.config.server.USE_NPCS_SCRIPTABLE) {
               PacketCreator.announce(client, new SetNPCScriptable(ScriptableNPCConstants.SCRIPTABLE_NPCS));
            }

            if (newcomer) {
               player.setLoginTime(System.currentTimeMillis());
            }
         } catch (Exception e) {
            e.printStackTrace();
         } finally {
            client.releaseClient();
         }
      } else {
         PacketCreator.announce(client, new AfterLoginError(10));
      }
   }

   private void loggingInPartnerOperations(World world, MapleCharacter player) {
      int partnerId = player.getPartnerId();
      world.getPlayerStorage().getCharacterById(partnerId)
            .filter(partner -> !partner.isAwayFromWorld())
            .ifPresent(partner -> {
               PacketCreator.announce(player, new WeddingPartnerTransfer(partnerId, partner.getMapId()));
               PacketCreator.announce(partner, new WeddingPartnerTransfer(player.getId(), player.getMapId()));
            });
   }

   private void loggingInPartyOperations(MapleClient c, MapleCharacter player, MapleParty party) {
      MaplePartyCharacter partyCharacter = player.getMPC();

      //Use this in case of enabling party HP bar HUD when logging in, however "you created a party" will appear on chat.
      //c.announce(MaplePacketCreator.partyCreated(partyCharacter));

      partyCharacter.setChannel(c.getChannel());
      partyCharacter.setMapId(player.getMapId());
      partyCharacter.setOnline(true);
      MaplePartyProcessor.getInstance().updateParty(party, PartyOperation.LOG_ON_OFF, partyCharacter);
      player.updatePartyMemberHP();
   }

   private void loggingInGuildOperations(MapleClient client, Server server, MapleCharacter player, boolean newcomer) {
      server.getGuild(player.getGuildId(), player.getWorld(), player).ifPresentOrElse(guild -> {
         guild.findMember(player.getId()).ifPresent(reference -> {
            reference.setCharacter(player);
            player.setMGC(reference);
         });

         MapleGuildProcessor.getInstance().setMemberOnline(player, true, client.getChannel());
         PacketCreator.announce(client, new ShowGuildInfo(player));
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
                  PacketCreator.announce(client, new UpdateAllianceInfo(alliance, client.getWorld()));
                  PacketCreator.announce(client, new AllianceNotice(alliance.id(), alliance.notice()));

                  if (newcomer) {
                     server.allianceMessage(allianceId, new AllianceMemberOnline(allianceId, player.getGuildId(), player.getId(), true), player.getId(), -1);
                  }
               });
      }
   }

   private Optional<MapleAlliance> loadAlliance(Server server, MapleCharacter player, int allianceId) {
      Optional<MapleAlliance> alliance = MapleAllianceProcessor.getInstance().loadAlliance(allianceId);
      if (alliance.isPresent()) {
         server.addAlliance(allianceId, alliance.get());
         return alliance;
      } else {
         player.getGuild().ifPresent(guild -> MapleGuildProcessor.getInstance().setGuildAllianceId(guild, 0));
         return Optional.empty();
      }
   }
}
