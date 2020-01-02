package client;

import java.util.ArrayList;
import java.util.List;

import constants.skills.Evan;
import server.MapleStatEffect;
import server.life.Element;

public class Skill {
   private int id;
   private List<MapleStatEffect> effects = new ArrayList<>();
   private Element element;
   private int animationTime;
   private int job;
   private boolean action;

   public Skill(int id) {
      this.id = id;
      this.job = id / 10000;
   }

   public int getId() {
      return id;
   }

   public MapleStatEffect getEffect(int level) {
      return effects.get(level - 1);
   }

   public int getMaxLevel() {
      return effects.size();
   }

   public boolean isFourthJob() {
      if (job == MapleJob.EVAN4.getId()) {
         return false;
      }
      if (id == Evan.MAGIC_MASTERY || id == Evan.FLAME_WHEEL || id == Evan.HEROS_WILL || id == Evan.DARK_FOG || id == Evan.SOUL_STONE) {
         return true;
      }
      return job % 10 == 2;
   }

   public Element getElement() {
      return element;
   }

   public void setElement(Element elem) {
      element = elem;
   }

   public int getAnimationTime() {
      return animationTime;
   }

   public void setAnimationTime(int time) {
      animationTime = time;
   }

   public void incAnimationTime(int time) {
      animationTime += time;
   }

   public boolean isBeginnerSkill() {
      return id % 10000000 < 10000;
   }

   public boolean getAction() {
      return action;
   }

   public void setAction(boolean act) {
      action = act;
   }

   public void addLevelEffect(MapleStatEffect effect) {
      effects.add(effect);
   }
}