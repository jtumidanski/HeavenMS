package rest.builders;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.CharacterAttributes;

public class CharacterAttributesBuilder extends Builder<CharacterAttributes, CharacterAttributesBuilder> implements
      AttributeResultBuilder {
   @Override
   public CharacterAttributes construct() {
      return new CharacterAttributes();
   }

   @Override
   protected CharacterAttributesBuilder getThis() {
      return this;
   }

   public CharacterAttributesBuilder setName(String name) {
      return add(attr -> attr.setName(name));
   }

   public CharacterAttributesBuilder setAccountId(Integer accountId) {
      return add(attr -> attr.setAccountId(accountId));
   }

   public CharacterAttributesBuilder setMapId(Integer mapId) {
      return add(attr -> attr.setMapId(mapId));
   }

   public CharacterAttributesBuilder setJobId(Integer jobId) {
      return add(attr -> attr.setJobId(jobId));
   }

   public CharacterAttributesBuilder setLevel(Integer level) {
      return add(attr -> attr.setLevel(level));
   }

   public CharacterAttributesBuilder setExperience(Integer experience) {
      return add(attr -> attr.setExperience(experience));
   }

   public CharacterAttributesBuilder setFame(Integer fame) {
      return add(attr -> attr.setFame(fame));
   }

   public CharacterAttributesBuilder setMeso(Integer meso) {
      return add(attr -> attr.setMeso(meso));
   }

   public CharacterAttributesBuilder setGm(Boolean gm) {
      return add(attr -> attr.setGm(gm));
   }

   public CharacterAttributesBuilder setGender(Integer gender) {
      return add(attr -> attr.setGender(gender));
   }

   public CharacterAttributesBuilder setX(Integer x) {
      return add(attr -> attr.setX(x));
   }

   public CharacterAttributesBuilder setY(Integer y) {
      return add(attr -> attr.setY(y));
   }

   public CharacterAttributesBuilder setHp(Integer hp) {
      return add(attr -> attr.setHp(hp));
   }
}
