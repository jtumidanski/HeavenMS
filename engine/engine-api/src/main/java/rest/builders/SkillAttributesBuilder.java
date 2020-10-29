package rest.builders;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.SkillAttributes;

public class SkillAttributesBuilder extends Builder<SkillAttributes, SkillAttributesBuilder> implements AttributeResultBuilder {
   @Override
   public SkillAttributes construct() {
      return new SkillAttributes();
   }

   @Override
   public SkillAttributesBuilder getThis() {
      return this;
   }

   public SkillAttributesBuilder setBeginnerSkill(Boolean beginnerSkill) {
      return add(attr -> attr.setBeginnerSkill(beginnerSkill));
   }
}
