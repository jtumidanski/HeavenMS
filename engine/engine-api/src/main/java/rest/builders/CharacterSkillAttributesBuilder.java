package rest.builders;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.CharacterSkillAttributes;

public class CharacterSkillAttributesBuilder extends Builder<CharacterSkillAttributes, CharacterSkillAttributesBuilder> implements AttributeResultBuilder {
   @Override
   public CharacterSkillAttributes construct() {
      return new CharacterSkillAttributes();
   }

   @Override
   public CharacterSkillAttributesBuilder getThis() {
      return this;
   }

   public CharacterSkillAttributesBuilder setLevel(Integer level) {
      return add(attr -> attr.setLevel(level));
   }

   public CharacterSkillAttributesBuilder setMasterLevel(Integer masterLevel) {
      return add(attr -> attr.setMasterLevel(masterLevel));
   }
}
