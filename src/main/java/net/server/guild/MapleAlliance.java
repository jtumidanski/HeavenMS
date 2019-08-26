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
package net.server.guild;

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
import net.server.coordinator.MapleInviteCoordinator.InviteType;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;

/**
 * @author XoticStory
 * @author Ronan
 */
public class MapleAlliance {
   final private List<Integer> guilds = new LinkedList<>();

   private int allianceId = -1;
   private int capacity;
   private String name;
   private String notice = "";
   private String[] rankTitles = new String[5];

   public MapleAlliance(String name, int id) {
      this.name = name;
      allianceId = id;
      String[] ranks = {"Master", "Jr. Master", "Member", "Member", "Member"};
      System.arraycopy(ranks, 0, rankTitles, 0, 5);
   }

   public static boolean canBeUsedAllianceName(String name) {
      if (name.contains(" ") || name.length() > 12) {
         return false;
      }
      boolean allianceExists = DatabaseConnection.withConnectionResult(connection -> AllianceProvider.getInstance().allianceExists(connection, name)).orElse(false);
      return !allianceExists;
   }

   private static List<MapleCharacter> getPartyGuildMasters(MapleParty party) {
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

   public static MapleAlliance createAlliance(MapleParty party, String name) {
      List<MapleCharacter> guildMasters = getPartyGuildMasters(party);
      if (guildMasters.size() != 2) {
         return null;
      }

      List<Integer> guilds = new LinkedList<>();
      for (MapleCharacter mc : guildMasters) guilds.add(mc.getGuildId());
      MapleAlliance alliance = MapleAlliance.createAllianceOnDb(guilds, name);
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

            int worldid = guildMasters.get(0).getWorld();
            Server.getInstance().allianceMessage(id, MaplePacketCreator.updateAllianceInfo(alliance, worldid), -1, -1);
            Server.getInstance().allianceMessage(id, MaplePacketCreator.getGuildAlliances(alliance, worldid), -1, -1);  // thanks Vcoc for noticing guilds from other alliances being visually stacked here due to this not being updated
         } catch (Exception e) {
            e.printStackTrace();
            return null;
         }
      }

