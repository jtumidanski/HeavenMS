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
import net.AbstractMaplePacketHandler;
import net.server.Server;
import net.server.coordinator.matchchecker.MatchCheckerListenerFactory.MatchCheckerType;
import net.server.guild.MapleGuild;
import net.server.guild.MapleGuildResponse;
import net.server.processor.MapleGuildProcessor;
import net.server.processor.MaplePartyProcessor;
import net.server.world.World;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.data.input.SeekableLittleEndianAccessor;

public final class GuildOperationHandler extends AbstractMaplePacketHandler {
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
   public final void handlePacket(SeekableLittleEndianAccessor accessor, MapleClient c) {
      MapleCharacter mc = c.getPlayer();
      byte type = accessor.readByte();
      switch (type) {
         case 0x00:
            //c.announce(MaplePacketCreator.showGuildInfo(mc));
            break;
         case 0x02:
            createGuild(new GuildCreationData(accessor), c, mc);
            break;
         case 0x05:
            inviteToGuild(new GuildInviteData(accessor), c, mc);
            break;
         case 0x06:
            joinGuild(new GuildJoinData(accessor), c, mc);
            break;
         case 0x07:
            leaveGuild(new GuildLeaveData(accessor), c, mc);
            break;
         case 0x08:
            expelMember(new GuildExpelData(accessor), mc);
            break;
         case 0x0d:
            changeRankAndTitle(new GuildRankTitleData(accessor), mc);
            break;
         case 0x0e:
            changeRank(new GuildRankData(accessor), mc);
            break;
         case 0x0f:
            changeGuildEmblem(new GuildEmblemData(accessor), c, mc);
            break;
         case 0x10:
            changeGuildNotice(new GuildNoticeData(accessor), mc);
            break;
         case 0x1E:
            guildMatch(new GuildMatchData(accessor), c, mc);
            break;
         default:
            System.out.println("Unhandled GUILD_OPERATION packet: \n" + accessor.toString());
      }
   }

   private void guildMatch(GuildMatchData matchData, MapleClient c, MapleCharacter mc) {
      World world = c.getWorldServer();

      if (mc.getParty() != null) {
         world.getMatchCheckerCoordinator().dismissMatchConfirmation(mc.getId());
         return;
      }

      int leaderId = world.getMatchCheckerCoordinator().getMatchConfirmationLeaderid(mc.getId());
      if (leaderId != -1) {
         if (matchData.isResult() && world.getMatchCheckerCoordinator().isMatchConfirmationActive(mc.getId())) {
            world.getPlayerStorage().getCharacterById(leaderId).ifPresent(leader -> {
               int partyId = leader.getPartyId();
               if (partyId != -1) {
                  MaplePartyProcessor.getInstance().joinParty(mc, partyId, true);    // GMS gimmick "party to form guild" recalled thanks to Vcoc
               }
            });
         }

         world.getMatchCheckerCoordinator().answerMatchConfirmation(mc.getId(), matchData.isResult());
      }
   }

   private void changeGuildNotice(GuildNoticeData noticeData, MapleCharacter mc) {
      if (mc.getGuildId() <= 0 || mc.getGuildRank() > 2) {
         if (mc.getGuildId() <= 0) {
            System.out.println("[Hack] " + mc.getName() + " tried to change guild notice while not in a guild.");
         }
         return;
      }
      if (noticeData.getNotice().length() > 100) {
         return;
      }
      Server.getInstance().setGuildNotice(mc.getGuildId(), noticeData.getNotice());
   }

   private void changeGuildEmblem(GuildEmblemData emblemData, MapleClient c, MapleCharacter mc) {
      if (mc.getGuildId() <= 0 || mc.getGuildRank() != 1 || mc.getMapId() != 200000301) {
         System.out.println("[Hack] " + mc.getName() + " tried to change guild emblem without being the guild leader.");
         return;
      }
      if (mc.getMeso() < ServerConstants.CHANGE_EMBLEM_COST) {
         MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "You do not have " + GameConstants.numberWithCommas(ServerConstants.CHANGE_EMBLEM_COST) + " mesos to change the Guild emblem.");
         return;
      }

      Server.getInstance().setGuildEmblem(mc.getGuildId(), emblemData.getBackground(), emblemData.getBackgroundColor(), emblemData.getLogo(), emblemData.getLogoColor());

