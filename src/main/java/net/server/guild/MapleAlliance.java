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
import client.database.administrator.AllianceAdministrator;
import client.database.administrator.AllianceGuildAdministrator;
import net.server.Server;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;

/**
 * @author XoticStory
 * @author Ronan
 */
public class MapleAlliance {
   final private List<Integer> guilds = new LinkedList<>();

   private int allianceId;
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

   public void setAllianceId(int allianceId) {
      this.allianceId = allianceId;
   }

   public void setName(String name) {
      this.name = name;
   }
}
