package rest.builders;

import java.util.Map;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.MonsterBookAttributes;

public class MonsterBookAttributesBuilder extends Builder<MonsterBookAttributes, MonsterBookAttributesBuilder>
      implements AttributeResultBuilder {
   @Override
   public MonsterBookAttributes construct() {
      return new MonsterBookAttributes();
   }

   @Override
   public MonsterBookAttributesBuilder getThis() {
      return this;
   }

   public MonsterBookAttributesBuilder setLevel(int level) {
      return add(attr -> attr.setLevel(level));
   }

   public MonsterBookAttributesBuilder setCards(Map<Integer, Integer> cards) {
      return add(attr -> attr.setCards(cards));
   }
}
