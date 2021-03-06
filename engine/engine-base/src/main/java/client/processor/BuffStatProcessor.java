package client.processor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import client.MapleBuffStat;
import client.MapleBuffStatValueHolder;
import server.MapleStatEffect;
import tools.Pair;

public class BuffStatProcessor {
   private static BuffStatProcessor ourInstance = new BuffStatProcessor();

   public static BuffStatProcessor getInstance() {
      return ourInstance;
   }

   private BuffStatProcessor() {
   }


   public MapleStatEffect getEffectFromBuffSource(Map<MapleBuffStat, MapleBuffStatValueHolder> buffSource) {
      try {
         return buffSource.entrySet().iterator().next().getValue().effect;
      } catch (Exception e) {
         return null;
      }
   }

   public Map<MapleStatEffect, Integer> topologicalSortLeafStatCount(Map<MapleBuffStat, Stack<MapleStatEffect>> buffStack) {
      Map<MapleStatEffect, Integer> leafBuffCount = new LinkedHashMap<>();

      for (Map.Entry<MapleBuffStat, Stack<MapleStatEffect>> e : buffStack.entrySet()) {
         Stack<MapleStatEffect> mseStack = e.getValue();
         if (mseStack.isEmpty()) {
            continue;
         }

         MapleStatEffect mse = mseStack.peek();
         leafBuffCount.merge(mse, 1, Integer::sum);
      }

      return leafBuffCount;
   }

   public List<MapleStatEffect> topologicalSortRemoveLeafStats(Map<MapleStatEffect, Set<MapleBuffStat>> stackedBuffStats, Map<MapleBuffStat, Stack<MapleStatEffect>> buffStack, Map<MapleStatEffect, Integer> leafStatCount) {
      List<MapleStatEffect> clearedStatEffects = new LinkedList<>();
      Set<MapleBuffStat> clearedStats = new LinkedHashSet<>();

      for (Map.Entry<MapleStatEffect, Integer> e : leafStatCount.entrySet()) {
         MapleStatEffect mse = e.getKey();

         if (stackedBuffStats.get(mse).size() <= e.getValue()) {
            clearedStatEffects.add(mse);

            clearedStats.addAll(stackedBuffStats.get(mse));
         }
      }

      for (MapleBuffStat mbs : clearedStats) {
         MapleStatEffect mse = buffStack.get(mbs).pop();
         stackedBuffStats.get(mse).remove(mbs);
      }

      return clearedStatEffects;
   }

   public void topologicalSortRebaseLeafStats(Map<MapleStatEffect, Set<MapleBuffStat>> stackedBuffStats, Map<MapleBuffStat, Stack<MapleStatEffect>> buffStack) {
      for (Map.Entry<MapleBuffStat, Stack<MapleStatEffect>> e : buffStack.entrySet()) {
         Stack<MapleStatEffect> mseStack = e.getValue();

         if (!mseStack.isEmpty()) {
            MapleStatEffect mse = mseStack.pop();
            stackedBuffStats.get(mse).remove(e.getKey());
         }
      }
   }

   public List<MapleStatEffect> topologicalSortEffects(Map<MapleBuffStat, List<Pair<MapleStatEffect, Integer>>> buffEffects) {
      Map<MapleStatEffect, Set<MapleBuffStat>> stackedBuffStats = new LinkedHashMap<>();
      Map<MapleBuffStat, Stack<MapleStatEffect>> buffStack = new LinkedHashMap<>();

      for (Map.Entry<MapleBuffStat, List<Pair<MapleStatEffect, Integer>>> e : buffEffects.entrySet()) {
         MapleBuffStat mbs = e.getKey();

         Stack<MapleStatEffect> mbsStack = new Stack<>();
         buffStack.put(mbs, mbsStack);

         for (Pair<MapleStatEffect, Integer> emse : e.getValue()) {
            MapleStatEffect mse = emse.getLeft();
            mbsStack.push(mse);

            Set<MapleBuffStat> mbsStats = stackedBuffStats.computeIfAbsent(mse, k -> new LinkedHashSet<>());
            mbsStats.add(mbs);
         }
      }

      List<MapleStatEffect> buffList = new LinkedList<>();
      while (true) {
         Map<MapleStatEffect, Integer> leafStatCount = topologicalSortLeafStatCount(buffStack);
         if (leafStatCount.isEmpty()) {
            break;
         }

         List<MapleStatEffect> clearedNodes = topologicalSortRemoveLeafStats(stackedBuffStats, buffStack, leafStatCount);
         if (clearedNodes.isEmpty()) {
            topologicalSortRebaseLeafStats(stackedBuffStats, buffStack);
         } else {
            buffList.addAll(clearedNodes);
         }
      }

      return buffList;
   }

   public List<MapleStatEffect> sortEffectsList(Map<MapleStatEffect, Integer> updateEffectsList) {
      Map<MapleBuffStat, List<Pair<MapleStatEffect, Integer>>> buffEffects = new LinkedHashMap<>();

      for (Map.Entry<MapleStatEffect, Integer> p : updateEffectsList.entrySet()) {
         MapleStatEffect mse = p.getKey();

         for (Pair<MapleBuffStat, Integer> statup : mse.getStatups()) {
            MapleBuffStat stat = statup.getLeft();

            List<Pair<MapleStatEffect, Integer>> statBuffs = buffEffects.computeIfAbsent(stat, k -> new ArrayList<>());
            statBuffs.add(new Pair<>(mse, statup.getRight()));
         }
      }

      Comparator<Pair<MapleStatEffect, Integer>> cmp = (o1, o2) -> o2.getRight().compareTo(o1.getRight());

      for (Map.Entry<MapleBuffStat, List<Pair<MapleStatEffect, Integer>>> statBuffs : buffEffects.entrySet()) {
         statBuffs.getValue().sort(cmp);
      }

      return topologicalSortEffects(buffEffects);
   }

   public MapleBuffStat getSingletonStatupFromEffect(MapleStatEffect mse) {
      return mse.getStatups().stream()
            .filter(pair -> isSingletonStatup(pair.getLeft()))
            .findFirst()
            .map(Pair::getLeft)
            .orElse(null);
   }

   public boolean isSingletonStatup(MapleBuffStat mbs) {
      //HPREC and MPREC are supposed to be singleton
      return switch (mbs) {
         case COUPON_EXP1, COUPON_EXP2, COUPON_EXP3, COUPON_EXP4, COUPON_DRP1, COUPON_DRP2, COUPON_DRP3, MESO_UP_BY_ITEM, ITEM_UP_BY_ITEM, RESPECT_PLAYER_IMMUNE, RESPECT_MONSTER_IMMUNE, DEFENSE_ATT, DEFENSE_STATE, WEAPON_ATTACK, WEAPON_DEFENSE, MAGIC_ATTACK, MAGIC_DEFENSE, ACC, AVOID, SPEED, JUMP -> false;
         default -> true;
      };
   }

   public boolean isPriorityBuffSourceId(int sourceId) {
      return switch (sourceId) {
         case -2022631, -2022632, -2022633 -> true;
         default -> false;
      };
   }
}
