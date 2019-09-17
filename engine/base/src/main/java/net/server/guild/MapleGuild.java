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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import javax.swing.text.html.Option;

import client.MapleCharacter;
import client.database.administrator.CharacterAdministrator;
import client.database.administrator.GuildAdministrator;
import client.database.administrator.NoteAdministrator;
import client.database.data.GuildData;
import client.database.provider.CharacterProvider;
import client.database.provider.GuildProvider;
import net.server.PlayerStorage;
import net.server.Server;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import net.server.channel.Channel;
import net.server.processor.MapleAllianceProcessor;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;
import tools.Pair;

public class MapleGuild {

   private final List<MapleGuildCharacter> members;
   private final Lock membersLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.GUILD, true);
   private String[] rankTitles = new String[5]; // 1 = master, 2 = jr, 5 = lowest member
   private String name, notice;
   private int id, gp, logo, logoColor, leader, capacity, logoBG, logoBGColor, signature, allianceId;
   private int world;
   private Map<Integer, List<Integer>> notifications = new LinkedHashMap<>();
   private boolean bDirty = true;

   public MapleGuild(int guildid, int world) {
      this.world = world;
      members = new ArrayList<>();
      DatabaseConnection.getInstance().withConnection(connection -> {
         id = guildid;
         Optional<GuildData> guildData = GuildProvider.getInstance().getGuildDataById(connection, guildid);
         if (guildData.isEmpty()) {
            return;
         }

         guildData.ifPresent(data -> {
            name = data.getName();
            gp = data.getGp();
            logo = data.getLogo();
            logoColor = data.getLogoColor();
            logoBG = data.getLogoBackground();
            logoBGColor = data.getLogoBackgroundColor();
            capacity = data.getCapacity();
            rankTitles = data.getRankTitles();
            leader = data.getLeaderId();
            notice = data.getNotice();
            signature = data.getSignature();
            allianceId = data.getAllianceId();
         });

         CharacterProvider.getInstance().getGuildCharacterData(connection, guildid)
               .forEach(data -> members.add(new MapleGuildCharacter(null, data.getId(), data.getLevel(),
                     data.getName(), (byte) -1, world, data.getJob(), data.getGuildRank(), guildid, false,
                     data.getAllianceRank())));
      });
   }

   private void buildNotifications() {
      if (!bDirty) {
         return;
      }
      Set<Integer> chs = Server.getInstance().getOpenChannels(world);
      synchronized (notifications) {
         if (notifications.keySet().size() != chs.size()) {
            notifications.clear();
            for (Integer ch : chs) {
               notifications.put(ch, new LinkedList<>());
            }
         } else {
            for (List<Integer> l : notifications.values()) {
               l.clear();
            }
         }
      }

      membersLock.lock();
      try {
         for (MapleGuildCharacter mgc : members) {
            if (!mgc.isOnline()) {
               continue;
            }

            List<Integer> chl;
            synchronized (notifications) {
               chl = notifications.get(mgc.getChannel());
            }
            if (chl != null) {
               chl.add(mgc.getId());
            }
            //Unable to connect to Channel... error was here
         }
      } finally {
         membersLock.unlock();
      }

      bDirty = false;
   }

   public void writeToDB(boolean bDisband) {
      DatabaseConnection.getInstance().withConnection(connection -> {
         if (!bDisband) {
            GuildAdministrator.getInstance().update(connection, gp, logo, logoColor, logoBG, logoBGColor, rankTitles, capacity, notice, this.id);
         } else {
            CharacterAdministrator.getInstance().removeAllCharactersFromGuild(connection, this.id);
            GuildAdministrator.getInstance().deleteGuild(connection, this.id);
            membersLock.lock();
            try {
               this.broadcast(MaplePacketCreator.guildDisband(this.id));
            } finally {
               membersLock.unlock();
            }
         }
      });
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

   public int getCapacity() {
      return capacity;
   }

   public int getSignature() {
      return signature;
   }

   public void broadcastNameChanged() {
      PlayerStorage ps = Server.getInstance().getWorld(world).getPlayerStorage();

      getMembers().stream()
            .map(member -> ps.getCharacterById(member.getId()))
            .flatMap(Optional::stream)
            .filter(MapleCharacter::isLoggedinWorld)
            .forEach(character -> {
               byte[] packet = MaplePacketCreator.guildNameChanged(character.getId(), this.getName());
               character.getMap().broadcastMessage(character, packet);
            });
   }

   public void broadcastEmblemChanged() {
      PlayerStorage ps = Server.getInstance().getWorld(world).getPlayerStorage();
      getMembers().stream()
            .map(member -> ps.getCharacterById(member.getId()))
            .flatMap(Optional::stream)
            .filter(MapleCharacter::isLoggedinWorld)
            .forEach(character -> {
               byte[] packet = MaplePacketCreator.guildMarkChanged(character.getId(), this);
               character.getMap().broadcastMessage(character, packet);
            });
   }

   public void broadcastInfoChanged() {
      PlayerStorage ps = Server.getInstance().getWorld(world).getPlayerStorage();

      for (MapleGuildCharacter mgc : getMembers()) {
         Optional<MapleCharacter> chr = ps.getCharacterById(mgc.getId());
         if (chr.isEmpty() || !chr.get().isLoggedinWorld()) continue;

         byte[] packet = MaplePacketCreator.showGuildInfo(chr.get());
         chr.get().announce(packet);
      }
   }

   public void broadcast(final byte[] packet) {
      broadcast(packet, -1, BCOp.NONE);
   }

   public void broadcast(final byte[] packet, int exception) {
      broadcast(packet, exception, BCOp.NONE);
   }

   public void broadcast(final byte[] packet, int exceptionId, BCOp bcop) {
      membersLock.lock(); // membersLock awareness thanks to ProjectNano dev team
      try {
         synchronized (notifications) {
            if (bDirty) {
               buildNotifications();
            }
            try {
               for (Integer b : Server.getInstance().getOpenChannels(world)) {
                  if (notifications.get(b).size() > 0) {
                     if (bcop == BCOp.DISBAND) {
                        Server.getInstance().getWorld(world).setGuildAndRank(notifications.get(b), 0, 5, exceptionId);
                     } else if (bcop == BCOp.EMBLEMCHANGE) {
                        Server.getInstance().getWorld(world).changeEmblem(this.id, notifications.get(b), new MapleGuildSummary(this));
                     } else {
                        Server.getInstance().getWorld(world).sendPacket(notifications.get(b), packet, exceptionId);
                     }
                  }
               }
            } catch (Exception re) {
               re.printStackTrace();
               System.out.println("Failed to contact channel(s) for broadcast.");//fu?
            }
         }
      } finally {
         membersLock.unlock();
      }
   }


   public void guildMessage(final byte[] serverNotice) {
      membersLock.lock();
      try {
         members.stream().map(this::getChannelForGuildMember).forEach(pair -> sendGuildMessageForPair(serverNotice, pair));
      } finally {
         membersLock.unlock();
      }
   }

   /**
    * Sends a guild message for a <code>MapleGuildCharacter</code> - <code>Optional<Channel></code> pair.
    *
    * @param serverNotice the server notice
    * @param pair         the pair
    */
   private void sendGuildMessageForPair(byte[] serverNotice, Pair<MapleGuildCharacter, Optional<Channel>> pair) {
      pair.getRight().ifPresent(channel -> channel.getPlayerStorage().getCharacterById(pair.getLeft().getId()).ifPresent(character -> character.getClient().announce(serverNotice)));
   }

   /**
    * Given a guild member, create a pair with a channel the member is associated with.
    *
    * @param member the guild member
    * @return a pair
    */
   private Pair<MapleGuildCharacter, Optional<Channel>> getChannelForGuildMember(MapleGuildCharacter member) {
      return new Pair<>(
            member,
            Server.getInstance().getChannelsFromWorld(world).stream()
                  .filter(channel -> channel.getPlayerStorage().getCharacterById(member.getId()).isPresent())
                  .findFirst()
      );
   }

   public void broadcastMessage(byte[] packet) {
      Server.getInstance().guildMessage(id, packet);
   }

   public final void setOnline(int cid, boolean online, int channel) {
      membersLock.lock();
      try {
         boolean bBroadcast = true;
         for (MapleGuildCharacter mgc : members) {
            if (mgc.getId() == cid) {
               if (mgc.isOnline() && online) {
                  bBroadcast = false;
               }
               mgc.setOnline(online);
               mgc.setChannel(channel);
               break;
            }
         }
         if (bBroadcast) {
            this.broadcast(MaplePacketCreator.guildMemberOnline(id, cid, online), cid);
         }
         bDirty = true;
      } finally {
         membersLock.unlock();
      }
   }

   public void guildChat(String name, int cid, String message) {
      membersLock.lock();
      try {
         this.broadcast(MaplePacketCreator.multiChat(name, message, 2), cid);
      } finally {
         membersLock.unlock();
      }
   }

   public String getRankTitle(int rank) {
      return rankTitles[rank - 1];
   }

   public int addGuildMember(MapleGuildCharacter mgc, MapleCharacter chr) {
      membersLock.lock();
      try {
         if (members.size() >= capacity) {
            return 0;
         }
         for (int i = members.size() - 1; i >= 0; i--) {
            if (members.get(i).getGuildRank() < 5 || members.get(i).getName().compareTo(mgc.getName()) < 0) {
               mgc.setCharacter(chr);
               members.add(i + 1, mgc);
               bDirty = true;
               break;
            }
         }

         this.broadcast(MaplePacketCreator.newGuildMember(mgc));
         return 1;
      } finally {
         membersLock.unlock();
      }
   }

   public void leaveGuild(MapleGuildCharacter mgc) {
      membersLock.lock();
      try {
         this.broadcast(MaplePacketCreator.memberLeft(mgc, false));
         members.remove(mgc);
         bDirty = true;
      } finally {
         membersLock.unlock();
      }
   }

   public void expelMember(MapleGuildCharacter initiator, String name, int cid) {
      membersLock.lock();
      try {
         java.util.Iterator<MapleGuildCharacter> itr = members.iterator();
         while (itr.hasNext()) {
            MapleGuildCharacter mgc = itr.next();
            if (mgc.getId() == cid && initiator.getGuildRank() < mgc.getGuildRank()) {
               this.broadcast(MaplePacketCreator.memberLeft(mgc, true));
               itr.remove();
               bDirty = true;
               try {
                  if (mgc.isOnline()) {
                     Server.getInstance().getWorld(mgc.getWorld()).setGuildAndRank(cid, 0, 5);
                  } else {
                     DatabaseConnection.getInstance().withConnection(
                           connection -> NoteAdministrator.getInstance().sendNote(connection, mgc.getName(), initiator.getName(), "You have been expelled from the guild.", Byte.valueOf("0")));
                     Server.getInstance().getWorld(mgc.getWorld()).setOfflineGuildStatus((short) 0, (byte) 5, cid);
                  }
               } catch (Exception re) {
                  re.printStackTrace();
                  return;
               }
               return;
            }
         }
         System.out.println("Unable to find member with name " + name + " and id " + cid);
      } finally {
         membersLock.unlock();
      }
   }

   public void changeRank(int cid, int newRank) {
      membersLock.lock();
      try {
         for (MapleGuildCharacter mgc : members) {
            if (cid == mgc.getId()) {
               changeRank(mgc, newRank);
               return;
            }
         }
      } finally {
         membersLock.unlock();
      }
   }

   public void changeRank(MapleGuildCharacter mgc, int newRank) {
      try {
         if (mgc.isOnline()) {
            Server.getInstance().getWorld(mgc.getWorld()).setGuildAndRank(mgc.getId(), this.id, newRank);
            mgc.setGuildRank(newRank);
         } else {
            Server.getInstance().getWorld(mgc.getWorld()).setOfflineGuildStatus((short) this.id, (byte) newRank, mgc.getId());
            mgc.setOfflineGuildRank(newRank);
         }
      } catch (Exception re) {
         re.printStackTrace();
         return;
      }

      membersLock.lock();
      try {
         this.broadcast(MaplePacketCreator.changeRank(mgc));
      } finally {
         membersLock.unlock();
      }
   }

   public void setGuildNotice(String notice) {
      this.notice = notice;
      writeToDB(false);

      membersLock.lock();
      try {
         this.broadcast(MaplePacketCreator.guildNotice(this.id, notice));
      } finally {
         membersLock.unlock();
      }
   }

   public void memberLevelJobUpdate(MapleGuildCharacter mgc) {
      membersLock.lock();
      try {
         for (MapleGuildCharacter member : members) {
            if (mgc.equals(member)) {
               member.setJobId(mgc.getJobId());
               member.setLevel(mgc.getLevel());
               this.broadcast(MaplePacketCreator.guildMemberLevelJobUpdate(mgc));
               break;
            }
         }
      } finally {
         membersLock.unlock();
      }
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

      membersLock.lock();
      try {
         this.broadcast(MaplePacketCreator.rankTitleChange(this.id, ranks));
      } finally {
         membersLock.unlock();
      }

      this.writeToDB(false);
   }

   public void disbandGuild() {
      if (allianceId > 0) {
         if (!MapleAllianceProcessor.getInstance().removeGuildFromAlliance(allianceId, id, world)) {
            MapleAllianceProcessor.getInstance().disbandAlliance(allianceId);
         }
      }

      membersLock.lock();
      try {
         this.writeToDB(true);
         this.broadcast(null, -1, BCOp.DISBAND);
      } finally {
         membersLock.unlock();
      }
   }

   public void setGuildEmblem(short bg, byte bgcolor, short logo, byte logocolor) {
      this.logoBG = bg;
      this.logoBGColor = bgcolor;
      this.logo = logo;
      this.logoColor = logocolor;
      this.writeToDB(false);

      membersLock.lock();
      try {
         this.broadcast(null, -1, BCOp.EMBLEMCHANGE);
      } finally {
         membersLock.unlock();
      }
   }

   public MapleGuildCharacter getMGC(int cid) {
      membersLock.lock();
      try {
         for (MapleGuildCharacter mgc : members) {
            if (mgc.getId() == cid) {
               return mgc;
            }
         }
         return null;
      } finally {
         membersLock.unlock();
      }
   }

   public boolean increaseCapacity() {
      if (capacity > 99) {
         return false;
      }
      capacity += 5;
      this.writeToDB(false);

      membersLock.lock();
      try {
         this.broadcast(MaplePacketCreator.guildCapacityChange(this.id, this.capacity));
      } finally {
         membersLock.unlock();
      }

      return true;
   }

   public void gainGP(int amount) {
      this.gp += amount;
      this.writeToDB(false);
      this.guildMessage(MaplePacketCreator.updateGP(this.id, this.gp));
      this.guildMessage(MaplePacketCreator.getGPMessage(amount));
   }

   public void removeGP(int amount) {
      this.gp -= amount;
      this.writeToDB(false);
      this.guildMessage(MaplePacketCreator.updateGP(this.id, this.gp));
   }

   public int getAllianceId() {
      return allianceId;
   }

   public void setAllianceId(int aid) {
      this.allianceId = aid;
      DatabaseConnection.getInstance().withConnection(connection -> GuildAdministrator.getInstance().setAlliance(connection, id, aid));
   }

   public void resetAllianceGuildPlayersRank() {
      membersLock.lock();
      try {
         for (MapleGuildCharacter mgc : members) {
            if (mgc.isOnline()) {
               mgc.setAllianceRank(5);
            }
         }
      } finally {
         membersLock.unlock();
      }
      DatabaseConnection.getInstance().withConnection(connection -> CharacterAdministrator.getInstance().updateAllianceRank(connection, id, 5));
   }

   private enum BCOp {
      NONE, DISBAND, EMBLEMCHANGE
   }
}