      mc.getAlliance().ifPresent(alliance -> {
         byte[] packet = MaplePacketCreator.getGuildAlliances(alliance, c.getWorld());
         Server.getInstance().allianceMessage(alliance.getId(), packet, -1, -1);
      });

      mc.gainMeso(-ServerConstants.CHANGE_EMBLEM_COST, true, false, true);
      mc.getGuild().ifPresent(MapleGuild::broadcastNameChanged);
      mc.getGuild().ifPresent(MapleGuild::broadcastEmblemChanged);
   }

   private void changeRank(GuildRankData rankData, MapleCharacter mc) {
      if (mc.getGuildRank() > 2 || (rankData.getNewRank() <= 2 && mc.getGuildRank() != 1) || mc.getGuildId() <= 0) {
         System.out.println("[Hack] " + mc.getName() + " is trying to change rank outside of his/her permissions.");
         return;
      }
      if (rankData.getNewRank() <= 1 || rankData.getNewRank() > 5) {
         return;
      }
      Server.getInstance().changeRank(mc.getGuildId(), rankData.getPlayerId(), rankData.getNewRank());
   }

   private void changeRankAndTitle(GuildRankTitleData rankTitleData, MapleCharacter mc) {
      if (mc.getGuildId() <= 0 || mc.getGuildRank() != 1) {
         System.out.println("[Hack] " + mc.getName() + " tried to change guild rank titles when s/he does not have permission.");
         return;
      }

      Server.getInstance().changeRankTitle(mc.getGuildId(), rankTitleData.getRanks());
   }

   private void expelMember(GuildExpelData expelData, MapleCharacter mc) {
      if (mc.getGuildRank() > 2 || mc.getGuildId() <= 0) {
         System.out.println("[Hack] " + mc.getName() + " is trying to expel without rank 1 or 2.");
         return;
      }

      Server.getInstance().expelMember(mc.getMGC(), expelData.getPlayerName(), expelData.getPlayerId());

      int allianceId = mc.getGuild().map(MapleGuild::getAllianceId).orElse(0);
      if (allianceId > 0) {
         Server.getInstance().getAlliance(allianceId).ifPresent(alliance -> alliance.updateAlliancePackets(mc));
      }
   }

   private void leaveGuild(GuildLeaveData leaveData, MapleClient c, MapleCharacter mc) {
      if (leaveData.getPlayerId() != mc.getId() || !leaveData.getPlayerName().equals(mc.getName()) || mc.getGuildId() <= 0) {
         System.out.println("[Hack] " + mc.getName() + " tried to quit guild under the name \"" + leaveData.getPlayerName() + "\" and current guild id of " + mc.getGuildId() + ".");
         return;
      }

      c.announce(MaplePacketCreator.updateGP(mc.getGuildId(), 0));
      Server.getInstance().leaveGuild(mc.getMGC());

      c.announce(MaplePacketCreator.showGuildInfo(null));

      int allianceId = mc.getGuild().map(MapleGuild::getAllianceId).orElse(0);
      if (allianceId > 0) {
         Server.getInstance().getAlliance(allianceId).ifPresent(alliance -> alliance.updateAlliancePackets(mc));
      }

      mc.getMGC().setGuildId(0);
      mc.getMGC().setGuildRank(5);
      mc.saveGuildStatus();
      mc.getMap().broadcastMessage(mc, MaplePacketCreator.guildNameChanged(mc.getId(), ""));
   }

   private void joinGuild(GuildJoinData joinData, MapleClient c, MapleCharacter mc) {
      if (mc.getGuildId() > 0) {
         System.out.println("[Hack] " + mc.getName() + " attempted to join a guild when s/he is already in one.");
         return;
      }

      if (joinData.getPlayerId() != mc.getId()) {
         System.out.println("[Hack] " + mc.getName() + " attempted to join a guild with a different character id.");
         return;
      }

      if (!MapleGuildProcessor.getInstance().answerInvitation(joinData.getPlayerId(), mc.getName(), joinData.getGuildId(), true)) {
         return;
      }

      mc.getMGC().setGuildId(joinData.getGuildId()); // joins the guild
      mc.getMGC().setGuildRank(5); // start at lowest rank
      mc.getMGC().setAllianceRank(5);

      int s = Server.getInstance().addGuildMember(mc.getMGC(), mc);
      if (s == 0) {
         MessageBroadcaster.getInstance().sendServerNotice(mc, ServerNoticeType.POP_UP, "The guild you are trying to join is already full.");
         mc.getMGC().setGuildId(0);
         return;
      }

      c.announce(MaplePacketCreator.showGuildInfo(mc));

      int allianceId = mc.getGuild().map(MapleGuild::getAllianceId).orElse(0);
      if (allianceId > 0) {
         Server.getInstance().getAlliance(allianceId).ifPresent(alliance -> alliance.updateAlliancePackets(mc));
      }

      mc.saveGuildStatus(); // update database
      mc.getGuild().ifPresent(guild -> {
         mc.getMap().broadcastMessage(mc, MaplePacketCreator.guildNameChanged(mc.getId(), guild.getName())); // thanks Vcoc for pointing out an issue with updating guild tooltip to players in the map
         mc.getMap().broadcastMessage(mc, MaplePacketCreator.guildMarkChanged(mc.getId(), guild));
      });

   }

   private void inviteToGuild(GuildInviteData inviteData, MapleClient c, MapleCharacter mc) {
      if (mc.getGuildId() <= 0 || mc.getGuildRank() > 2) {
         return;
      }

      MapleGuildResponse mgr = MapleGuildProcessor.getInstance().sendInvitation(c, inviteData.getPlayerName());
      if (mgr != null) {
         c.announce(mgr.getPacket(inviteData.getPlayerName()));
      }
   }

   private void createGuild(GuildCreationData creationData, MapleClient c, MapleCharacter mc) {
      if (mc.getGuildId() > 0) {
         MessageBroadcaster.getInstance().sendServerNotice(mc, ServerNoticeType.POP_UP, "You cannot create a new Guild while in one.");
         return;
      }
      if (mc.getMeso() < ServerConstants.CREATE_GUILD_COST) {
         MessageBroadcaster.getInstance().sendServerNotice(mc, ServerNoticeType.POP_UP, "You do not have " + GameConstants.numberWithCommas(ServerConstants.CREATE_GUILD_COST) + " mesos to create a Guild.");
         return;
      }
      if (!isGuildNameAcceptable(creationData.getGuildName())) {
         MessageBroadcaster.getInstance().sendServerNotice(mc, ServerNoticeType.POP_UP, "The Guild name you have chosen is not accepted.");
         return;
      }

      Set<MapleCharacter> eligibleMembers = new HashSet<>(MapleGuildProcessor.getInstance().getEligiblePlayersForGuild(mc));
      if (eligibleMembers.size() < ServerConstants.CREATE_GUILD_MIN_PARTNERS) {
         if (mc.getMap().getAllPlayers().size() < ServerConstants.CREATE_GUILD_MIN_PARTNERS) {
            // thanks NovaStory for noticing message in need of smoother info
            MessageBroadcaster.getInstance().sendServerNotice(mc, ServerNoticeType.POP_UP, "Your Guild doesn't have enough cofounders present here and therefore cannot be created at this time.");
         } else {
            // players may be unaware of not belonging on a party in order to become eligible, thanks Hair (Legalize) for pointing this out
            MessageBroadcaster.getInstance().sendServerNotice(mc, ServerNoticeType.POP_UP, "Please make sure everyone you are trying to invite is neither on a guild nor on a party.");
         }

         return;
      }

      if (!MaplePartyProcessor.getInstance().createParty(mc, true)) {
         MessageBroadcaster.getInstance().sendServerNotice(mc, ServerNoticeType.POP_UP, "You cannot create a new Guild while in a party.");
         return;
      }

      Set<Integer> eligibleCids = new HashSet<>();
      for (MapleCharacter chr : eligibleMembers) {
         eligibleCids.add(chr.getId());
      }

      c.getWorldServer().getMatchCheckerCoordinator().createMatchConfirmation(MatchCheckerType.GUILD_CREATION, c.getWorld(), mc.getId(), eligibleCids, creationData.getGuildName());
   }

   private class GuildMatchData {
      private boolean result;

      public GuildMatchData(boolean result) {
         this.result = result;
      }

      public GuildMatchData(SeekableLittleEndianAccessor accessor) {
         accessor.readInt();
         this.result = accessor.readByte() != 0;
      }

      public boolean isResult() {
         return result;
      }
   }

   private class GuildNoticeData {
      private String notice;

      public GuildNoticeData(String notice) {
         this.notice = notice;
      }

      public GuildNoticeData(SeekableLittleEndianAccessor accessor) {
         this.notice = accessor.readMapleAsciiString();
      }

      public String getNotice() {
         return notice;
      }
   }

   private class GuildEmblemData {
      private short background;

      private byte backgroundColor;

      private short logo;

      private byte logoColor;

      public GuildEmblemData(short background, byte backgroundColor, short logo, byte logoColor) {
         this.background = background;
         this.backgroundColor = backgroundColor;
         this.logo = logo;
         this.logoColor = logoColor;
      }

      public GuildEmblemData(SeekableLittleEndianAccessor accessor) {
         this.background = accessor.readShort();
         this.backgroundColor = accessor.readByte();
         this.logo = accessor.readShort();
         this.logoColor = accessor.readByte();
      }

      public short getBackground() {
         return background;
      }

      public byte getBackgroundColor() {
         return backgroundColor;
      }

      public short getLogo() {
         return logo;
      }

      public byte getLogoColor() {
         return logoColor;
      }
   }

   private class GuildRankData {
      private int playerId;

      private byte newRank;

      public GuildRankData(int playerId, byte newRank) {
         this.playerId = playerId;
         this.newRank = newRank;
      }

      public GuildRankData(SeekableLittleEndianAccessor accessor) {
         this.playerId = accessor.readInt();
         this.newRank = accessor.readByte();
      }

      public int getPlayerId() {
         return playerId;
      }

      public byte getNewRank() {
         return newRank;
      }
   }

   private class GuildRankTitleData {
      private String[] ranks;

      public GuildRankTitleData(String[] ranks) {
         this.ranks = ranks;
      }

      public GuildRankTitleData(SeekableLittleEndianAccessor accessor) {
         ranks = new String[5];
         for (int i = 0; i < 5; i++) {
            ranks[i] = accessor.readMapleAsciiString();
         }
      }

      public String[] getRanks() {
         return ranks;
      }
   }

   private class GuildExpelData {
      private int playerId;

      private String playerName;

      public GuildExpelData(int playerId, String playerName) {
         this.playerId = playerId;
         this.playerName = playerName;
      }

      public GuildExpelData(SeekableLittleEndianAccessor accessor) {
         this.playerId = accessor.readInt();
         this.playerName = accessor.readMapleAsciiString();
      }

      public int getPlayerId() {
         return playerId;
      }

      public String getPlayerName() {
         return playerName;
      }
   }

   private class GuildLeaveData {
      private int playerId;

      private String playerName;

      public GuildLeaveData(int playerId, String playerName) {
         this.playerId = playerId;
         this.playerName = playerName;
      }

      public GuildLeaveData(SeekableLittleEndianAccessor accessor) {
         this.playerId = accessor.readInt();
         this.playerName = accessor.readMapleAsciiString();
      }

      public int getPlayerId() {
         return playerId;
      }

      public String getPlayerName() {
         return playerName;
      }
   }

   private class GuildJoinData {
      private int guildId;

      private int playerId;

      public GuildJoinData(int guildId, int playerId) {
         this.guildId = guildId;
         this.playerId = playerId;
      }

      public GuildJoinData(SeekableLittleEndianAccessor accessor) {
         this.guildId = accessor.readInt();
         this.playerId = accessor.readInt();
      }

      public int getGuildId() {
         return guildId;
      }

      public int getPlayerId() {
         return playerId;
      }
   }

   private class GuildInviteData {
      private String playerName;

      public GuildInviteData(String playerName) {
         this.playerName = playerName;
      }

      public GuildInviteData(SeekableLittleEndianAccessor accessor) {
         this.playerName = accessor.readMapleAsciiString();
      }

      public String getPlayerName() {
         return playerName;
      }
   }

   private class GuildCreationData {
      private String guildName;

      public GuildCreationData(String guildName) {
         this.guildName = guildName;
      }

      public GuildCreationData(SeekableLittleEndianAccessor accessor) {
         this.guildName = accessor.readMapleAsciiString();
      }

      public String getGuildName() {
         return guildName;
      }
   }
}
