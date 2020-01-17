package server.partyquest;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import client.MapleCharacter;
import config.YamlConfig;
import constants.string.LanguageConstants;
import net.server.Server;
import net.server.channel.Channel;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import server.TimerManager;
import server.maps.MapleMap;
import server.maps.MapleReactor;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.field.effect.PlaySound;
import tools.packet.field.effect.ShowEffect;
import tools.packet.ui.GetClock;

public class MonsterCarnival {

   public static int D = 3;
   public static int C = 2;
   public static int B = 1;
   public static int A = 0;

   private MapleParty p1, p2;
   private MapleMap map;
   private ScheduledFuture<?> timer, effectTimer, respawnTask;
   private long startTime = 0;
   private int summonsR = 0, summonsB = 0, room = 0;
   private MapleCharacter leader1, leader2, team1, team2;
   private int redCP, blueCP, redTotalCP, blueTotalCP, redTimeUpCP, blueTimeUpCP;
   private boolean cpq1;

   public MonsterCarnival(MapleParty p1, MapleParty p2, int mapId, boolean cpq1, int room) {
      try {
         this.cpq1 = cpq1;
         this.room = room;
         this.p1 = p1;
         this.p2 = p2;
         Channel cs = Server.getInstance().getWorld(p2.getLeader().getWorld()).getChannel(p2.getLeader().getChannel());
         p1.setEnemy(p2);
         p2.setEnemy(p1);
         map = cs.getMapFactory().getDisposableMap(mapId);
         startTime = System.currentTimeMillis() + 10 * 60 * 1000;
         final int redPortal = map.isPurpleCPQMap() ? 2 : 0;
         final int bluePortal = map.isPurpleCPQMap() ? 1 : 0;

         p1.getMembers().stream()
               .map(MaplePartyCharacter::getPlayer)
               .flatMap(Optional::stream)
               .forEach(character -> {
                  character.setMonsterCarnival(this);
                  character.setTeam(0);
                  character.setFestivalPoints(0);
                  character.forceChangeMap(map, map.getPortal(redPortal));
                  MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("CPQ_ENTRY"));
                  if (p1.getLeader().getId() == character.getId()) {
                     leader1 = character;
                  }
                  team1 = character;
               });
         p2.getMembers().stream()
               .map(MaplePartyCharacter::getPlayer)
               .flatMap(Optional::stream)
               .forEach(character -> {
                  character.setMonsterCarnival(this);
                  character.setTeam(1);
                  character.setFestivalPoints(0);
                  character.forceChangeMap(map, map.getPortal(bluePortal));
                  MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("CPQ_ENTRY"));
                  if (p2.getLeader().getId() == character.getId()) {
                     leader2 = character;
                  }
                  team2 = character;
               });

