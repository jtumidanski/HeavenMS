package net.server.coordinator.matchchecker.listener;

import java.util.Set;

import client.MapleCharacter;
import config.YamlConfig;
import constants.game.GameConstants;
import net.server.Server;
import net.server.coordinator.matchchecker.AbstractMatchCheckerListener;
import net.server.coordinator.matchchecker.MatchCheckerListenerRecipe;
import net.server.guild.MapleGuildCharacter;
import net.server.processor.MapleGuildProcessor;
import net.server.processor.MaplePartyProcessor;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.guild.CreateGuildMessage;
import tools.packet.guild.GenericGuildMessage;
import tools.packet.guild.ShowGuildInfo;

public class MatchCheckerGuildCreation implements MatchCheckerListenerRecipe {

   private static void broadcastGuildCreationDismiss(Set<MapleCharacter> nonLeaderMatchPlayers) {
      for (MapleCharacter chr : nonLeaderMatchPlayers) {
         if (chr.isLoggedInWorld()) {
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
               if (chr.isLoggedInWorld()) {
                  chr.announce(createGuildPacket);
               }
            }
         }

         @Override
         public void onMatchAccepted(int leaderId, Set<MapleCharacter> matchPlayers, String message) {
            MapleCharacter leader = null;
            for (MapleCharacter chr : matchPlayers) {
               if (chr.getId() == leaderId) {
                  leader = chr;
                  break;
               }
            }

            if (leader == null || !leader.isLoggedInWorld()) {
               broadcastGuildCreationDismiss(matchPlayers);
               return;
            }
            matchPlayers.remove(leader);

            if (leader.getGuildId() > 0) {
               MessageBroadcaster.getInstance().sendServerNotice(leader, ServerNoticeType.POP_UP, I18nMessage.from("GUILD_CREATION_ALREADY_IN_A_GUILD"));
               broadcastGuildCreationDismiss(matchPlayers);
               return;
            }
            int partyId = leader.getPartyId();
            if (partyId == -1 || !leader.isPartyLeader()) {
               MessageBroadcaster.getInstance().sendServerNotice(leader, ServerNoticeType.POP_UP, I18nMessage.from("GUILD_CREATION_PARTY_LEAD_REQUIREMENT"));
               broadcastGuildCreationDismiss(matchPlayers);
               return;
            }
            if (leader.getMapId() != 200000301) {
               MessageBroadcaster.getInstance().sendServerNotice(leader, ServerNoticeType.POP_UP, I18nMessage.from("GUILD_CREATION_MAP_REQUIREMENT"));
               broadcastGuildCreationDismiss(matchPlayers);
               return;
            }
            for (MapleCharacter chr : matchPlayers) {
               if (leader.getMap().getCharacterById(chr.getId()) == null) {
                  MessageBroadcaster.getInstance().sendServerNotice(leader, ServerNoticeType.POP_UP, I18nMessage.from("GUILD_CREATION_MEMBER_NOT_PRESENT"));
                  broadcastGuildCreationDismiss(matchPlayers);
                  return;
               }
            }
            if (leader.getMeso() < YamlConfig.config.server.CREATE_GUILD_COST) {
               MessageBroadcaster.getInstance().sendServerNotice(leader, ServerNoticeType.POP_UP, I18nMessage.from("GUILD_CREATION_MINIMUM_MESO_ERROR").with(GameConstants.numberWithCommas(YamlConfig.config.server.CREATE_GUILD_COST)));
               broadcastGuildCreationDismiss(matchPlayers);
               return;
            }

            int gid = Server.getInstance().createGuild(leader.getId(), message);
            if (gid == 0) {
               PacketCreator.announce(leader, new GenericGuildMessage((byte) 0x23));
               broadcastGuildCreationDismiss(matchPlayers);
               return;
            }
            leader.gainMeso(-YamlConfig.config.server.CREATE_GUILD_COST, true, false, true);

            leader.getMGC().setGuildId(gid);

            // initialize guild structure
            MapleGuildProcessor.getInstance().changeRank(gid, leader.getId(), 1);

            PacketCreator.announce(leader, new ShowGuildInfo(leader));
            MessageBroadcaster.getInstance().sendServerNotice(leader, ServerNoticeType.POP_UP, I18nMessage.from("GUILD_CREATION_SUCCESS"));

            for (MapleCharacter chr : matchPlayers) {
               boolean coFounder = chr.getPartyId() == partyId;

               MapleGuildCharacter mgc = chr.getMGC();
               mgc.setGuildId(gid);
               mgc.setGuildRank(coFounder ? 2 : 5);
               mgc.setAllianceRank(5);

               MapleGuildProcessor.getInstance().addGuildMember(mgc, chr);
               if (chr.isLoggedInWorld()) {
                  PacketCreator.announce(chr, new ShowGuildInfo(chr));

                  if (coFounder) {
                     MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("GUILD_CREATION_CO_FOUNDER_SUCCESS"));
                  } else {
                     MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("GUILD_CREATION_MEMBER_SUCCESS"));
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
         public void onMatchDeclined(int leaderId, Set<MapleCharacter> matchPlayers, String message) {
            for (MapleCharacter chr : matchPlayers) {
               if (chr.getId() == leaderId && chr.getClient() != null) {
                  chr.getParty().ifPresent(party -> MaplePartyProcessor.getInstance().leaveParty(party, chr));
               }

               if (chr.isLoggedInWorld()) {
                  PacketCreator.announce(chr, new GenericGuildMessage((byte) 0x26));
               }
            }
         }

         @Override
         public void onMatchDismissed(int leaderId, Set<MapleCharacter> matchPlayers, String message) {

            MapleCharacter leader = null;
            for (MapleCharacter chr : matchPlayers) {
               if (chr.getId() == leaderId) {
                  leader = chr;
                  break;
               }
            }

            I18nMessage msg;
            if (leader != null && leader.getParty().isEmpty()) {
               msg = I18nMessage.from("GUILD_CREATION_ERROR_LEADER_LEFT");
            } else {
               msg = I18nMessage.from("GUILD_CREATION_ERROR_MEMBER_ALREADY_IN_PARTY");
            }

            for (MapleCharacter chr : matchPlayers) {
               if (chr.getId() == leaderId && chr.getClient() != null) {
                  chr.getParty().ifPresent(party -> MaplePartyProcessor.getInstance().leaveParty(party, chr));
               }

               if (chr.isLoggedInWorld()) {
                  MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, msg);
                  PacketCreator.announce(chr, new GenericGuildMessage((byte) 0x26));
               }
            }
         }
      };
   }
}
