package com.ms.engine.rest.builders;

import com.ms.engine.rest.QuestConsumableItemAttributes;

import builder.AttributeResultBuilder;
import builder.Builder;

public class QuestConsumableItemAttributesBuilder
      extends Builder<QuestConsumableItemAttributes, QuestConsumableItemAttributesBuilder> implements AttributeResultBuilder {
   @Override
   public QuestConsumableItemAttributes construct() {
      return new QuestConsumableItemAttributes();
   }

   @Override
   public QuestConsumableItemAttributesBuilder getThis() {
      return this;
   }

   public QuestConsumableItemAttributesBuilder setExperience(Integer experience) {
      return add(attr -> attr.setExperience(experience));
   }

   public QuestConsumableItemAttributesBuilder setGrade(Integer grade) {
      return add(attr -> attr.setGrade(grade));
   }
}
