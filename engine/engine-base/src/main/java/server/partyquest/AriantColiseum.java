package server.partyquest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import client.MapleCharacter;
import constants.game.GameConstants;
import server.TimerManager;
import server.expeditions.MapleExpedition;
import server.expeditions.MapleExpeditionType;
import server.maps.MapleMap;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.packet.pq.ariant.AriantScore;
import tools.packet.pq.ariant.ShowAriantScoreboard;
import tools.packet.pq.ariant.UpdateAriantRanking;

public class AriantColiseum {

   private MapleExpedition expedition;
   private MapleMap map;

   private Map<MapleCharacter, Integer> score;
   private Map<MapleCharacter, Integer> rewardTier;
   private boolean scoreDirty = false;

   private ScheduledFuture<?> ariantUpdate;
   private ScheduledFuture<?> ariantFinish;
   private ScheduledFuture<?> ariantScoreboard;

   private int lostShards = 0;

   private boolean eventClear = false;

   public AriantColiseum(MapleMap eventMap, MapleExpedition expedition) {
      this.expedition = expedition;
      this.expedition.finishRegistration();

      map = eventMap;
      map.resetFully();

      int pqTimer = 10 * 60 * 1000;
      int pqTimerBoard = (9 * 60 * 1000) + 50 * 1000;

      List<MapleCharacter> players = this.expedition.getActiveMembers();
      score = new HashMap<>();
      rewardTier = new HashMap<>();
      for (MapleCharacter mc : players) {
         mc.changeMap(map, 0);
         mc.setAriantColiseum(this);
         mc.updateAriantScore();
         rewardTier.put(mc, 0);
      }

      List<AriantScore> scores = score.entrySet().stream().map(e -> new AriantScore(e.getKey().getName(), e.getValue())).collect(Collectors.toList());
      for (MapleCharacter mc : players) {
         PacketCreator.announce(mc, new UpdateAriantRanking(scores));
      }

      setAriantScoreBoard(TimerManager.getInstance().schedule(this::showArenaResults, pqTimerBoard));

      setArenaFinish(TimerManager.getInstance().schedule(this::enterKingsRoom, pqTimer));

      setArenaUpdate(TimerManager.getInstance().register(this::broadcastAriantScoreUpdate, 500, 500));
   }

   private static boolean isUnfairMatch(Integer winnerScore, Integer secondScore, Integer lostShardsScore, List<Integer> runnersUpScore) {
      if (winnerScore <= 0) {
         return false;
      }

      double runnersUpScoreCount = 0;
      for (Integer i : runnersUpScore) {
         runnersUpScoreCount += i;
      }

      runnersUpScoreCount += lostShardsScore;
      secondScore += lostShardsScore;

      double matchRes = runnersUpScoreCount / winnerScore;
      double runnersUp = ((double) secondScore) / winnerScore;

      return matchRes < 0.81770726891980117713114871015349 && (runnersUpScoreCount < 7 || runnersUp < 0.5929);
   }

   private void setArenaUpdate(ScheduledFuture<?> ariantUpdate) {
      this.ariantUpdate = ariantUpdate;
   }

   private void setArenaFinish(ScheduledFuture<?> arenaFinish) {
      this.ariantFinish = arenaFinish;
   }

   private void setAriantScoreBoard(ScheduledFuture<?> ariantScore) {
      this.ariantScoreboard = ariantScore;
   }

   private void cancelArenaUpdate() {
      if (ariantUpdate != null) {
         ariantUpdate.cancel(true);
         ariantUpdate = null;
      }
   }

   private void cancelArenaFinish() {
      if (ariantFinish != null) {
         ariantFinish.cancel(true);
         ariantFinish = null;
      }
   }

   private void cancelAriantScoreBoard() {
      if (ariantScoreboard != null) {
         ariantScoreboard.cancel(true);
         ariantScoreboard = null;
      }
   }

   private void cancelAriantSchedules() {
      cancelArenaUpdate();
      cancelArenaFinish();
      cancelAriantScoreBoard();
   }

