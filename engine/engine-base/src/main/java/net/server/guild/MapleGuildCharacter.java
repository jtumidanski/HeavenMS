package net.server.guild;

import java.util.Optional;

import client.MapleCharacter;

public class MapleGuildCharacter {
   private Optional<MapleCharacter> character;
   private int level;
   private int id;
   private int world, channel;
   private int jobId;
   private int guildRank;
   private int guildId;
   private int allianceRank;
   private boolean online;
   private String name;

   public MapleGuildCharacter(MapleCharacter c) {
      this.character = Optional.of(c);
      this.name = c.getName();
      this.level = c.getLevel();
      this.id = c.getId();
      this.channel = c.getClient().getChannel();
      this.world = c.getWorld();
      this.jobId = c.getJob().getId();
      this.guildRank = c.getGuildRank();
      this.guildId = c.getGuildId();
      this.online = true;
      this.allianceRank = c.getAllianceRank();
   }

   public MapleGuildCharacter(MapleCharacter c, int _id, int _lv, String _name, int _channel, int _world, int _job, int _rank, int _gid, boolean _on, int _allianceRank) {
      this.character = Optional.ofNullable(c);
      this.level = _lv;
      this.id = _id;
      this.name = _name;
      if (_on) {
         this.channel = _channel;
         this.world = _world;
      }
      this.jobId = _job;
      this.online = _on;
      this.guildRank = _rank;
      this.guildId = _gid;
      this.allianceRank = _allianceRank;
   }

   public Optional<MapleCharacter> getCharacter() {
      return character;
   }

   public void setCharacter(MapleCharacter ch) {
      this.character = Optional.of(ch);
   }

   public void clear() {
      this.character = Optional.empty();
   }

   public int getLevel() {
      return level;
   }

   public void setLevel(int l) {
      level = l;
   }

   public int getId() {
      return id;
   }

   public int getChannel() {
      return channel;
   }

   public void setChannel(int ch) {
      channel = ch;
   }

   public int getWorld() {
      return world;
   }

   public int getJobId() {
      return jobId;
   }

   public void setJobId(int job) {
      jobId = job;
   }

   public int getGuildId() {
      return guildId;
   }

   public void setGuildId(int gid) {
      guildId = gid;
      character.ifPresent(reference -> setGuildId(gid));
   }

   public int getGuildRank() {
      return guildRank;
   }

   public void setGuildRank(int rank) {
      guildRank = rank;
      character.ifPresent(reference -> reference.setGuildRank(rank));
   }

   public void setOfflineGuildRank(int rank) {
      guildRank = rank;
   }

   public int getAllianceRank() {
      return allianceRank;
   }

   public void setAllianceRank(int rank) {
      allianceRank = rank;
      character.ifPresent(reference -> reference.setAllianceRank(rank));
   }

   public boolean isOnline() {
      return online;
   }

   public void setOnline(boolean f) {
      online = f;
   }

   public String getName() {
      return name;
   }

   public boolean is(int characterId) {
      return characterId == id;
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
      hash = 19 * hash + this.id;
      hash = 19 * hash + (this.name != null ? this.name.hashCode() : 0);
      return hash;
   }
}
