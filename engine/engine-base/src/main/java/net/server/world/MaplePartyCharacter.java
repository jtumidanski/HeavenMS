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
package net.server.world;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleJob;

public class MaplePartyCharacter {
   private String name;
   private int id;
   private int level;
   private int channel, world;
   private int jobid;
   private int mapid;
   private boolean online;
   private MapleJob job;
   private Optional<MapleCharacter> character;

   public MaplePartyCharacter(MapleCharacter maplechar) {
      this.character = Optional.of(maplechar);
      this.name = maplechar.getName();
      this.level = maplechar.getLevel();
      this.channel = maplechar.getClient().getChannel();
      this.world = maplechar.getWorld();
      this.id = maplechar.getId();
      this.jobid = maplechar.getJob().getId();
      this.mapid = maplechar.getMapId();
      this.online = true;
      this.job = maplechar.getJob();
   }

   public MaplePartyCharacter() {
      this.name = "";
   }

   public Optional<MapleCharacter> getPlayer() {
      return character;
   }

   public boolean inMap(int mapId) {
      return getPlayer()
            .map(MapleCharacter::getMapId)
            .map(id -> id != mapId)
            .orElse(false);
   }

   public MapleJob getJob() {
      return job;
   }

   public int getLevel() {
      return level;
   }

   public int getChannel() {
      return channel;
   }

   public void setChannel(int channel) {
      this.channel = channel;
   }

   public boolean isLeader() {
      return getPlayer().map(MapleCharacter::isPartyLeader).orElse(false);
   }

   public boolean isOnline() {
      return online;
   }

   public void setOnline(boolean online) {
      this.online = online;
      if (!online) {
         this.character = Optional.empty();  // thanks Feras for noticing offline party members retaining whole character object unnecessarily
      }
   }

   public int getMapId() {
      return mapid;
   }

   public void setMapId(int mapid) {
      this.mapid = mapid;
   }

   public String getName() {
      return name;
   }

   public int getId() {
      return id;
   }

   public int getJobId() {
      return jobid;
   }

   public int getGuildId() {
      return getPlayer().map(MapleCharacter::getGuildId).orElse(-1);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final MaplePartyCharacter other = (MaplePartyCharacter) obj;
      if (name == null) {
         return other.name == null;
      } else {
         return name.equals(other.name);
      }
   }

   public int getWorld() {
      return world;
   }

}
