package rest.builders;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.FreeSlotAttributes;

public class FreeSlotAttributesBuilder extends Builder<FreeSlotAttributes, FreeSlotAttributesBuilder>
      implements AttributeResultBuilder {
   @Override
   public FreeSlotAttributes construct() {
      return new FreeSlotAttributes();
   }

   @Override
   public FreeSlotAttributesBuilder getThis() {
      return this;
   }

   public FreeSlotAttributesBuilder setCount(Integer count) {
      return add(attr -> attr.setCount(count));
   }
}
