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
               MessageBroadcaster.getInstance().sendServerNotice(leader, ServerNoticeType.POP_UP, "You cannot create a new Guild while in one.");
               broadcastGuildCreationDismiss(matchPlayers);
               return;
            }
            int partyId = leader.getPartyId();
            if (partyId == -1 || !leader.isPartyLeader()) {
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
            if (leader.getMeso() < YamlConfig.config.server.CREATE_GUILD_COST) {
               MessageBroadcaster.getInstance().sendServerNotice(leader, ServerNoticeType.POP_UP, "You do not have " + GameConstants.numberWithCommas(YamlConfig.config.server.CREATE_GUILD_COST) + " mesos to create a Guild.");
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
            MessageBroadcaster.getInstance().sendServerNotice(leader, ServerNoticeType.POP_UP, "You have successfully created a Guild.");

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

            String msg;
            if (leader != null && leader.getParty().isEmpty()) {
               msg = "The Guild creation has been dismissed since the leader left the founding party.";
            } else {
               msg = "The Guild creation has been dismissed since a member was already in a party when they answered.";
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