         if (team1 == null || team2 == null) {
            for (MaplePartyCharacter mpc : p1.getMembers()) {
               mpc.getPlayer().ifPresent(chr -> MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("CPQ_ERROR")));
            }
            for (MaplePartyCharacter mpc : p2.getMembers()) {
               mpc.getPlayer().ifPresent(chr -> MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("CPQ_ERROR")));
            }
            return;
         }

         timer = TimerManager.getInstance().schedule(this::timeUp, map.getTimeDefault() * 1000);
         effectTimer = TimerManager.getInstance().schedule(this::complete, map.getTimeDefault() * 1000 - 10 * 1000);
         respawnTask = TimerManager.getInstance().register(this::respawn, YamlConfig.config.server.RESPAWN_INTERVAL);

         cs.initMonsterCarnival(cpq1, room);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void respawn() {
      map.respawn();
   }

   public void playerDisconnected(int characterId) {
      int team = -1;
      for (MaplePartyCharacter mpc : leader1.getParty().map(MapleParty::getMembers).orElse(Collections.emptyList())) {
         if (mpc.getId() == characterId) {
            team = 0;
            break;
         }
      }
      for (MaplePartyCharacter mpc : leader2.getParty().map(MapleParty::getMembers).orElse(Collections.emptyList())) {
         if (mpc.getId() == characterId) {
            team = 1;
            break;
         }
      }
      for (MapleCharacter chrMap : map.getAllPlayers()) {
         if (team == -1) {
            team = 1;
         }
         String teamS = "";
         switch (team) {
            case 0:
               teamS = LanguageConstants.getMessage(chrMap, LanguageConstants.CPQRed);
               break;
            case 1:
               teamS = LanguageConstants.getMessage(chrMap, LanguageConstants.CPQBlue);
               break;
         }
         MessageBroadcaster.getInstance().sendServerNotice(chrMap, ServerNoticeType.PINK_TEXT, I18nMessage.from("CPQ_PLAYER_EXIT").with(teamS));
      }
      earlyFinish();
   }

   private void earlyFinish() {
      dispose(true);
   }

   public void leftParty(int characterId) {
      playerDisconnected(characterId);
   }

   protected void dispose() {
      dispose(false);
   }

   public boolean canSummonR() {
      return summonsR < map.getMaxMobs();
   }

   public void summonR() {
      summonsR++;
   }

   public boolean canSummonB() {
      return summonsB < map.getMaxMobs();
   }

   public void summonB() {
      summonsB++;
   }

   public boolean canGuardianR() {
      int teamReactors = 0;
      for (MapleReactor react : map.getAllReactors()) {
         if (react.getName().substring(0, 1).contentEquals("0")) {
            teamReactors += 1;
         }
      }

      return teamReactors < map.getMaxReactors();
   }

   public boolean canGuardianB() {
      int teamReactors = 0;
      for (MapleReactor react : map.getAllReactors()) {
         if (react.getName().substring(0, 1).contentEquals("1")) {
            teamReactors += 1;
         }
      }

      return teamReactors < map.getMaxReactors();
   }


   protected void dispose(boolean warpOut) {
      Channel cs = map.getChannelServer();
      MapleMap out;
      if (!cpq1) { // cpq2
         out = cs.getMapFactory().getMap(980030010);
      } else {
         out = cs.getMapFactory().getMap(980000010);
      }

      disposeParty(leader1, warpOut, out);
      disposeParty(leader2, warpOut, out);

      if (this.timer != null) {
         this.timer.cancel(true);
         this.timer = null;
      }
      if (this.effectTimer != null) {
         this.effectTimer.cancel(true);
         this.effectTimer = null;
      }
      if (this.respawnTask != null) {
         this.respawnTask.cancel(true);
         this.respawnTask = null;
      }
      redTotalCP = 0;
      blueTotalCP = 0;
      leader1.getParty().ifPresent(party -> party.setEnemy(null));
      leader2.getParty().ifPresent(party -> party.setEnemy(null));
      map.dispose();
      map = null;

      cs.finishMonsterCarnival(cpq1, room);
   }

   private void disposeParty(MapleCharacter partyLeader, boolean warpOut, MapleMap out) {
      partyLeader.getParty()
            .map(MapleParty::getMembers).orElse(Collections.emptyList()).stream()
            .map(MaplePartyCharacter::getPlayer)
            .flatMap(Optional::stream)
            .forEach(character -> {
               character.resetCP();
               character.setTeam(-1);
               character.setMonsterCarnival(null);
               if (warpOut) {
                  character.changeMap(out, out.getPortal(0));
               }
            });
   }

   public void exit() {
      dispose();
   }

   public ScheduledFuture<?> getTimer() {
      return this.timer;
   }

   private void finishForParty(MapleCharacter leader, int totalCP, int cpq1MapOffset, int mapOffset) {
      Channel cs = map.getChannelServer();

      leader.getParty()
            .map(MapleParty::getMembers).orElse(Collections.emptyList()).stream()
            .map(MaplePartyCharacter::getPlayer)
            .flatMap(Optional::stream)
            .forEach(character -> {
               character.gainFestivalPoints(totalCP);
               character.setMonsterCarnival(null);
               if (cpq1) {
                  character.changeMap(cs.getMapFactory().getMap(map.getId() + cpq1MapOffset), cs.getMapFactory().getMap(map.getId() + cpq1MapOffset).getPortal(0));
               } else {
                  character.changeMap(cs.getMapFactory().getMap(map.getId() + mapOffset), cs.getMapFactory().getMap(map.getId() + mapOffset).getPortal(0));
               }
               character.setTeam(-1);
               character.dispelAbnormalStatuses();
            });
   }

   private void finish(int winningTeam) {
      try {
         if (winningTeam == 0) {
            finishForParty(leader1, this.redTotalCP, 2, 200);
            finishForParty(leader2, this.blueTotalCP, 3, 300);
         } else if (winningTeam == 1) {
            finishForParty(leader2, this.blueTotalCP, 2, 200);
            finishForParty(leader1, this.redTotalCP, 3, 300);
         }
         dispose();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void timeUp() {
      int cp1 = this.redTimeUpCP;
      int cp2 = this.blueTimeUpCP;
      if (cp1 == cp2) {
         extendTime();
         return;
      }
      if (cp1 > cp2) {
         finish(0);
      } else {
         finish(1);
      }
   }

   public long getTimeLeft() {
      return (startTime - System.currentTimeMillis());
   }

   public int getTimeLeftSeconds() {
      return (int) (getTimeLeft() / 1000);
   }

   private void extendTime() {
      for (MapleCharacter chrMap : map.getAllPlayers()) {
         MessageBroadcaster.getInstance().sendServerNotice(chrMap, ServerNoticeType.PINK_TEXT, I18nMessage.from("CPQ_EXTEND_TIME"));
      }
      startTime = System.currentTimeMillis() + 3 * 60 * 1000;

      MasterBroadcaster.getInstance().sendToAllInMap(map, new GetClock(3 * 60));

      timer = TimerManager.getInstance().schedule(this::timeUp, map.getTimeExpand() * 1000);
      effectTimer = TimerManager.getInstance().schedule(this::complete, map.getTimeExpand() * 1000 - 10 * 1000);
   }

   private void completeForParty(Channel channel, MapleCharacter leader, boolean win) {
      leader.getParty()
            .map(MapleParty::getMembers).orElse(Collections.emptyList()).stream()
            .map(MaplePartyCharacter::getPlayer)
            .flatMap(Optional::stream)
            .forEach(character -> {
               if (win) {
                  PacketCreator.announce(character, new ShowEffect("quest/carnival/win"));
                  PacketCreator.announce(character, new PlaySound("MobCarnival/Win"));
               } else {
                  PacketCreator.announce(character, new ShowEffect("quest/carnival/lose"));
                  PacketCreator.announce(character, new PlaySound("MobCarnival/Lose"));
               }
               character.dispelAbnormalStatuses();
            });
   }

   public void complete() {
      int cp1 = this.redTotalCP;
      int cp2 = this.blueTotalCP;

      this.redTimeUpCP = cp1;
      this.blueTimeUpCP = cp2;

      if (cp1 == cp2) {
         return;
      }
      boolean redWin = cp1 > cp2;
      int leader1Channel = leader1.getClient().getChannel();
      int leader2Channel = leader2.getClient().getChannel();
      if (leader1Channel != leader2Channel) {
         throw new RuntimeException("The leader channels are different.");
      }

      Channel cs = map.getChannelServer();
      map.killAllMonsters();

      completeForParty(cs, leader1, redWin);
      completeForParty(cs, leader2, !redWin);
   }

   public MapleParty getRed() {
      return p1;
   }

   public void setRed(MapleParty p1) {
      this.p1 = p1;
   }

   public MapleParty getBlue() {
      return p2;
   }

   public void setBlue(MapleParty p2) {
      this.p2 = p2;
   }

   public MapleCharacter getLeader1() {
      return leader1;
   }

   public void setLeader1(MapleCharacter leader1) {
      this.leader1 = leader1;
   }

   public MapleCharacter getLeader2() {
      return leader2;
   }

   public void setLeader2(MapleCharacter leader2) {
      this.leader2 = leader2;
   }

   public MapleCharacter getEnemyLeader(int team) {
      switch (team) {
         case 0:
            return leader2;
         case 1:
            return leader1;
      }
      return null;
   }

   public int getBlueCP() {
      return blueCP;
   }

   public void setBlueCP(int blueCP) {
      this.blueCP = blueCP;
   }

   public int getBlueTotalCP() {
      return blueTotalCP;
   }

   public void setBlueTotalCP(int blueTotalCP) {
      this.blueTotalCP = blueTotalCP;
   }

   public int getRedCP() {
      return redCP;
   }

   public void setRedCP(int redCP) {
      this.redCP = redCP;
   }

   public int getRedTotalCP() {
      return redTotalCP;
   }

   public void setRedTotalCP(int redTotalCP) {
      this.redTotalCP = redTotalCP;
   }

   public int getTotalCP(int team) {
      if (team == 0) {
         return redTotalCP;
      } else if (team == 1) {
         return blueTotalCP;
      } else {
         throw new RuntimeException("Equipe desconhecida");
      }
   }

   public void setTotalCP(int totalCP, int team) {
      if (team == 0) {
         this.redTotalCP = totalCP;
      } else if (team == 1) {
         this.blueTotalCP = totalCP;
      }
   }

   public int getCP(int team) {
      if (team == 0) {
         return redCP;
      } else if (team == 1) {
         return blueCP;
      } else {
         throw new RuntimeException("Equipe desconhecida" + team);
      }
   }

   public void setCP(int CP, int team) {
      if (team == 0) {
         this.redCP = CP;
      } else if (team == 1) {
         this.blueCP = CP;
      }
   }

   public int getRoom() {
      return this.room;
   }

   public MapleMap getEventMap() {
      return this.map;
   }
}
