package server;

import java.util.List;

import tools.Pair;

public class CardItemupStats {
   protected int itemCode, prob;
   protected boolean party;
   private List<Pair<Integer, Integer>> areas;

   public CardItemupStats(int code, int prob, List<Pair<Integer, Integer>> areas, boolean inParty) {
      this.itemCode = code;
      this.prob = prob;
      this.areas = areas;
      this.party = inParty;
   }

   public boolean isInArea(int mapid) {
      if (this.areas == null) {
         return true;
      }

      for (Pair<Integer, Integer> a : this.areas) {
         if (mapid >= a.left && mapid <= a.right) {
            return true;
         }
      }

      return false;
   }

   public int getItemCode() {
      return itemCode;
   }

   public int getProb() {
      return prob;
   }

   public boolean isParty() {
      return party;
   }
}