   public int getAriantScore(MapleCharacter chr) {
      Integer chrScore = score.get(chr);
      return chrScore != null ? chrScore : 0;
   }

   public void clearAriantScore(MapleCharacter chr) {
      score.remove(chr);
   }

   public void updateAriantScore(MapleCharacter chr, int points) {
      if (map != null) {
         score.put(chr, points);
         scoreDirty = true;
      }
   }

   private void broadcastAriantScoreUpdate() {
      if (scoreDirty) {
         List<AriantScore> scores = score.entrySet().stream().map(e -> new AriantScore(e.getKey().getName(), e.getValue())).collect(Collectors.toList());
         for (MapleCharacter chr : score.keySet()) {
            PacketCreator.announce(chr, new UpdateAriantRanking(scores));
         }
         scoreDirty = false;
      }
   }

   public int getAriantRewardTier(MapleCharacter chr) {
      Integer reward = rewardTier.get(chr);
      return reward != null ? reward : 0;
   }

   public void clearAriantRewardTier(MapleCharacter chr) {
      rewardTier.remove(chr);
   }

   public void addLostShards(int quantity) {
      lostShards += quantity;
   }

   public void leaveArena(MapleCharacter chr) {
      if (!(eventClear && GameConstants.isAriantColiseumArena(chr.getMapId()))) {
         leaveArenaInternal(chr);
      }
   }

   private synchronized void leaveArenaInternal(MapleCharacter chr) {
      if (expedition != null) {
         if (expedition.removeMember(chr)) {
            int minSize = eventClear ? 1 : 2;
            if (expedition.getActiveMembers().size() < minSize) {
               dispose();
            }
            chr.setAriantColiseum(null);

            int shards = chr.countItem(4031868);
            chr.getAbstractPlayerInteraction().removeAll(4031868);
            chr.updateAriantScore(shards);
         }
      }
   }

   public void playerDisconnected(MapleCharacter chr) {
      leaveArenaInternal(chr);
   }

   private void showArenaResults() {
      eventClear = true;

      if (map != null) {
         MasterBroadcaster.getInstance().sendToAllInMap(map, new ShowAriantScoreboard());
         map.killAllMonsters();

         distributeAriantPoints();
      }
   }

   public void distributeAriantPoints() {
      int firstTop = -1, secondTop = -1;
      MapleCharacter winner = null;
      List<Integer> runnersUp = new ArrayList<>();

      for (Entry<MapleCharacter, Integer> e : score.entrySet()) {
         Integer s = e.getValue();
         if (s > firstTop) {
            secondTop = firstTop;
            firstTop = s;
            winner = e.getKey();
         } else if (s > secondTop) {
            secondTop = s;
         }

         runnersUp.add(s);
         rewardTier.put(e.getKey(), (int) Math.floor(s / 10));
      }

      runnersUp.remove(firstTop);
      if (isUnfairMatch(firstTop, secondTop, map.getDroppedItemsCountById(4031868) + lostShards, runnersUp)) {
         rewardTier.put(winner, 1);
      }
   }

   private MapleExpeditionType getExpeditionType() {
      MapleExpeditionType type;
      if (map.getId() == 980010101) {
         type = MapleExpeditionType.ARIANT;
      } else if (map.getId() == 980010201) {
         type = MapleExpeditionType.ARIANT1;
      } else {
         type = MapleExpeditionType.ARIANT2;
      }

      return type;
   }

   private void enterKingsRoom() {
      expedition.removeChannelExpedition(map.getChannelServer());
      cancelAriantSchedules();

      for (MapleCharacter chr : map.getAllPlayers()) {
         chr.changeMap(980010010, 0);
      }
   }

   private synchronized void dispose() {
      if (expedition != null) {
         expedition.dispose(false);

         for (MapleCharacter chr : expedition.getActiveMembers()) {
            chr.setAriantColiseum(null);
            chr.changeMap(980010000, 0);
         }

         map.getWorldServer().registerTimedMapObject(() -> {
            score.clear();
            expedition = null;
            map = null;
         }, 5 * 60 * 1000);
      }
   }
}
