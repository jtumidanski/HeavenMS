package net.server.channel.handlers;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import client.MapleCharacter;
import client.MapleClient;
import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import config.YamlConfig;
import constants.game.GameConstants;
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
import net.server.processor.MapleAllianceProcessor;
import net.server.processor.MapleGuildProcessor;
import net.server.processor.MaplePartyProcessor;
import net.server.world.World;
import tools.I18nMessage;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.alliance.GetGuildAlliances;
import tools.packet.guild.GuildMarkChanged;
import tools.packet.guild.GuildNameChange;
import tools.packet.guild.ShowGuildInfo;
import tools.packet.guild.UpdateGuildPoints;

public final class GuildOperationHandler extends AbstractPacketHandler<BaseGuildOperationPacket> {
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
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.UNHANDLED_EVENT, "Unhandled GUILD_OPERATION packet: \n" + packet.toString());
      }
   }

   private void guildMatch(MapleClient client, MapleCharacter mapleCharacter, GuildMatchPacket packet) {
      World world = client.getWorldServer();

      if (mapleCharacter.getParty().isPresent()) {
         world.getMatchCheckerCoordinator().dismissMatchConfirmation(mapleCharacter.getId());
         return;
      }

      int leaderId = world.getMatchCheckerCoordinator().getMatchConfirmationLeaderId(mapleCharacter.getId());
      if (leaderId != -1) {
         if (packet.result() && world.getMatchCheckerCoordinator().isMatchConfirmationActive(mapleCharacter.getId())) {
            world.getPlayerStorage().getCharacterById(leaderId).ifPresent(leader -> {
               int partyId = leader.getPartyId();
               if (partyId != -1) {
                  MaplePartyProcessor.getInstance().joinParty(mapleCharacter, partyId, true);
               }
            });
         }

         world.getMatchCheckerCoordinator().answerMatchConfirmation(mapleCharacter.getId(), packet.result());
      }
   }

   private void changeGuildNotice(MapleCharacter mapleCharacter, ChangeGuildNoticePacket packet) {
      if (mapleCharacter.getGuildId() <= 0 || mapleCharacter.getGuildRank() > 2) {
         if (mapleCharacter.getGuildId() <= 0) {
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXPLOITS, mapleCharacter.getName() + " tried to change guild notice while not in a guild.");
         }
         return;
      }
      if (packet.notice().length() > 100) {
         return;
      }

      mapleCharacter.getGuild().ifPresent(guild -> MapleGuildProcessor.getInstance().setGuildNotice(guild, packet.notice()));
   }

   private void changeGuildEmblem(MapleClient client, MapleCharacter mapleCharacter, ChangeGuildEmblemPacket packet) {
      if (mapleCharacter.getGuildId() <= 0 || mapleCharacter.getGuildRank() != 1 || mapleCharacter.getMapId() != 200000301) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXPLOITS, mapleCharacter.getName() + " tried to change guild emblem without being the guild leader.");
         return;
      }
      if (mapleCharacter.getMeso() < YamlConfig.config.server.CHANGE_EMBLEM_COST) {
         MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("GUILD_EMBLEM_CHANGE_MINIMUM_MESO_ERROR").with(GameConstants.numberWithCommas(YamlConfig.config.server.CHANGE_EMBLEM_COST)));
         return;
      }

      MapleGuildProcessor.getInstance().setGuildEmblem(mapleCharacter.getGuildId(), packet.background(), packet.backgroundColor(), packet.logo(), packet.logoColor());

      mapleCharacter.getAlliance().ifPresent(alliance -> Server.getInstance().allianceMessage(alliance.id(), new GetGuildAlliances(alliance, client.getWorld()), -1, -1));

      mapleCharacter.gainMeso(-YamlConfig.config.server.CHANGE_EMBLEM_COST, true, false, true);
      mapleCharacter.getGuild().ifPresent(guild -> MapleGuildProcessor.getInstance().broadcastNameChanged(guild));
      mapleCharacter.getGuild().ifPresent(guild -> MapleGuildProcessor.getInstance().broadcastEmblemChanged(guild));
   }

   private void changeRank(MapleCharacter mapleCharacter, ChangeGuildRankPacket packet) {
      if (mapleCharacter.getGuildRank() > 2 || (packet.rank() <= 2 && mapleCharacter.getGuildRank() != 1) || mapleCharacter.getGuildId() <= 0) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXPLOITS, mapleCharacter.getName() + " is trying to change rank outside of his/her permissions.");
         return;
      }
      if (packet.rank() <= 1 || packet.rank() > 5) {
         return;
      }
      MapleGuildProcessor.getInstance().changeRank(mapleCharacter.getGuildId(), packet.playerId(), packet.rank());
   }

   private void changeRankAndTitle(MapleCharacter mapleCharacter, ChangeGuildRankAndTitlePacket packet) {
      if (mapleCharacter.getGuildId() <= 0 || mapleCharacter.getGuildRank() != 1) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXPLOITS, mapleCharacter.getName() + " tried to change guild rank titles when s/he does not have permission.");
         return;
      }

      MapleGuildProcessor.getInstance().changeRankTitle(mapleCharacter.getGuildId(), packet.ranks());
   }

   private void expelMember(MapleCharacter mapleCharacter, ExpelFromGuildPacket packet) {
      if (mapleCharacter.getGuildRank() > 2 || mapleCharacter.getGuildId() <= 0) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXPLOITS, mapleCharacter.getName() + " is trying to expel without rank 1 or 2.");
         return;
      }

      MapleGuildProcessor.getInstance().expelMember(mapleCharacter.getMGC(), packet.name(), packet.playerId());

      int allianceId = mapleCharacter.getGuild().map(MapleGuild::getAllianceId).orElse(0);
      if (allianceId > 0) {
         Server.getInstance().getAlliance(allianceId).ifPresent(alliance -> MapleAllianceProcessor.getInstance().updateAlliancePackets(alliance, mapleCharacter));
      }
   }

   private void leaveGuild(MapleClient client, MapleCharacter mapleCharacter, LeaveGuildPacket packet) {
      if (packet.playerId() != mapleCharacter.getId() || !packet.name().equals(mapleCharacter.getName()) || mapleCharacter.getGuildId() <= 0) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXPLOITS, mapleCharacter.getName() + " tried to quit guild under the name \"" + packet.name() + "\" and current guild id of " + mapleCharacter.getGuildId() + ".");
         return;
      }

      PacketCreator.announce(client, new UpdateGuildPoints(mapleCharacter.getGuildId(), 0));
      MapleGuildProcessor.getInstance().leaveGuild(mapleCharacter);
      PacketCreator.announce(client, new ShowGuildInfo(null));

      int allianceId = mapleCharacter.getGuild().map(MapleGuild::getAllianceId).orElse(0);
      if (allianceId > 0) {
         Server.getInstance().getAlliance(allianceId).ifPresent(alliance -> MapleAllianceProcessor.getInstance().updateAlliancePackets(alliance, mapleCharacter));
      }

      mapleCharacter.getMGC().setGuildId(0);
      mapleCharacter.getMGC().setGuildRank(5);
      mapleCharacter.saveGuildStatus();
      mapleCharacter.getMap().broadcastMessage(mapleCharacter, new GuildNameChange(mapleCharacter.getId(), ""));
   }

   private void joinGuild(MapleClient client, MapleCharacter mapleCharacter, JoinGuildPacket packet) {
      if (mapleCharacter.getGuildId() > 0) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXPLOITS, mapleCharacter.getName() + " attempted to join a guild when s/he is already in one.");
         return;
      }

      if (packet.playerId() != mapleCharacter.getId()) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXPLOITS, mapleCharacter.getName() + " attempted to join a guild with a different character id.");
         return;
      }

      if (!MapleGuildProcessor.getInstance().answerInvitation(packet.playerId(), mapleCharacter.getName(), packet.guildId(), true)) {
         return;
      }

      mapleCharacter.getMGC().setGuildId(packet.guildId()); // joins the guild
      mapleCharacter.getMGC().setGuildRank(5); // start at lowest rank
      mapleCharacter.getMGC().setAllianceRank(5);

      boolean success = MapleGuildProcessor.getInstance().addGuildMember(mapleCharacter.getMGC(), mapleCharacter);
      if (!success) {
         MessageBroadcaster.getInstance().sendServerNotice(mapleCharacter, ServerNoticeType.POP_UP, I18nMessage.from("GUILD_JOIN_ATTEMPT_FULL"));
         mapleCharacter.getMGC().setGuildId(0);
         return;
      }

      PacketCreator.announce(client, new ShowGuildInfo(mapleCharacter));

      int allianceId = mapleCharacter.getGuild().map(MapleGuild::getAllianceId).orElse(0);
      if (allianceId > 0) {
         Server.getInstance().getAlliance(allianceId).ifPresent(alliance -> MapleAllianceProcessor.getInstance().updateAlliancePackets(alliance, mapleCharacter));
      }

      mapleCharacter.saveGuildStatus(); // update database
      mapleCharacter.getGuild().ifPresent(guild -> {
         mapleCharacter.getMap().broadcastMessage(mapleCharacter, new GuildNameChange(mapleCharacter.getId(), guild.getName()));
         mapleCharacter.getMap().broadcastMessage(mapleCharacter, new GuildMarkChanged(mapleCharacter.getId(), guild.getLogoBG(), guild.getLogoBGColor(), guild.getLogo(), guild.getLogoColor()));
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
         MessageBroadcaster.getInstance().sendServerNotice(mapleCharacter, ServerNoticeType.POP_UP, I18nMessage.from("GUILD_CREATION_ALREADY_IN_A_GUILD"));
         return;
      }
      if (mapleCharacter.getMeso() < YamlConfig.config.server.CREATE_GUILD_COST) {
         MessageBroadcaster.getInstance().sendServerNotice(mapleCharacter, ServerNoticeType.POP_UP, I18nMessage.from("GUILD_CREATION_MINIMUM_MESO_ERROR").with(GameConstants.numberWithCommas(YamlConfig.config.server.CREATE_GUILD_COST)));
         return;
      }
      if (!isGuildNameAcceptable(packet.name())) {
         MessageBroadcaster.getInstance().sendServerNotice(mapleCharacter, ServerNoticeType.POP_UP, I18nMessage.from("GUILD_CREATION_NAME_ERROR"));
         return;
      }

      Set<MapleCharacter> eligibleMembers = new HashSet<>(MapleGuildProcessor.getInstance().getEligiblePlayersForGuild(mapleCharacter));
      if (eligibleMembers.size() < YamlConfig.config.server.CREATE_GUILD_MIN_PARTNERS) {
         if (mapleCharacter.getMap().getAllPlayers().size() < YamlConfig.config.server.CREATE_GUILD_MIN_PARTNERS) {
            MessageBroadcaster.getInstance().sendServerNotice(mapleCharacter, ServerNoticeType.POP_UP, I18nMessage.from("GUILD_CREATION_MINIMUM_CO_FOUNDERS_ERROR"));
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(mapleCharacter, ServerNoticeType.POP_UP, I18nMessage.from("GUILD_CREATION_INVITEE_IN_GUILD_ERROR"));
         }

         return;
      }

      if (!MaplePartyProcessor.getInstance().createParty(mapleCharacter, true)) {
         MessageBroadcaster.getInstance().sendServerNotice(mapleCharacter, ServerNoticeType.POP_UP, I18nMessage.from("GUILD_CREATION_IN_PARTY_ERROR"));
         return;
      }

      Set<Integer> eligibleCharacterIds = eligibleMembers.stream().map(MapleCharacter::getId).collect(Collectors.toSet());
      client.getWorldServer().getMatchCheckerCoordinator().createMatchConfirmation(MatchCheckerType.GUILD_CREATION, client.getWorld(), mapleCharacter.getId(), eligibleCharacterIds, packet.name());
   }
}
