package com.ms.engine.rest;

import rest.AttributeResult;

public class CharacterStatisticsAttributes implements AttributeResult {
   private Integer strength;

   private Integer dexterity;

   private Integer luck;

   private Integer intelligence;

   private Integer hp;

   public Integer getStrength() {
      return strength;
   }

   public void setStrength(Integer strength) {
      this.strength = strength;
   }

   public Integer getDexterity() {
      return dexterity;
   }

   public void setDexterity(Integer dexterity) {
      this.dexterity = dexterity;
   }

   public Integer getLuck() {
      return luck;
   }

   public void setLuck(Integer luck) {
      this.luck = luck;
   }

   public Integer getIntelligence() {
      return intelligence;
   }

   public void setIntelligence(Integer intelligence) {
      this.intelligence = intelligence;
   }

   public Integer getHp() {
      return hp;
   }

   public void setHp(Integer hp) {
      this.hp = hp;
   }
}
