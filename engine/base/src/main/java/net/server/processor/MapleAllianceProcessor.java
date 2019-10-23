package net.server.processor;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import client.MapleCharacter;
import client.MapleClient;
import client.database.administrator.AllianceAdministrator;
import client.database.administrator.AllianceGuildAdministrator;
import client.database.data.AllianceData;
import client.database.provider.AllianceGuildProvider;
import client.database.provider.AllianceProvider;
import net.server.Server;
import net.server.coordinator.MapleInviteCoordinator;
import net.server.guild.MapleAlliance;
import net.server.guild.MapleGuild;
import net.server.guild.MapleGuildCharacter;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import tools.DatabaseConnection;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.PacketInput;
import tools.packet.alliance.AllianceInvite;
import tools.packet.alliance.AllianceNotice;
import tools.packet.alliance.DisbandAlliance;
import tools.packet.alliance.GetGuildAlliances;
import tools.packet.alliance.RemoveGuildFromAlliance;
import tools.packet.alliance.UpdateAllianceInfo;

public class MapleAllianceProcessor {
   private static MapleAllianceProcessor ourInstance = new MapleAllianceProcessor();

   public static MapleAllianceProcessor getInstance() {
      return ourInstance;
   }

   private MapleAllianceProcessor() {
   }

   public boolean canBeUsedAllianceName(String name) {
      if (name.contains(" ") || name.length() > 12) {
         return false;
      }
      boolean allianceExists = DatabaseConnection.getInstance().withConnectionResult(connection -> AllianceProvider.getInstance().allianceExists(connection, name)).orElse(false);
      return !allianceExists;
   }

   public MapleAlliance createAlliance(MapleParty party, String name) {
      List<MapleCharacter> guildMasters = getPartyGuildMasters(party);
      if (guildMasters.size() != 2) {
         return null;
      }

      List<Integer> guilds = guildMasters.stream().map(MapleCharacter::getGuildId).collect(Collectors.toList());
      MapleAlliance alliance = createAllianceOnDb(guilds, name);
      if (alliance != null) {
         alliance.capacity_$eq(guilds.size());
         guilds.forEach(alliance::addGuild);

         int id = alliance.id();
         try {
            for (int i = 0; i < guildMasters.size(); i++) {
               final int index = i;
               int guildId = guilds.get(index);
               MapleGuildProcessor.getInstance().setGuildAllianceId(guildId, id);
               MapleGuildProcessor.getInstance().resetAllianceGuildPlayersRank(guildId);

               MapleCharacter chr = guildMasters.get(index);
               chr.getMGC().setAllianceRank((index == 0) ? 1 : 2);
               Server.getInstance().getGuild(chr.getGuildId())
                     .flatMap(guild -> guild.getMGC(chr.getId()))
                     .ifPresent(guildCharacter -> guildCharacter.setAllianceRank((index == 0) ? 1 : 2));
               chr.saveGuildStatus();
            }

            Server.getInstance().addAlliance(id, alliance);

            int worldId = guildMasters.get(0).getWorld();
            Server.getInstance().allianceMessage(id, new UpdateAllianceInfo(alliance, worldId), -1, -1);
            Server.getInstance().allianceMessage(id, new GetGuildAlliances(alliance, worldId), -1, -1);  // thanks Vcoc for noticing guilds from other alliances being visually stacked here due to this not being updated
         } catch (Exception e) {
            e.printStackTrace();
            return null;
         }
      }

      return alliance;
   }

   private List<MapleCharacter> getPartyGuildMasters(MapleParty party) {
      List<MapleCharacter> mcl = new LinkedList<>();

      for (MaplePartyCharacter mpc : party.getMembers()) {
         MapleCharacter chr = mpc.getPlayer();
         if (chr != null) {
            MapleCharacter lchr = party.getLeader().getPlayer();
            if (chr.getGuildRank() == 1 && lchr != null && chr.getMapId() == lchr.getMapId()) {
               mcl.add(chr);
            }
         }
      }

      if (!mcl.isEmpty() && !mcl.get(0).isPartyLeader()) {
         for (int i = 1; i < mcl.size(); i++) {
            if (mcl.get(i).isPartyLeader()) {
               MapleCharacter temp = mcl.get(0);
               mcl.set(0, mcl.get(i));
               mcl.set(i, temp);
            }
         }
      }

      return mcl;
   }

   private MapleAlliance createAllianceOnDb(List<Integer> guilds, String name) {
      // will create an alliance, where the first guild listed is the leader and the alliance name MUST BE already checked for unicity.
      int id = DatabaseConnection.getInstance().withConnectionResult(connection -> {
         int allianceId = AllianceAdministrator.getInstance().createAlliance(connection, name);
         AllianceGuildAdministrator.getInstance().addGuilds(connection, allianceId, guilds);
         return allianceId;
      }).orElse(-1);
      return new MapleAlliance(name, id);
   }

   public Optional<MapleAlliance> loadAlliance(int id) {
      if (id <= 0) {
         return Optional.empty();
      }
      MapleAlliance alliance = new MapleAlliance(null, -1);
      DatabaseConnection.getInstance().withConnection(connection -> {
         AllianceProvider.getInstance().getAllianceData(connection, id).ifPresent(data -> setData(id, alliance, data));
         AllianceGuildProvider.getInstance().getGuildsForAlliance(connection, id).forEach(alliance::addGuild);
      });
      return Optional.of(alliance);
   }

   private void setData(int id, MapleAlliance alliance, AllianceData data) {
      alliance.id_$eq(id);
      alliance.capacity_$eq(data.capacity());
      alliance.name_$eq(data.name());
      alliance.notice_$eq(data.notice());

      String[] ranks = new String[5];
      ranks[0] = data.rank1();
      ranks[1] = data.rank2();
      ranks[2] = data.rank3();
      ranks[3] = data.rank4();
      ranks[4] = data.rank5();
      alliance.rankTitles_$eq(ranks);
   }

