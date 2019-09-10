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

import java.util.HashSet;
import java.util.Set;

import client.MapleCharacter;
import client.MapleClient;
import constants.GameConstants;
import constants.ServerConstants;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.guild.BaseGuildOperationPacket;
import net.server.channel.packet.guild.ChangeGuildEmblemPacket;
import net.server.channel.packet.guild.ChangeGuildNoticePacket;
import net.server.channel.packet.guild.ChangeGuildRankAndTitlePacket;
import net.server.channel.packet.guild.ChangeGuildRankPacket;
import net.server.channel.packet.guild.CreateGuildPacket;
import net.server.channel.packet.guild.ExpelFromGuildPacket;
import net.server.channel.packet.guild.GuildMatchPacket;
import net.server.channel.packet.guild.InviteToGuildPacket;
import net.server.channel.packet.guild.JoinGuildPacket;
import net.server.channel.packet.guild.LeaveGuildPacket;
import net.server.channel.packet.guild.ShowGuildInformationPacket;
import net.server.channel.packet.reader.GuildOperationReader;
import net.server.coordinator.matchchecker.MatchCheckerListenerFactory.MatchCheckerType;
import net.server.guild.MapleGuild;
import net.server.guild.MapleGuildResponse;
import net.server.processor.MapleGuildProcessor;
import net.server.processor.MaplePartyProcessor;
import net.server.world.World;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public final class GuildOperationHandler extends AbstractPacketHandler<BaseGuildOperationPacket, GuildOperationReader> {
   @Override
   public Class<GuildOperationReader> getReaderClass() {
      return GuildOperationReader.class;
   }

   private boolean isGuildNameAcceptable(String name) {
      if (name.length() < 3 || name.length() > 12) {
         return false;
      }
      for (int i = 0; i < name.length(); i++) {
         if (!Character.isLowerCase(name.charAt(i)) && !Character.isUpperCase(name.charAt(i))) {
            return false;
         }
      }
      return true;
   }

   @Override
   public void handlePacket(BaseGuildOperationPacket packet, MapleClient client) {
      MapleCharacter mapleCharacter = client.getPlayer();
      if (packet instanceof ShowGuildInformationPacket) {
         //c.announce(MaplePacketCreator.showGuildInfo(mc));
      } else if (packet instanceof CreateGuildPacket) {
         createGuild(client, mapleCharacter, (CreateGuildPacket) packet);
      } else if (packet instanceof InviteToGuildPacket) {
         inviteToGuild(client, mapleCharacter, (InviteToGuildPacket) packet);
      } else if (packet instanceof JoinGuildPacket) {
         joinGuild(client, mapleCharacter, (JoinGuildPacket) packet);
      } else if (packet instanceof LeaveGuildPacket) {
         leaveGuild(client, mapleCharacter, (LeaveGuildPacket) packet);
      } else if (packet instanceof ExpelFromGuildPacket) {
         expelMember(mapleCharacter, (ExpelFromGuildPacket) packet);
      } else if (packet instanceof ChangeGuildRankAndTitlePacket) {
         changeRankAndTitle(mapleCharacter, (ChangeGuildRankAndTitlePacket) packet);
      } else if (packet instanceof ChangeGuildRankPacket) {
         changeRank(mapleCharacter, (ChangeGuildRankPacket) packet);
      } else if (packet instanceof ChangeGuildEmblemPacket) {
         changeGuildEmblem(client, mapleCharacter, (ChangeGuildEmblemPacket) packet);
      } else if (packet instanceof ChangeGuildNoticePacket) {
         changeGuildNotice(mapleCharacter, (ChangeGuildNoticePacket) packet);
      } else if (packet instanceof GuildMatchPacket) {
         guildMatch(client, mapleCharacter, (GuildMatchPacket) packet);
      } else {
         System.out.println("Unhandled GUILD_OPERATION packet: \n" + packet.toString());
      }
   }

   private void guildMatch(MapleClient client, MapleCharacter mapleCharacter, GuildMatchPacket packet) {
      World world = client.getWorldServer();

      if (mapleCharacter.getParty() != null) {
         world.getMatchCheckerCoordinator().dismissMatchConfirmation(mapleCharacter.getId());
         return;
      }

      int leaderId = world.getMatchCheckerCoordinator().getMatchConfirmationLeaderid(mapleCharacter.getId());
      if (leaderId != -1) {
         if (packet.result() && world.getMatchCheckerCoordinator().isMatchConfirmationActive(mapleCharacter.getId())) {
            world.getPlayerStorage().getCharacterById(leaderId).ifPresent(leader -> {
               int partyId = leader.getPartyId();
               if (partyId != -1) {
                  MaplePartyProcessor.getInstance().joinParty(mapleCharacter, partyId, true);    // GMS gimmick "party to form guild" recalled thanks to Vcoc
               }
            });
         }

         world.getMatchCheckerCoordinator().answerMatchConfirmation(mapleCharacter.getId(), packet.result());
      }
   }

   private void changeGuildNotice(MapleCharacter mapleCharacter, ChangeGuildNoticePacket packet) {
      if (mapleCharacter.getGuildId() <= 0 || mapleCharacter.getGuildRank() > 2) {
         if (mapleCharacter.getGuildId() <= 0) {
            System.out.println("[Hack] " + mapleCharacter.getName() + " tried to change guild notice while not in a guild.");
         }
         return;
      }
      if (packet.notice().length() > 100) {
         return;
      }
      Server.getInstance().setGuildNotice(mapleCharacter.getGuildId(), packet.notice());
   }

   private void changeGuildEmblem(MapleClient client, MapleCharacter mapleCharacter, ChangeGuildEmblemPacket packet) {
      if (mapleCharacter.getGuildId() <= 0 || mapleCharacter.getGuildRank() != 1 || mapleCharacter.getMapId() != 200000301) {
         System.out.println("[Hack] " + mapleCharacter.getName() + " tried to change guild emblem without being the guild leader.");
         return;
      }
      if (mapleCharacter.getMeso() < ServerConstants.CHANGE_EMBLEM_COST) {
         MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, "You do not have " + GameConstants.numberWithCommas(ServerConstants.CHANGE_EMBLEM_COST) + " mesos to change the Guild emblem.");
         return;
      }

      Server.getInstance().setGuildEmblem(mapleCharacter.getGuildId(), packet.background(), packet.backgroundColor(), packet.logo(), packet.logoColor());

      mapleCharacter.getAlliance().ifPresent(alliance -> {
         byte[] packetData = MaplePacketCreator.getGuildAlliances(alliance, client.getWorld());
         Server.getInstance().allianceMessage(alliance.getId(), packetData, -1, -1);
      });

      mapleCharacter.gainMeso(-ServerConstants.CHANGE_EMBLEM_COST, true, false, true);
      mapleCharacter.getGuild().ifPresent(MapleGuild::broadcastNameChanged);
      mapleCharacter.getGuild().ifPresent(MapleGuild::broadcastEmblemChanged);
   }

   private void changeRank(MapleCharacter mapleCharacter, ChangeGuildRankPacket packet) {
      if (mapleCharacter.getGuildRank() > 2 || (packet.rank() <= 2 && mapleCharacter.getGuildRank() != 1) || mapleCharacter.getGuildId() <= 0) {
         System.out.println("[Hack] " + mapleCharacter.getName() + " is trying to change rank outside of his/her permissions.");
         return;
      }
      if (packet.rank() <= 1 || packet.rank() > 5) {
         return;
      }
      Server.getInstance().changeRank(mapleCharacter.getGuildId(), packet.playerId(), packet.rank());
   }

   private void changeRankAndTitle(MapleCharacter mapleCharacter, ChangeGuildRankAndTitlePacket packet) {
      if (mapleCharacter.getGuildId() <= 0 || mapleCharacter.getGuildRank() != 1) {
         System.out.println("[Hack] " + mapleCharacter.getName() + " tried to change guild rank titles when s/he does not have permission.");
         return;
      }

      Server.getInstance().changeRankTitle(mapleCharacter.getGuildId(), packet.ranks());
   }

   private void expelMember(MapleCharacter mapleCharacter, ExpelFromGuildPacket packet) {
      if (mapleCharacter.getGuildRank() > 2 || mapleCharacter.getGuildId() <= 0) {
         System.out.println("[Hack] " + mapleCharacter.getName() + " is trying to expel without rank 1 or 2.");
         return;
      }

      Server.getInstance().expelMember(mapleCharacter.getMGC(), packet.name(), packet.playerId());

      int allianceId = mapleCharacter.getGuild().map(MapleGuild::getAllianceId).orElse(0);
      if (allianceId > 0) {
         Server.getInstance().getAlliance(allianceId).ifPresent(alliance -> alliance.updateAlliancePackets(mapleCharacter));
      }
   }

   private void leaveGuild(MapleClient client, MapleCharacter mapleCharacter, LeaveGuildPacket packet) {
      if (packet.playerId() != mapleCharacter.getId() || !packet.name().equals(mapleCharacter.getName()) || mapleCharacter.getGuildId() <= 0) {
         System.out.println("[Hack] " + mapleCharacter.getName() + " tried to quit guild under the name \"" + packet.name() + "\" and current guild id of " + mapleCharacter.getGuildId() + ".");
         return;
      }

      client.announce(MaplePacketCreator.updateGP(mapleCharacter.getGuildId(), 0));
      Server.getInstance().leaveGuild(mapleCharacter.getMGC());

      client.announce(MaplePacketCreator.showGuildInfo(null));

      int allianceId = mapleCharacter.getGuild().map(MapleGuild::getAllianceId).orElse(0);
      if (allianceId > 0) {
         Server.getInstance().getAlliance(allianceId).ifPresent(alliance -> alliance.updateAlliancePackets(mapleCharacter));
      }

      mapleCharacter.getMGC().setGuildId(0);
      mapleCharacter.getMGC().setGuildRank(5);
      mapleCharacter.saveGuildStatus();
      mapleCharacter.getMap().broadcastMessage(mapleCharacter, MaplePacketCreator.guildNameChanged(mapleCharacter.getId(), ""));
   }

   private void joinGuild(MapleClient client, MapleCharacter mapleCharacter, JoinGuildPacket packet) {
      if (mapleCharacter.getGuildId() > 0) {
         System.out.println("[Hack] " + mapleCharacter.getName() + " attempted to join a guild when s/he is already in one.");
         return;
      }

      if (packet.playerId() != mapleCharacter.getId()) {
         System.out.println("[Hack] " + mapleCharacter.getName() + " attempted to join a guild with a different character id.");
         return;
      }

      if (!MapleGuildProcessor.getInstance().answerInvitation(packet.playerId(), mapleCharacter.getName(), packet.guildId(), true)) {
         return;
      }

      mapleCharacter.getMGC().setGuildId(packet.guildId()); // joins the guild
      mapleCharacter.getMGC().setGuildRank(5); // start at lowest rank
      mapleCharacter.getMGC().setAllianceRank(5);

      int s = Server.getInstance().addGuildMember(mapleCharacter.getMGC(), mapleCharacter);
      if (s == 0) {
         MessageBroadcaster.getInstance().sendServerNotice(mapleCharacter, ServerNoticeType.POP_UP, "The guild you are trying to join is already full.");
         mapleCharacter.getMGC().setGuildId(0);
         return;
      }

      client.announce(MaplePacketCreator.showGuildInfo(mapleCharacter));

      int allianceId = mapleCharacter.getGuild().map(MapleGuild::getAllianceId).orElse(0);
      if (allianceId > 0) {
         Server.getInstance().getAlliance(allianceId).ifPresent(alliance -> alliance.updateAlliancePackets(mapleCharacter));
      }

      mapleCharacter.saveGuildStatus(); // update database
      mapleCharacter.getGuild().ifPresent(guild -> {
         mapleCharacter.getMap().broadcastMessage(mapleCharacter, MaplePacketCreator.guildNameChanged(mapleCharacter.getId(), guild.getName())); // thanks Vcoc for pointing out an issue with updating guild tooltip to players in the map
         mapleCharacter.getMap().broadcastMessage(mapleCharacter, MaplePacketCreator.guildMarkChanged(mapleCharacter.getId(), guild));
      });

   }

   private void inviteToGuild(MapleClient client, MapleCharacter mapleCharacter, InviteToGuildPacket packet) {
      if (mapleCharacter.getGuildId() <= 0 || mapleCharacter.getGuildRank() > 2) {
         return;
      }

      MapleGuildResponse mgr = MapleGuildProcessor.getInstance().sendInvitation(client, packet.name());
      if (mgr != null) {
         client.announce(mgr.getPacket(packet.name()));
      }
   }

   private void createGuild(MapleClient client, MapleCharacter mapleCharacter, CreateGuildPacket packet) {
      if (mapleCharacter.getGuildId() > 0) {
         MessageBroadcaster.getInstance().sendServerNotice(mapleCharacter, ServerNoticeType.POP_UP, "You cannot create a new Guild while in one.");
         return;
      }
      if (mapleCharacter.getMeso() < ServerConstants.CREATE_GUILD_COST) {
         MessageBroadcaster.getInstance().sendServerNotice(mapleCharacter, ServerNoticeType.POP_UP, "You do not have " + GameConstants.numberWithCommas(ServerConstants.CREATE_GUILD_COST) + " mesos to create a Guild.");
         return;
      }
      if (!isGuildNameAcceptable(packet.name())) {
         MessageBroadcaster.getInstance().sendServerNotice(mapleCharacter, ServerNoticeType.POP_UP, "The Guild name you have chosen is not accepted.");
         return;
      }

      Set<MapleCharacter> eligibleMembers = new HashSet<>(MapleGuildProcessor.getInstance().getEligiblePlayersForGuild(mapleCharacter));
      if (eligibleMembers.size() < ServerConstants.CREATE_GUILD_MIN_PARTNERS) {
         if (mapleCharacter.getMap().getAllPlayers().size() < ServerConstants.CREATE_GUILD_MIN_PARTNERS) {
            // thanks NovaStory for noticing message in need of smoother info
            MessageBroadcaster.getInstance().sendServerNotice(mapleCharacter, ServerNoticeType.POP_UP, "Your Guild doesn't have enough cofounders present here and therefore cannot be created at this time.");
         } else {
            // players may be unaware of not belonging on a party in order to become eligible, thanks Hair (Legalize) for pointing this out
            MessageBroadcaster.getInstance().sendServerNotice(mapleCharacter, ServerNoticeType.POP_UP, "Please make sure everyone you are trying to invite is neither on a guild nor on a party.");
         }

         return;
      }

      if (!MaplePartyProcessor.getInstance().createParty(mapleCharacter, true)) {
         MessageBroadcaster.getInstance().sendServerNotice(mapleCharacter, ServerNoticeType.POP_UP, "You cannot create a new Guild while in a party.");
         return;
      }

      Set<Integer> eligibleCids = new HashSet<>();
      for (MapleCharacter chr : eligibleMembers) {
         eligibleCids.add(chr.getId());
      }

      client.getWorldServer().getMatchCheckerCoordinator().createMatchConfirmation(MatchCheckerType.GUILD_CREATION, client.getWorld(), mapleCharacter.getId(), eligibleCids, packet.name());
   }
}
