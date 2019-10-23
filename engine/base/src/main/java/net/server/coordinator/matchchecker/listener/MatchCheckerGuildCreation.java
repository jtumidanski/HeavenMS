/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2018 RonanLana

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
package net.server.coordinator.matchchecker.listener;

import java.util.Set;

import client.MapleCharacter;
import constants.GameConstants;
import constants.ServerConstants;
import net.server.Server;
import net.server.coordinator.matchchecker.AbstractMatchCheckerListener;
import net.server.coordinator.matchchecker.MatchCheckerListenerRecipe;
import net.server.guild.MapleGuildCharacter;
import net.server.processor.MapleGuildProcessor;
import net.server.processor.MaplePartyProcessor;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.guild.CreateGuildMessage;
import tools.packet.guild.GenericGuildMessage;
import tools.packet.guild.ShowGuildInfo;

/**
 * @author Ronan
 */
public class MatchCheckerGuildCreation implements MatchCheckerListenerRecipe {

   private static void broadcastGuildCreationDismiss(Set<MapleCharacter> nonLeaderMatchPlayers) {
      for (MapleCharacter chr : nonLeaderMatchPlayers) {
         if (chr.isLoggedinWorld()) {
            PacketCreator.announce(chr, new GenericGuildMessage((byte) 0x26));
         }
      }
   }

   public static AbstractMatchCheckerListener loadListener() {
      return (new MatchCheckerGuildCreation()).getListener();
   }

   @Override
   public AbstractMatchCheckerListener getListener() {
      return new AbstractMatchCheckerListener() {
         @Override
         public void onMatchCreated(MapleCharacter leader, Set<MapleCharacter> nonLeaderMatchPlayers, String message) {
            byte[] createGuildPacket = PacketCreator.create(new CreateGuildMessage(leader.getName(), message));
            for (MapleCharacter chr : nonLeaderMatchPlayers) {
               if (chr.isLoggedinWorld()) {
                  chr.announce(createGuildPacket);
               }
            }
         }

         @Override
         public void onMatchAccepted(int leaderid, Set<MapleCharacter> matchPlayers, String message) {
            MapleCharacter leader = null;
            for (MapleCharacter chr : matchPlayers) {
               if (chr.getId() == leaderid) {
                  leader = chr;
                  break;
               }
            }

            if (leader == null || !leader.isLoggedinWorld()) {
               broadcastGuildCreationDismiss(matchPlayers);
               return;
            }
            matchPlayers.remove(leader);

            if (leader.getGuildId() > 0) {
               MessageBroadcaster.getInstance().sendServerNotice(leader, ServerNoticeType.POP_UP, "You cannot create a new Guild while in one.");
               broadcastGuildCreationDismiss(matchPlayers);
               return;
            }
            int partyid = leader.getPartyId();
            if (partyid == -1 || !leader.isPartyLeader()) {
               MessageBroadcaster.getInstance().sendServerNotice(leader, ServerNoticeType.POP_UP, "You cannot establish the creation of a new Guild without leading a party.");
               broadcastGuildCreationDismiss(matchPlayers);
               return;
            }
            if (leader.getMapId() != 200000301) {
               MessageBroadcaster.getInstance().sendServerNotice(leader, ServerNoticeType.POP_UP, "You cannot establish the creation of a new Guild outside of the Guild Headquarters.");
               broadcastGuildCreationDismiss(matchPlayers);
               return;
            }
            for (MapleCharacter chr : matchPlayers) {
               if (leader.getMap().getCharacterById(chr.getId()) == null) {
                  MessageBroadcaster.getInstance().sendServerNotice(leader, ServerNoticeType.POP_UP, "You cannot establish the creation of a new Guild if one of the members is not present here.");
                  broadcastGuildCreationDismiss(matchPlayers);
                  return;
               }
            }
            if (leader.getMeso() < ServerConstants.CREATE_GUILD_COST) {
               MessageBroadcaster.getInstance().sendServerNotice(leader, ServerNoticeType.POP_UP, "You do not have " + GameConstants.numberWithCommas(ServerConstants.CREATE_GUILD_COST) + " mesos to create a Guild.");
               broadcastGuildCreationDismiss(matchPlayers);
               return;
            }

            int gid = Server.getInstance().createGuild(leader.getId(), message);
            if (gid == 0) {
               PacketCreator.announce(leader, new GenericGuildMessage((byte) 0x23));
               broadcastGuildCreationDismiss(matchPlayers);
               return;
            }
            leader.gainMeso(-ServerConstants.CREATE_GUILD_COST, true, false, true);

            leader.getMGC().setGuildId(gid);

            // initialize guild structure
            MapleGuildProcessor.getInstance().changeRank(gid, leader.getId(), 1);

            PacketCreator.announce(leader, new ShowGuildInfo(leader));
            MessageBroadcaster.getInstance().sendServerNotice(leader, ServerNoticeType.POP_UP, "You have successfully created a Guild.");

            for (MapleCharacter chr : matchPlayers) {
               boolean cofounder = chr.getPartyId() == partyid;

               MapleGuildCharacter mgc = chr.getMGC();
               mgc.setGuildId(gid);
               mgc.setGuildRank(cofounder ? 2 : 5);
               mgc.setAllianceRank(5);

               MapleGuildProcessor.getInstance().addGuildMember(mgc, chr);
               if (chr.isLoggedinWorld()) {
                  PacketCreator.announce(chr, new ShowGuildInfo(chr));

                  if (cofounder) {
                     MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You have successfully co-founded a Guild.");
                  } else {
                     MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You have successfully joined the new Guild.");
                  }
               }

               chr.saveGuildStatus(); // update database
            }

            Server.getInstance().getGuild(leader.getGuildId(), leader.getWorld(), leader).ifPresent(guild -> {
               MapleGuildProcessor.getInstance().broadcastNameChanged(guild);
               MapleGuildProcessor.getInstance().broadcastEmblemChanged(guild);
            });
         }

         @Override
         public void onMatchDeclined(int leaderid, Set<MapleCharacter> matchPlayers, String message) {
            for (MapleCharacter chr : matchPlayers) {
               if (chr.getId() == leaderid && chr.getClient() != null) {
                  MaplePartyProcessor.getInstance().leaveParty(chr.getParty(), chr.getClient());
               }

               if (chr.isLoggedinWorld()) {
                  PacketCreator.announce(chr, new GenericGuildMessage((byte) 0x26));
               }
            }
         }

         @Override
         public void onMatchDismissed(int leaderid, Set<MapleCharacter> matchPlayers, String message) {

            MapleCharacter leader = null;
            for (MapleCharacter chr : matchPlayers) {
               if (chr.getId() == leaderid) {
                  leader = chr;
                  break;
               }
            }

            String msg;
            if (leader != null && leader.getParty() == null) {
               msg = "The Guild creation has been dismissed since the leader left the founding party.";
            } else {
               msg = "The Guild creation has been dismissed since a member was already in a party when they answered.";
            }

            for (MapleCharacter chr : matchPlayers) {
               if (chr.getId() == leaderid && chr.getClient() != null) {
                  MaplePartyProcessor.getInstance().leaveParty(chr.getParty(), chr.getClient());
               }

               if (chr.isLoggedinWorld()) {
                  MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, msg);
                  PacketCreator.announce(chr, new GenericGuildMessage((byte) 0x26));
               }
            }
         }
      };
   }
}