      return alliance;
   }

   public static MapleAlliance createAllianceOnDb(List<Integer> guilds, String name) {
      // will create an alliance, where the first guild listed is the leader and the alliance name MUST BE already checked for unicity.
      int id = DatabaseConnection.withConnectionResult(connection -> {
         int allianceId = AllianceAdministrator.getInstance().createAlliance(connection, name);
         AllianceGuildAdministrator.getInstance().addGuilds(connection, allianceId, guilds);
         return allianceId;
      }).orElse(-1);
      return new MapleAlliance(name, id);
   }

   public static Optional<MapleAlliance> loadAlliance(int id) {
      if (id <= 0) {
         return Optional.empty();
      }
      MapleAlliance alliance = new MapleAlliance(null, -1);
      DatabaseConnection.withConnection(connection -> {
         AllianceProvider.getInstance().getAllianceData(connection, id).ifPresent(data -> setData(id, alliance, data));
         AllianceGuildProvider.getInstance().getGuildsForAlliance(connection, id).forEach(alliance::addGuild);
      });
      return Optional.of(alliance);
   }

   private static void setData(int id, MapleAlliance alliance, AllianceData data) {
      alliance.allianceId = id;
      alliance.capacity = data.getCapacity();
      alliance.name = data.getName();
      alliance.notice = data.getNotice();

      String[] ranks = new String[5];
      ranks[0] = data.getRank1();
      ranks[1] = data.getRank2();
      ranks[2] = data.getRank3();
      ranks[3] = data.getRank4();
      ranks[4] = data.getRank5();
      alliance.rankTitles = ranks;
   }

   public static void disbandAlliance(int allianceId) {
      DatabaseConnection.withConnection(connection -> {
         AllianceAdministrator.getInstance().deleteAlliance(connection, allianceId);
         AllianceGuildAdministrator.getInstance().deleteForAlliance(connection, allianceId);
      });

      Server.getInstance().allianceMessage(allianceId, MaplePacketCreator.disbandAlliance(allianceId), -1, -1);
      Server.getInstance().disbandAlliance(allianceId);
   }

   private static void removeGuildFromAllianceOnDb(int guildId) {
      DatabaseConnection.withConnection(connection -> AllianceGuildAdministrator.getInstance().removeGuild(connection, guildId));
   }

   public static boolean removeGuildFromAlliance(int allianceId, int guildId, int worldId) {
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

   public static void sendInvitation(MapleClient c, String targetGuildName, int allianceId) {
      Server.getInstance().getGuildByName(targetGuildName).ifPresentOrElse(guild -> {
         if (guild.getAllianceId() > 0) {
            c.getPlayer().dropMessage(5, "The entered guild is already registered on a guild alliance.");
         } else {
            MapleCharacter victim = guild.getMGC(guild.getLeaderId()).getCharacter();
            if (victim == null) {
               c.getPlayer().dropMessage(5, "The master of the guild that you offered an invitation is currently not online.");
            } else {
               if (MapleInviteCoordinator.createInvite(InviteType.ALLIANCE, c.getPlayer(), allianceId, victim.getId())) {
                  victim.getClient().announce(MaplePacketCreator.allianceInvite(allianceId, c.getPlayer()));
               } else {
                  c.getPlayer().dropMessage(5, "The master of the guild that you offered an invitation is currently managing another invite.");
               }
            }
         }
      }, () -> c.getPlayer().dropMessage(5, "The entered guild does not exist."));
   }

   public static boolean answerInvitation(int targetId, String targetGuildName, int allianceId, boolean answer) {
      MapleInviteCoordinator.MapleInviteResult res = MapleInviteCoordinator.answerInvite(InviteType.ALLIANCE, targetId, allianceId, answer);

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

   public void saveToDB() {
      DatabaseConnection.withConnection(connection -> {
         AllianceAdministrator.getInstance().updateAlliance(connection, this.allianceId, this.capacity, this.notice, this.rankTitles[0], this.rankTitles[1], this.rankTitles[2], this.rankTitles[3], this.rankTitles[4]);
         AllianceGuildAdministrator.getInstance().deleteForAlliance(connection, this.allianceId);
         AllianceGuildAdministrator.getInstance().addGuilds(connection, this.allianceId, guilds);
      });
   }

   public void updateAlliancePackets(MapleCharacter chr) {
      if (allianceId > 0) {
         this.broadcastMessage(MaplePacketCreator.updateAllianceInfo(this, chr.getWorld()));
         this.broadcastMessage(MaplePacketCreator.allianceNotice(this.getId(), this.getNotice()));
      }
   }

   public boolean removeGuild(int gid) {
      synchronized (guilds) {
         int index = getGuildIndex(gid);
         if (index == -1) {
            return false;
         }

         guilds.remove(index);
         return true;
      }
   }

   public boolean addGuild(int gid) {
      synchronized (guilds) {
         if (guilds.size() == capacity || getGuildIndex(gid) > -1) {
            return false;
         }

         guilds.add(gid);
         return true;
      }
   }

   private int getGuildIndex(int gid) {
      synchronized (guilds) {
         for (int i = 0; i < guilds.size(); i++) {
            if (guilds.get(i) == gid) {
               return i;
            }
         }
         return -1;
      }
   }

   public void setRankTitle(String[] ranks) {
      rankTitles = ranks;
   }

   public String getRankTitle(int rank) {
      return rankTitles[rank - 1];
   }

   public List<Integer> getGuilds() {
      synchronized (guilds) {
         List<Integer> guilds_ = new LinkedList<>();
         for (int guild : guilds) {
            if (guild != -1) {
               guilds_.add(guild);
            }
         }
         return guilds_;
      }
   }

   public String getAllianceNotice() {
      return notice;
   }

   public String getNotice() {
      return notice;
   }

   public void setNotice(String notice) {
      this.notice = notice;
   }

   public void increaseCapacity(int inc) {
      this.capacity += inc;
   }

   public int getCapacity() {
      return this.capacity;
   }

   public void setCapacity(int newCapacity) {
      this.capacity = newCapacity;
   }

   public int getId() {
      return allianceId;
   }

   public String getName() {
      return name;
   }

   public MapleGuildCharacter getLeader() {
      synchronized (guilds) {
         return guilds.stream()
               .map(guildId -> Server.getInstance().getGuild(guildId))
               .filter(Optional::isPresent)
               .map(guild -> guild.get().getMGC(guild.get().getLeaderId()))
               .filter(character -> character.getAllianceRank() == 1)
               .findFirst().orElseThrow();
      }
   }

   public void dropMessage(String message) {
      dropMessage(5, message);
   }

   public void dropMessage(int type, String message) {
      synchronized (guilds) {
         guilds.stream()
               .map(guildId -> Server.getInstance().getGuild(guildId))
               .filter(Optional::isPresent)
               .forEach(guild -> guild.get().dropMessage(type, message));
      }
   }

   public void broadcastMessage(byte[] packet) {
      Server.getInstance().allianceMessage(allianceId, packet, -1, -1);
   }
}
