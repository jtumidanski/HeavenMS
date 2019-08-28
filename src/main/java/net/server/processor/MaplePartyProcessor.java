package net.server.processor;

import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import constants.ServerConstants;
import net.server.coordinator.MapleMatchCheckerCoordinator;
import net.server.coordinator.matchchecker.MatchCheckerListenerFactory;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import net.server.world.PartyOperation;
import net.server.world.World;
import scripting.event.EventInstanceManager;
import server.maps.MapleMap;
import server.partyquest.MonsterCarnival;
import tools.MaplePacketCreator;

public class MaplePartyProcessor {
   private static MaplePartyProcessor ourInstance = new MaplePartyProcessor();

   public static MaplePartyProcessor getInstance() {
      return ourInstance;
   }

   private MaplePartyProcessor() {
   }

   public boolean createParty(MapleCharacter player, boolean silentCheck) {
      MapleParty party = player.getParty();
      if (party == null) {
         if (player.getLevel() < 10 && !ServerConstants.USE_PARTY_FOR_STARTERS) {
            player.announce(MaplePacketCreator.partyStatusMessage(10));
            return false;
         } else if (player.getAriantColiseum() != null) {
            player.dropMessage(5, "You cannot request a party creation while participating the Ariant Battle Arena.");
            return false;
         }

         MaplePartyCharacter partyCharacter = new MaplePartyCharacter(player);
         party = player.getWorldServer().createParty(partyCharacter);
         player.setParty(party);
         player.setMPC(partyCharacter);
         player.getMap().addPartyMember(player);
         player.silentPartyUpdate();

         player.updatePartySearchAvailability(false);
         player.partyOperationUpdate(party, null);

         player.announce(MaplePacketCreator.partyCreated(party, partyCharacter.getId()));

         return true;
      } else {
         if (!silentCheck) {
            player.announce(MaplePacketCreator.partyStatusMessage(16));
         }

         return false;
      }
   }

   public boolean joinParty(MapleCharacter player, int partyid, boolean silentCheck) {
      MapleParty party = player.getParty();
      World world = player.getWorldServer();

      if (party == null) {
         party = world.getParty(partyid);
         if (party != null) {
            if (party.getMembers().size() < 6) {
               MaplePartyCharacter partyCharacter = new MaplePartyCharacter(player);
               player.getMap().addPartyMember(player);

               world.updateParty(party.getId(), PartyOperation.JOIN, partyCharacter);
               player.receivePartyMemberHP();
               player.updatePartyMemberHP();

               player.resetPartySearchInvite(party.getLeaderId());
               player.updatePartySearchAvailability(false);
               player.partyOperationUpdate(party, null);
               return true;
            } else {
               if (!silentCheck) {
                  player.announce(MaplePacketCreator.partyStatusMessage(17));
               }
            }
         } else {
            player.announce(MaplePacketCreator.serverNotice(5, "You couldn't join the party since it had already been disbanded."));
         }
      } else {
         if (!silentCheck) {
            player.announce(MaplePacketCreator.serverNotice(5, "You can't join the party as you are already in one."));
         }
      }

      return false;
   }

   public void leaveParty(MapleParty party, MapleClient c) {
      World world = c.getWorldServer();
      MapleCharacter player = c.getPlayer();
      MaplePartyCharacter partyCharacter = player.getMPC();

      if (party != null && partyCharacter != null) {
         if (partyCharacter.getId() == party.getLeaderId()) {
            c.getWorldServer().removeMapPartyMembers(party.getId());

            MonsterCarnival monsterCarnival = player.getMonsterCarnival();
            if (monsterCarnival != null) {
               monsterCarnival.leftParty(player.getId());
            }

            world.updateParty(party.getId(), PartyOperation.DISBAND, partyCharacter);

            EventInstanceManager eim = player.getEventInstance();
            if (eim != null) {
               eim.disbandParty();
            }
         } else {
            MapleMap map = player.getMap();
            if (map != null) {
               map.removePartyMember(player);
            }

            MonsterCarnival monsterCarnival = player.getMonsterCarnival();
            if (monsterCarnival != null) {
               monsterCarnival.leftParty(player.getId());
            }

            world.updateParty(party.getId(), PartyOperation.LEAVE, partyCharacter);

            EventInstanceManager eim = player.getEventInstance();
            if (eim != null) {
               eim.leftParty(player);
            }
         }

         player.setParty(null);

         MapleMatchCheckerCoordinator matchCheckerCoordinator = c.getWorldServer().getMatchCheckerCoordinator();
         if (matchCheckerCoordinator.getMatchConfirmationLeaderid(player.getId()) == player.getId() && matchCheckerCoordinator.getMatchConfirmationType(player.getId()) == MatchCheckerListenerFactory.MatchCheckerType.GUILD_CREATION) {
            matchCheckerCoordinator.dismissMatchConfirmation(player.getId());
         }
      }
   }

   public void expelFromParty(MapleParty party, MapleClient c, int expelCid) {
      World world = c.getWorldServer();
      MapleCharacter player = c.getPlayer();
      MaplePartyCharacter partyCharacter = player.getMPC();

      if (party != null && partyCharacter != null) {
         if (partyCharacter.equals(party.getLeader())) {
            MaplePartyCharacter expelled = party.getMemberById(expelCid);
            if (expelled != null) {
               MapleCharacter emc = expelled.getPlayer();
               if (emc != null) {
                  List<MapleCharacter> partyMembers = emc.getPartyMembersOnline();

                  MapleMap map = emc.getMap();
                  if (map != null) {
                     map.removePartyMember(emc);
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
                  world.updateParty(party.getId(), PartyOperation.EXPEL, expelled);

                  emc.updatePartySearchAvailability(true);
                  emc.partyOperationUpdate(party, partyMembers);
               } else {
                  world.updateParty(party.getId(), PartyOperation.EXPEL, expelled);
               }
            }
         }
      }
   }
}
