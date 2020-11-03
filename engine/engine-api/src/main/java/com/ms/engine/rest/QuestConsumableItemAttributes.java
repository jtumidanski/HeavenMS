package com.ms.engine.rest;

import rest.AttributeResult;

public class QuestConsumableItemAttributes implements AttributeResult {
   private Integer experience;

   private Integer grade;

   public Integer getExperience() {
      return experience;
   }

   public void setExperience(Integer experience) {
      this.experience = experience;
   }

   public Integer getGrade() {
      return grade;
   }

   public void setGrade(Integer grade) {
      this.grade = grade;
   }
}
