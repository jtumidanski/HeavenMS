package rest;

import java.util.LinkedHashMap;
import java.util.Map;

public class MonsterBookAttributes implements AttributeResult {
   private int level;

   private Map<Integer, Integer> cards;

   public MonsterBookAttributes() {
      cards = new LinkedHashMap<>();
   }

   public int getLevel() {
      return level;
   }

   public void setLevel(int level) {
      this.level = level;
   }

   public Map<Integer, Integer> getCards() {
      return cards;
   }

   public void setCards(Map<Integer, Integer> cards) {
      this.cards = cards;
   }
}
