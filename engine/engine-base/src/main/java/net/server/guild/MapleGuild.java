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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import client.MapleCharacter;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;

public class MapleGuild {
   private final List<MapleGuildCharacter> members;
   private final Lock membersLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.GUILD, true);
   private String[] rankTitles = new String[5]; // 1 = master, 2 = jr, 5 = lowest member
   private String name, notice;
   private int id, gp, logo, logoColor, leader, capacity, logoBG, logoBGColor, signature, allianceId;
   private int worldId;

   public MapleGuild(int guildId, int worldId) {
      this.id = guildId;
      this.worldId = worldId;
      members = new ArrayList<>();
   }

   public MapleGuild(int guildId, int worldId, String name, int gp, int logo, int logoColor, int logoBG,
                     int logoBGColor, int capacity, String[] rankTitles, int leader, String notice, int signature,
                     int allianceId) {
      this(guildId, worldId);
      this.name = name;
      this.gp = gp;
      this.logo = logo;
      this.logoColor = logoColor;
      this.logoBG = logoBG;
      this.logoBGColor = logoBGColor;
      this.capacity = capacity;
      this.rankTitles = rankTitles;
      this.leader = leader;
      this.notice = notice;
      this.signature = signature;
      this.allianceId = allianceId;
   }

   public int getId() {
      return id;
   }

   public int getLeaderId() {
      return leader;
   }

   public int setLeaderId(int charId) {
      return leader = charId;
   }

   public int getGP() {
      return gp;
   }

   public int getLogo() {
      return logo;
   }

   public void setLogo(int l) {
      logo = l;
   }

   public int getLogoColor() {
      return logoColor;
   }

   public void setLogoColor(int c) {
      logoColor = c;
   }

   public int getLogoBG() {
      return logoBG;
   }

   public void setLogoBG(int bg) {
      logoBG = bg;
   }

   public int getLogoBGColor() {
      return logoBGColor;
   }

   public void setLogoBGColor(int c) {
      logoBGColor = c;
   }

   public int getWorldId() {
      return worldId;
   }

   public String[] getRankTitles() {
      return rankTitles;
   }

   public String getNotice() {
      if (notice == null) {
         return "";
      }
      return notice;
   }

   public String getName() {
      return name;
   }

   public List<MapleGuildCharacter> getMembers() {
      membersLock.lock();
      try {
         return new ArrayList<>(members);
      } finally {
         membersLock.unlock();
      }
   }

   public List<MapleCharacter> getMemberCharacters() {
      membersLock.lock();
      try {
         return members.stream().map(MapleGuildCharacter::getCharacter).flatMap(Optional::stream).collect(Collectors.toList());
      } finally {
         membersLock.unlock();
      }
   }

   public int getCapacity() {
      return capacity;
   }

   public int getSignature() {
      return signature;
   }

   public String getRankTitle(int rank) {
      return rankTitles[rank - 1];
   }

   public void addGuildMember(MapleGuildCharacter mgc, MapleCharacter chr) {
      membersLock.lock();
      try {
         for (int i = members.size() - 1; i >= 0; i--) {
            if (members.get(i).getGuildRank() < 5 || members.get(i).getName().compareTo(mgc.getName()) < 0) {
               mgc.setCharacter(chr);
               members.add(i + 1, mgc);
               break;
            }
         }
      } finally {
         membersLock.unlock();
      }
   }

   public void leaveGuild(MapleGuildCharacter mgc) {
      membersLock.lock();
      try {
         members.remove(mgc);
      } finally {
         membersLock.unlock();
      }
   }

   public Optional<MapleGuildCharacter> findMember(int characterId) {
      membersLock.lock();
      try {
         return members.parallelStream().filter(guildCharacter -> guildCharacter.getId() == characterId).findFirst();
      } finally {
         membersLock.unlock();
      }
   }

   public void setGuildNotice(String notice) {
      this.notice = notice;
   }

   @Override
   public boolean equals(Object other) {
      if (!(other instanceof MapleGuildCharacter)) {
         return false;
      }
      MapleGuildCharacter o = (MapleGuildCharacter) other;
      return (o.getId() == id && o.getName().equals(name));
   }

   @Override
   public int hashCode() {
      int hash = 3;
      hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
      hash = 89 * hash + this.id;
      return hash;
   }

   public void changeRankTitle(String[] ranks) {
      System.arraycopy(ranks, 0, rankTitles, 0, 5);
   }

   public void setGuildEmblem(short bg, byte bgcolor, short logo, byte logocolor) {
      this.logoBG = bg;
      this.logoBGColor = bgcolor;
      this.logo = logo;
      this.logoColor = logocolor;
   }

   public void increaseCapacity(int amount) {
      capacity += amount;
   }

   public void gainGP(int amount) {
      this.gp += amount;
   }

   public void removeGP(int amount) {
      this.gp -= amount;
   }

   public int getAllianceId() {
      return allianceId;
   }

   public void setAllianceId(int aid) {
      this.allianceId = aid;
   }
}
