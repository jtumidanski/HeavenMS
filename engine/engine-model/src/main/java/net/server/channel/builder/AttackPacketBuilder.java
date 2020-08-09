package net.server.channel.builder;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.server.channel.packet.AttackPacket;

public class AttackPacketBuilder {
   private Integer numAttacked;

   private Integer numDamage;

   private Integer numAttackedAndDamage;

   private Integer skill;

   private Integer skillLevel;

   private Integer stance;

   private Integer direction;

   private Integer rangedDirection;

   private Integer charge;

   private Integer display;

   private Boolean ranged;

   private Boolean magic;

   private Integer speed;

   private Map<Integer, List<Integer>> allDamage;

   private Point position;

   public AttackPacketBuilder() {
      allDamage = new HashMap<>();
   }

   public AttackPacket build() {
      return new AttackPacket(numAttacked, numDamage, numAttackedAndDamage, skill, skillLevel, stance, direction, rangedDirection, charge, display, ranged, magic, speed, allDamage, position);
   }

   public AttackPacketBuilder setNumAttacked(Integer numAttacked) {
      this.numAttacked = numAttacked;
      return this;
   }

   public AttackPacketBuilder setNumDamage(Integer numDamage) {
      this.numDamage = numDamage;
      return this;
   }

   public AttackPacketBuilder setNumAttackedAndDamage(Integer numAttackedAndDamage) {
      this.numAttackedAndDamage = numAttackedAndDamage;
      return this;
   }

   public AttackPacketBuilder setSkill(Integer skill) {
      this.skill = skill;
      return this;
   }

   public AttackPacketBuilder setSkillLevel(Integer skillLevel) {
      this.skillLevel = skillLevel;
      return this;
   }

   public AttackPacketBuilder setStance(Integer stance) {
      this.stance = stance;
      return this;
   }

   public AttackPacketBuilder setDirection(Integer direction) {
      this.direction = direction;
      return this;
   }

   public AttackPacketBuilder setRangedDirection(Integer rangedDirection) {
      this.rangedDirection = rangedDirection;
      return this;
   }

   public AttackPacketBuilder setCharge(Integer charge) {
      this.charge = charge;
      return this;
   }

   public AttackPacketBuilder setDisplay(Integer display) {
      this.display = display;
      return this;
   }

   public AttackPacketBuilder setRanged(Boolean ranged) {
      this.ranged = ranged;
      return this;
   }

   public AttackPacketBuilder setMagic(Boolean magic) {
      this.magic = magic;
      return this;
   }

   public AttackPacketBuilder setSpeed(Integer speed) {
      this.speed = speed;
      return this;
   }

   public AttackPacketBuilder setAllDamage(Map<Integer, List<Integer>> allDamage) {
      this.allDamage = allDamage;
      return this;
   }

   public AttackPacketBuilder setPosition(Point position) {
      this.position = position;
      return this;
   }

   public AttackPacketBuilder setLocation(int x, int y) {
      Point point = new Point();
      point.setLocation(x, y);
      this.position = point;
      return this;
   }

   public AttackPacketBuilder addDamage(Integer key, List<Integer> damage) {
      allDamage.put(key, damage);
      return this;
   }
}
