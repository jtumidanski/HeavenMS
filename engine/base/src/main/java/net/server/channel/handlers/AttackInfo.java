package net.server.channel.handlers;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttackInfo {
   private int numAttacked;
   private int numDamage;
   private int numAttackedAndDamage;
   private int skill;
   private int skillLevel;
   private int stance;
   private int direction;
   private int rangedDirection;
   private int charge;
   private int display;
   private Map<Integer, List<Integer>> allDamage;
   private boolean ranged, magic;
   private int speed = 4;
   private Point position = new Point();

   public int getSkill() {
      return skill;
   }

   public void setSkill(int skill) {
      this.skill = skill;
   }

   public Point getPosition() {
      return position;
   }

   public int getNumAttacked() {
      return numAttacked;
   }

   public void setNumAttacked(int numAttacked) {
      this.numAttacked = numAttacked;
   }

   public Map<Integer, List<Integer>> getAllDamage() {
      return allDamage;
   }

   public boolean isMagic() {
      return magic;
   }

   public void setMagic(boolean magic) {
      this.magic = magic;
   }

   public boolean isRanged() {
      return ranged;
   }

   public void setRanged(boolean ranged) {
      this.ranged = ranged;
   }

   public int getNumDamage() {
      return numDamage;
   }

   public void setNumDamage(int numDamage) {
      this.numDamage = numDamage;
   }

   public int getNumAttackedAndDamage() {
      return numAttackedAndDamage;
   }

   public void setNumAttackedAndDamage(int numAttackedAndDamage) {
      this.numAttackedAndDamage = numAttackedAndDamage;
   }

   public int getStance() {
      return stance;
   }

   public void setStance(int stance) {
      this.stance = stance;
   }

   public int getDirection() {
      return direction;
   }

   public void setDirection(int direction) {
      this.direction = direction;
   }

   public void setCharge(int charge) {
      this.charge = charge;
   }

   public int getDisplay() {
      return display;
   }

   public void setDisplay(int display) {
      this.display = display;
   }

   public int getSkillLevel() {
      return skillLevel;
   }

   public void setSkillLevel(int skillLevel) {
      this.skillLevel = skillLevel;
   }

   public void clearAllDamage() {
      allDamage = new HashMap<>();
   }

   public int getRangedDirection() {
      return rangedDirection;
   }

   public void setRangedDirection(int rangedDirection) {
      this.rangedDirection = rangedDirection;
   }

   public int getSpeed() {
      return speed;
   }

   public void setSpeed(int speed) {
      this.speed = speed;
   }

   public int getCharge() {
      return charge;
   }
}
