package rest.builders;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.PetAttributes;

public class PetAttributesBuilder extends Builder<PetAttributes, PetAttributesBuilder> implements AttributeResultBuilder {
   @Override
   public PetAttributes construct() {
      return new PetAttributes();
   }

   @Override
   public PetAttributesBuilder getThis() {
      return this;
   }

   public PetAttributesBuilder setSlot(short slot) {
      return add(attr -> attr.setSlot(slot));
   }

   public PetAttributesBuilder setFlag(short flag) {
      return add(attr -> attr.setFlag(flag));
   }

   public PetAttributesBuilder setPetFlag(Integer petFlag) {
      return add(attr -> attr.setPetFlag(petFlag));
   }

   public PetAttributesBuilder setCloseness(int closeness) {
      return add(attr -> attr.setCloseness(closeness));
   }

   public PetAttributesBuilder setFullness(Integer fullness) {
      return add(attr -> attr.setFullness(fullness));
   }
}
