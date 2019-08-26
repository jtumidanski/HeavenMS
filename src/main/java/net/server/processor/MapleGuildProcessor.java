package net.server.processor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import client.MapleCharacter;
import client.MapleClient;
import client.database.administrator.CharacterAdministrator;
import client.database.administrator.GuildAdministrator;
import client.database.provider.GuildProvider;
import constants.ServerConstants;
import net.server.coordinator.MapleInviteCoordinator;
import net.server.coordinator.MapleMatchCheckerCoordinator;
import net.server.guild.MapleGuildResponse;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;

public class MapleGuildProcessor {
   private static MapleGuildProcessor ourInstance = new MapleGuildProcessor();

   public static MapleGuildProcessor getInstance() {
      return ourInstance;
   }

   private MapleGuildProcessor() {
   }

   public int createGuild(int leaderId, String name) {
      Optional<Integer> result = DatabaseConnection.withConnectionResult(connection -> {
         if (GuildProvider.getInstance().getByName(connection, name) != -1) {
            return 0;
         }

         GuildAdministrator.getInstance().createGuild(connection, leaderId, name);
         int guildId = GuildProvider.getInstance().getByLeader(connection, leaderId);
         CharacterAdministrator.getInstance().updateGuild(connection, leaderId, guildId);
         return guildId;
      });
      return result.orElse(0);
   }

   public MapleGuildResponse sendInvitation(MapleClient c, String targetName) {
      Optional<MapleCharacter> mc = c.getChannelServer().getPlayerStorage().getCharacterByName(targetName);
      if (mc.isEmpty()) {
         return MapleGuildResponse.NOT_IN_CHANNEL;
      }
      if (mc.get().getGuildId() > 0) {
         return MapleGuildResponse.ALREADY_IN_GUILD;
      }

      MapleCharacter sender = c.getPlayer();
      if (MapleInviteCoordinator.createInvite(MapleInviteCoordinator.InviteType.GUILD, sender, sender.getGuildId(), mc.get().getId())) {
         mc.get().getClient().announce(MaplePacketCreator.guildInvite(sender.getGuildId(), sender.getName()));
         return null;
      } else {
         return MapleGuildResponse.MANAGING_INVITE;
      }
   }

   public boolean answerInvitation(int targetId, String targetName, int guildId, boolean answer) {
      MapleInviteCoordinator.MapleInviteResult res = MapleInviteCoordinator.answerInvite(MapleInviteCoordinator.InviteType.GUILD, targetId, guildId, answer);

      MapleGuildResponse mgr;
      MapleCharacter sender = res.from;
      switch (res.result) {
         case ACCEPTED:
            return true;

         case DENIED:
            mgr = MapleGuildResponse.DENIED_INVITE;
            break;

         default:
            mgr = MapleGuildResponse.NOT_FOUND_INVITE;
      }

      if (mgr != null && sender != null) {
         sender.announce(mgr.getPacket(targetName));
      }
      return false;
   }

   public Set<MapleCharacter> getEligiblePlayersForGuild(MapleCharacter guildLeader) {
      Set<MapleCharacter> guildMembers = new HashSet<>();
      guildMembers.add(guildLeader);

      MapleMatchCheckerCoordinator mmce = guildLeader.getWorldServer().getMatchCheckerCoordinator();
      for (MapleCharacter chr : guildLeader.getMap().getAllPlayers()) {
         if (chr.getParty() == null && chr.getGuild().isEmpty() && mmce.getMatchConfirmationLeaderid(chr.getId()) == -1) {
            guildMembers.add(chr);
         }
      }

      return guildMembers;
   }

   public void displayGuildRanks(MapleClient c, int npcid) {
      DatabaseConnection.withConnection(connection -> c.announce(MaplePacketCreator.showGuildRanks(npcid, GuildProvider.getInstance().getGuildRankData(connection))));
   }

   public int getIncreaseGuildCost(int size) {
      int cost = ServerConstants.EXPAND_GUILD_BASE_COST + Math.max(0, (size - 15) / 5) * ServerConstants.EXPAND_GUILD_TIER_COST;

      if (size > 30) {
         return Math.min(ServerConstants.EXPAND_GUILD_MAX_COST, Math.max(cost, 5000000));
      } else {
         return cost;
      }
   }
}
