package net.server.guild;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record MapleAlliance(String name, Integer id, Integer capacity, String notice, String[] rankTitles,
                            List<Integer> guilds) {
   public MapleAlliance(String name, Integer id) {
      this(name, id, 0);
   }

   public MapleAlliance(String name, Integer id, Integer capacity) {
      this(name, id, capacity, "", new String[]{"Master", "Jr. Master", "Master", "Master", "Master"});
   }

   public MapleAlliance(String name, Integer id, Integer capacity, String notice, String[] rankTitles) {
      this(name, id, capacity, notice, rankTitles, Collections.emptyList());
   }

   public MapleAlliance setRankTitles(String[] rankTitles) {
      return new MapleAlliance(name, id, capacity, notice, rankTitles, guilds);
   }

   public MapleAlliance setGuilds(List<Integer> guilds) {
      return new MapleAlliance(name, id, capacity, notice, rankTitles, guilds);
   }

   public MapleAlliance addGuild(Integer guildId) {
      if (guilds.size() == capacity || guilds.contains(guildId)) {
         return this;
      }

      List<Integer> newGuilds = new ArrayList<>(guilds);
      newGuilds.add(guildId);
      return new MapleAlliance(name, id, capacity, notice, rankTitles, Collections.unmodifiableList(newGuilds));
   }

   public MapleAlliance removeGuild(Integer guildId) {
      return new MapleAlliance(name, id, capacity, notice, rankTitles, guilds.stream()
            .filter(id -> !id.equals(guildId))
            .collect(Collectors.toUnmodifiableList()));
   }

   public MapleAlliance increaseCapacity(Integer increase) {
      return new MapleAlliance(name, id, capacity + increase, notice, rankTitles, guilds);
   }

   public MapleAlliance setNotice(String notice) {
      return new MapleAlliance(this.name, id, capacity, notice, rankTitles, guilds);
   }

   public String rankTitle(Integer rank) {
      if (rank < 1 || rank > rankTitles.length) {
         throw new UnsupportedOperationException();
      }
      return rankTitles[rank - 1];
   }
}
