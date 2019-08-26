package net.server.processor;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
import tools.MaplePacketCreator;

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

      List<Integer> guilds = new LinkedList<>();
      for (MapleCharacter mc : guildMasters) guilds.add(mc.getGuildId());
      MapleAlliance alliance = createAllianceOnDb(guilds, name);
      if (alliance != null) {
         alliance.setCapacity(guilds.size());
         for (Integer g : guilds)
            alliance.addGuild(g);

         int id = alliance.getId();
         try {
            for (int i = 0; i < guildMasters.size(); i++) {
               final int index = i;
               int guildId = guilds.get(index);
               Server.getInstance().setGuildAllianceId(guildId, id);
               Server.getInstance().resetAllianceGuildPlayersRank(guildId);

               MapleCharacter chr = guildMasters.get(index);
               chr.getMGC().setAllianceRank((index == 0) ? 1 : 2);
               Server.getInstance().getGuild(chr.getGuildId()).ifPresent(guild -> guild.getMGC(chr.getId()).setAllianceRank((index == 0) ? 1 : 2));
               chr.saveGuildStatus();
            }

            Server.getInstance().addAlliance(id, alliance);

            int worldId = guildMasters.get(0).getWorld();
            Server.getInstance().allianceMessage(id, MaplePacketCreator.updateAllianceInfo(alliance, worldId), -1, -1);
            Server.getInstance().allianceMessage(id, MaplePacketCreator.getGuildAlliances(alliance, worldId), -1, -1);  // thanks Vcoc for noticing guilds from other alliances being visually stacked here due to this not being updated
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
         if (mpc.getPlayer().getGuildRank() == 1 && mpc.getPlayer().getMapId() == party.getLeader().getPlayer().getMapId()) {
            mcl.add(mpc.getPlayer());
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
      alliance.setAllianceId(id);
      alliance.setCapacity(data.getCapacity());
      alliance.setName(data.getName());
      alliance.setNotice(data.getNotice());

      String[] ranks = new String[5];
      ranks[0] = data.getRank1();
      ranks[1] = data.getRank2();
      ranks[2] = data.getRank3();
      ranks[3] = data.getRank4();
      ranks[4] = data.getRank5();
      alliance.setRankTitle(ranks);
   }

   public void disbandAlliance(int allianceId) {
      DatabaseConnection.getInstance().withConnection(connection -> {
         AllianceAdministrator.getInstance().deleteAlliance(connection, allianceId);
         AllianceGuildAdministrator.getInstance().deleteForAlliance(connection, allianceId);
      });

      Server.getInstance().allianceMessage(allianceId, MaplePacketCreator.disbandAlliance(allianceId), -1, -1);
      Server.getInstance().disbandAlliance(allianceId);
   }

   public boolean removeGuildFromAlliance(int allianceId, int guildId, int worldId) {
      Server server = Server.getInstance();
      Optional<MapleAlliance> allianceOptional = server.getAlliance(allianceId);
      if (allianceOptional.isEmpty()) {
         return false;
      }

      if (allianceOptional.map(MapleAlliance::getLeader).map(MapleGuildCharacter::getGuildId).filter(id -> id == guildId).isPresent()) {
         return false;
      }

      MapleAlliance alliance = allianceOptional.get();
      server.allianceMessage(alliance.getId(), MaplePacketCreator.removeGuildFromAlliance(alliance, guildId, worldId), -1, -1);
      server.removeGuildFromAlliance(alliance.getId(), guildId);
      removeGuildFromAllianceOnDb(guildId);

      server.allianceMessage(alliance.getId(), MaplePacketCreator.getGuildAlliances(alliance, worldId), -1, -1);
      server.allianceMessage(alliance.getId(), MaplePacketCreator.allianceNotice(alliance.getId(), alliance.getNotice()), -1, -1);
      server.guildMessage(guildId, MaplePacketCreator.disbandAlliance(alliance.getId()));

      String guildName = server.getGuild(guildId, worldId).map(MapleGuild::getName).orElse("");
      alliance.dropMessage("[" + guildName + "] guild has left the union.");
      return true;
   }

   private void removeGuildFromAllianceOnDb(int guildId) {
      DatabaseConnection.getInstance().withConnection(connection -> AllianceGuildAdministrator.getInstance().removeGuild(connection, guildId));
   }

   public void sendInvitation(MapleClient c, String targetGuildName, int allianceId) {
      Server.getInstance().getGuildByName(targetGuildName).ifPresentOrElse(guild -> {
         if (guild.getAllianceId() > 0) {
            c.getPlayer().dropMessage(5, "The entered guild is already registered on a guild alliance.");
         } else {
            MapleCharacter victim = guild.getMGC(guild.getLeaderId()).getCharacter();
            if (victim == null) {
               c.getPlayer().dropMessage(5, "The master of the guild that you offered an invitation is currently not online.");
            } else {
               if (MapleInviteCoordinator.createInvite(MapleInviteCoordinator.InviteType.ALLIANCE, c.getPlayer(), allianceId, victim.getId())) {
                  victim.getClient().announce(MaplePacketCreator.allianceInvite(allianceId, c.getPlayer()));
               } else {
                  c.getPlayer().dropMessage(5, "The master of the guild that you offered an invitation is currently managing another invite.");
               }
            }
         }
      }, () -> c.getPlayer().dropMessage(5, "The entered guild does not exist."));
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
         sender.dropMessage(5, msg);
      }

      return false;
   }

}