   public void disbandAlliance(int allianceId) {
      DatabaseConnection.getInstance().withConnection(connection -> {
         AllianceAdministrator.getInstance().deleteAlliance(connection, allianceId);
         AllianceGuildAdministrator.getInstance().deleteForAlliance(connection, allianceId);
      });

      Server.getInstance().allianceMessage(allianceId, new DisbandAlliance(allianceId), -1, -1);
      Server.getInstance().disbandAlliance(allianceId);
   }

   public boolean removeGuildFromAlliance(int allianceId, int guildId, int worldId) {
      Server server = Server.getInstance();
      Optional<MapleAlliance> allianceOptional = server.getAlliance(allianceId);
      if (allianceOptional.isEmpty()) {
         return false;
      }

      if (allianceOptional.map(this::getLeader).map(MapleGuildCharacter::getGuildId).filter(id -> id == guildId).isPresent()) {
         return false;
      }

      MapleAlliance alliance = allianceOptional.get();
      server.allianceMessage(alliance.id(), new RemoveGuildFromAlliance(alliance, guildId, worldId), -1, -1);
      server.removeGuildFromAlliance(alliance.id(), guildId);
      removeGuildFromAllianceOnDb(guildId);

      server.allianceMessage(alliance.id(), new GetGuildAlliances(alliance, worldId), -1, -1);
      server.allianceMessage(alliance.id(), new AllianceNotice(alliance.id(), alliance.notice()), -1, -1);
      MasterBroadcaster.getInstance().sendToGuild(guildId, new DisbandAlliance(alliance.id()));

      String guildName = server.getGuild(guildId, worldId).map(MapleGuild::getName).orElse("");
      MessageBroadcaster.getInstance().sendAllianceServerNotice(alliance, ServerNoticeType.NOTICE, "[" + guildName + "] guild has left the union.");
      return true;
   }

   private void removeGuildFromAllianceOnDb(int guildId) {
      DatabaseConnection.getInstance().withConnection(connection -> AllianceGuildAdministrator.getInstance().removeGuild(connection, guildId));
   }

   public void sendInvitation(MapleClient c, String targetGuildName, int allianceId) {
      Server.getInstance().getGuildByName(targetGuildName).ifPresentOrElse(guild -> {
         if (guild.getAllianceId() > 0) {
            MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.PINK_TEXT, "The entered guild is already registered on a guild alliance.");
         } else {

            Optional<MapleCharacter> victim = guild.getMGC(guild.getLeaderId()).map(MapleGuildCharacter::getCharacter);
            if (victim.isEmpty()) {
               MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.PINK_TEXT, "The master of the guild that you offered an invitation is currently not online.");
            } else {
               MapleCharacter victimCharacter = victim.get();
               if (MapleInviteCoordinator.createInvite(MapleInviteCoordinator.InviteType.ALLIANCE, c.getPlayer(), allianceId, victimCharacter.getId())) {
                  PacketCreator.announce(victimCharacter, new AllianceInvite(allianceId, c.getPlayer().getName()));
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.PINK_TEXT, "The master of the guild that you offered an invitation is currently managing another invite.");
               }
            }
         }
      }, () -> MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.PINK_TEXT, "The entered guild does not exist."));
   }

   public boolean answerInvitation(int targetId, String targetGuildName, int allianceId, boolean answer) {
      MapleInviteCoordinator.MapleInviteResult res = MapleInviteCoordinator.answerInvite(MapleInviteCoordinator.InviteType.ALLIANCE, targetId, allianceId, answer);

      String msg;
      MapleCharacter sender = res.from;
      switch (res.result) {
         case ACCEPTED:
            return true;

         case DENIED:
            msg = "[" + targetGuildName + "] guild has denied your guild alliance invitation.";
            break;

         default:
            msg = "The guild alliance request has not been accepted, since the invitation expired.";
      }

      if (sender != null) {
         MessageBroadcaster.getInstance().sendServerNotice(sender, ServerNoticeType.PINK_TEXT, msg);
      }

      return false;
   }

   public void saveToDB(MapleAlliance alliance) {
      DatabaseConnection.getInstance().withConnection(connection -> {
         AllianceAdministrator.getInstance().updateAlliance(connection, alliance.id(), alliance.capacity(), alliance.notice(), alliance.rankTitle(0), alliance.rankTitle(1), alliance.rankTitle(2), alliance.rankTitle(3), alliance.rankTitle(4));
         AllianceGuildAdministrator.getInstance().deleteForAlliance(connection, alliance.id());
         AllianceGuildAdministrator.getInstance().addGuilds(connection, alliance.id(), alliance.guilds());
      });
   }

   public void updateAlliancePackets(MapleAlliance alliance, MapleCharacter chr) {
      if (alliance.id() > 0) {
         this.broadcastMessage(alliance, new UpdateAllianceInfo(alliance, chr.getWorld()));
         this.broadcastMessage(alliance, new AllianceNotice(alliance.id(), alliance.notice()));
      }
   }

   protected void broadcastMessage(MapleAlliance alliance, PacketInput packetInput) {
      Server.getInstance().allianceMessage(alliance.id(), packetInput, -1, -1);
   }

   public MapleGuildCharacter getLeader(MapleAlliance alliance) {
      return alliance.guilds().stream()
            .map(guildId -> Server.getInstance().getGuild(guildId))
            .filter(Optional::isPresent)
            .map(guild -> guild.get().getMGC(guild.get().getLeaderId()))
            .flatMap(Optional::stream)
            .filter(character -> character.getAllianceRank() == 1)
            .findFirst().orElseThrow();
   }
}
