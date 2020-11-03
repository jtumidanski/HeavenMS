package com.ms.engine.rest.builders;

import com.ms.engine.rest.CharacterStatisticsAttributes;

import builder.AttributeResultBuilder;
import builder.Builder;

public class CharacterStatisticsAttributesBuilder
      extends Builder<CharacterStatisticsAttributes, CharacterStatisticsAttributesBuilder> implements AttributeResultBuilder {
   @Override
   public CharacterStatisticsAttributes construct() {
      return new CharacterStatisticsAttributes();
   }

   @Override
   public CharacterStatisticsAttributesBuilder getThis() {
      return this;
   }

   public CharacterStatisticsAttributesBuilder setStrength(Integer strength) {
      return add(attr -> attr.setStrength(strength));
   }

   public CharacterStatisticsAttributesBuilder setDexterity(Integer dexterity) {
      return add(attr -> attr.setDexterity(dexterity));
   }

   public CharacterStatisticsAttributesBuilder setLuck(Integer luck) {
      return add(attr -> attr.setLuck(luck));
   }

   public CharacterStatisticsAttributesBuilder setIntelligence(Integer intelligence) {
      return add(attr -> attr.setIntelligence(intelligence));
   }

   public CharacterStatisticsAttributesBuilder setHp(Integer hp) {
      return add(attr -> attr.setHp(hp));
   }
}
