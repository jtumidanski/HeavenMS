package net.server.processor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import client.MapleCharacter;
import config.YamlConfig;
import net.server.Server;
import net.server.coordinator.matchchecker.MapleMatchCheckerCoordinator;
import net.server.coordinator.matchchecker.MatchCheckerListenerFactory;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import net.server.world.PartyOperation;
import net.server.world.World;
import scripting.event.EventInstanceManager;
import server.maps.MapleMap;
import server.maps.MapleMiniDungeon;
import server.maps.MapleMiniDungeonInfo;
import server.partyquest.MonsterCarnival;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.party.PartyCreated;
import tools.packet.party.PartyStatusMessage;
import tools.packet.party.UpdateParty;

public class MaplePartyProcessor {
   private static MaplePartyProcessor ourInstance = new MaplePartyProcessor();

   public static MaplePartyProcessor getInstance() {
      return ourInstance;
   }

   private MaplePartyProcessor() {
   }

   public boolean createParty(MapleCharacter player, boolean silentCheck) {
      Optional<MapleParty> party = player.getParty();
      if (party.isEmpty()) {
         if (player.getLevel() < 10 && !YamlConfig.config.server.USE_PARTY_FOR_STARTERS) {
            PacketCreator.announce(player, new PartyStatusMessage(10));
            return false;
         } else if (player.getAriantColiseum() != null) {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("PARTY_CANNOT_BE_CREATED_IN_ARIANT_BATTLE"));
            return false;
         }

         MaplePartyCharacter partyCharacter = new MaplePartyCharacter(player);
         party = Optional.of(player.getWorldServer().createParty(partyCharacter));
         player.setParty(party.get());
         player.setMPC(partyCharacter);
         player.getMap().addPartyMember(player, party.get().getId());
         player.silentPartyUpdate();

         player.updatePartySearchAvailability(false);
         player.partyOperationUpdate(party.get(), null);

         PacketCreator.announce(player, new PartyCreated(party.get(), partyCharacter.getId()));

         return true;
      } else {
         if (!silentCheck) {
            PacketCreator.announce(player, new PartyStatusMessage(16));
         }

         return false;
      }
   }

   public boolean joinParty(MapleCharacter player, int partyId, boolean silentCheck) {
      World world = player.getWorldServer();

      Optional<MapleParty> party = player.getParty();
      if (party.isEmpty()) {
         party = world.getParty(partyId);
         if (party.isPresent()) {
            if (party.get().getMembers().size() < 6) {
               MaplePartyCharacter partyCharacter = new MaplePartyCharacter(player);
               player.getMap().addPartyMember(player, party.get().getId());

               updateParty(party.get(), PartyOperation.JOIN, partyCharacter);
               player.receivePartyMemberHP();
               player.updatePartyMemberHP();

               player.resetPartySearchInvite(party.get().getLeaderId());
               player.updatePartySearchAvailability(false);
               player.partyOperationUpdate(party.get(), null);
               return true;
            } else {
               if (!silentCheck) {
                  PacketCreator.announce(player, new PartyStatusMessage(17));
               }
            }
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("PARTY_JOIN_ERROR_DISBANDED"));
         }
      } else {
         if (!silentCheck) {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("PARTY_JOIN_ERROR_ALREADY_IN"));
         }
      }

      return false;
   }

   public void leaveParty(MapleParty party, MapleCharacter player) {
      MaplePartyCharacter partyCharacter = player.getMPC();

      if (party != null && partyCharacter != null) {
         if (partyCharacter.getId() == party.getLeaderId()) {
            player.getClient().getWorldServer().removeMapPartyMembers(party.getId());

            MonsterCarnival monsterCarnival = player.getMonsterCarnival();
            if (monsterCarnival != null) {
               monsterCarnival.leftParty(player.getId());
            }

            updateParty(party, PartyOperation.DISBAND, partyCharacter);

            EventInstanceManager eim = player.getEventInstance();
            if (eim != null) {
               eim.disbandParty();
            }
         } else {
            MapleMap map = player.getMap();
            if (map != null) {
               map.removePartyMember(player, party.getId());
            }

            MonsterCarnival monsterCarnival = player.getMonsterCarnival();
            if (monsterCarnival != null) {
               monsterCarnival.leftParty(player.getId());
            }

            updateParty(party, PartyOperation.LEAVE, partyCharacter);

            EventInstanceManager eim = player.getEventInstance();
            if (eim != null) {
               eim.leftParty(player);
            }
         }

         player.setParty(null);

         MapleMatchCheckerCoordinator matchCheckerCoordinator = player.getClient().getWorldServer().getMatchCheckerCoordinator();
         if (matchCheckerCoordinator.getMatchConfirmationLeaderId(player.getId()) == player.getId() && matchCheckerCoordinator.getMatchConfirmationType(player.getId()) == MatchCheckerListenerFactory.MatchCheckerType.GUILD_CREATION) {
            matchCheckerCoordinator.dismissMatchConfirmation(player.getId());
         }
      }
   }

   public void expelFromParty(MapleParty party, MapleCharacter player, int expelCid) {
      MaplePartyCharacter partyCharacter = player.getMPC();

      if (party != null && partyCharacter != null) {
         if (partyCharacter.equals(party.getLeader())) {
            party.getMemberById(expelCid).ifPresent(expelled -> expelled.getPlayer().ifPresentOrElse(emc -> {
               List<MapleCharacter> partyMembers = emc.getPartyMembersOnline();

               MapleMap map = emc.getMap();
               if (map != null) {
                  map.removePartyMember(emc, party.getId());
               }

               MonsterCarnival monsterCarnival = player.getMonsterCarnival();
               if (monsterCarnival != null) {
                  monsterCarnival.leftParty(emc.getId());
               }

               EventInstanceManager eim = emc.getEventInstance();
               if (eim != null) {
                  eim.leftParty(emc);
               }

               emc.setParty(null);
               updateParty(party, PartyOperation.EXPEL, expelled);

               emc.updatePartySearchAvailability(true);
               emc.partyOperationUpdate(party, partyMembers);
            }, () -> updateParty(party, PartyOperation.EXPEL, expelled)));
         }
      }
   }

   public void assignNewLeader(MapleParty party) {
      party.assignNewLeader().ifPresent(maplePartyCharacter -> updateParty(party, PartyOperation.CHANGE_LEADER, maplePartyCharacter));
   }

   public void updateParty(MapleParty party, PartyOperation operation, MaplePartyCharacter target) {
      if (party == null) {
         throw new IllegalArgumentException("no party with the specified party id exists");
      }

      switch (operation) {
         case JOIN:
            party.addMember(target);
            break;
         case LEAVE:
         case EXPEL:
            party.removeMember(target);
            break;
         case DISBAND:
            Server.getInstance().getWorld(party.getWorldId()).disbandParty(party.getId());
            break;
         case SILENT_UPDATE:
         case LOG_ON_OFF:
            party.updateMember(target);
            break;
         case CHANGE_LEADER:
            party.getLeader().getPlayer().ifPresent(leader -> {
               EventInstanceManager eim = leader.getEventInstance();
               if (eim != null && eim.isEventLeader(leader)) {
                  eim.changedLeader(target);
               } else {
                  int oldLeaderMapId = leader.getMapId();

                  if (MapleMiniDungeonInfo.isDungeonMap(oldLeaderMapId)) {
                     if (oldLeaderMapId != target.getMapId()) {
                        MapleMiniDungeon mmd = leader.getClient().getChannelServer().getMiniDungeon(oldLeaderMapId);
                        if (mmd != null) {
                           mmd.close();
                        }
                     }
                  }
               }
               party.setLeader(target);
            });
            break;
         default:
            System.out.println("Unhandled updateParty operation " + operation.name());
      }

      Collection<MaplePartyCharacter> partyMembers = party.getMembers();
      updateCharacterParty(party, operation, target, partyMembers);

      for (MaplePartyCharacter partyCharacter : partyMembers) {
         Server.getInstance().getWorld(party.getWorldId()).getPlayerStorage().getCharacterById(partyCharacter.getId()).ifPresent(character -> {
            if (operation == PartyOperation.DISBAND) {
               character.setParty(null);
               character.setMPC(null);
            } else {
               character.setParty(party);
               character.setMPC(partyCharacter);
            }
            PacketCreator.announce(character, new UpdateParty(character.getClient().getChannel(), party, operation, target));
         });
      }

      switch (operation) {
         case LEAVE:
         case EXPEL:
            Server.getInstance().getWorld(party.getWorldId()).getPlayerStorage().getCharacterById(target.getId()).ifPresent(character -> {
               PacketCreator.announce(character, new UpdateParty(character.getClient().getChannel(), party, operation, target));
               character.setParty(null);
               character.setMPC(null);
            });
         default:
            break;
      }
   }

   private void updateCharacterParty(MapleParty party, PartyOperation operation, MaplePartyCharacter target, Collection<MaplePartyCharacter> partyMembers) {
      switch (operation) {
         case JOIN:
            Server.getInstance().getWorld(party.getWorldId()).registerCharacterParty(target.getId(), party.getId());
            break;
         case LEAVE:
         case EXPEL:
            Server.getInstance().getWorld(party.getWorldId()).unregisterCharacterParty(target.getId());
            break;
         case DISBAND:
            partyMembers.forEach(partyCharacter -> Server.getInstance().getWorld(party.getWorldId()).unregisterCharacterParty(partyCharacter.getId()));
            break;
         default:
            break;
      }
   }
}
