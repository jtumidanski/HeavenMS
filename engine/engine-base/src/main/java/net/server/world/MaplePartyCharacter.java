package net.server.world;

import java.util.Optional;

import client.MapleCharacter;
import constants.MapleJob;

public class MaplePartyCharacter {
   private String name;
   private int id;
   private int level;
   private int channel, world;
   private int jobId;
   private int mapId;
   private boolean online;
   private MapleJob job;
   private Optional<MapleCharacter> character;

   public MaplePartyCharacter(MapleCharacter character) {
      this.character = Optional.of(character);
      this.name = character.getName();
      this.level = character.getLevel();
      this.channel = character.getClient().getChannel();
      this.world = character.getWorld();
      this.id = character.getId();
      this.jobId = character.getJob().getId();
      this.mapId = character.getMapId();
      this.online = true;
      this.job = character.getJob();
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
         this.character = Optional.empty();
      }
   }

   public int getMapId() {
      return mapId;
   }

   public void setMapId(int mapId) {
      this.mapId = mapId;
   }

   public String getName() {
      return name;
   }

   public int getId() {
      return id;
   }

   public int getJobId() {
      return jobId;
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
