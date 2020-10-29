package rest.builders;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.CanHoldAttributes;

public class CanHoldAttributesBuilder extends Builder<CanHoldAttributes, CanHoldAttributesBuilder>
      implements AttributeResultBuilder {
   @Override
   public CanHoldAttributes construct() {
      return new CanHoldAttributes();
   }

   @Override
   public CanHoldAttributesBuilder getThis() {
      return this;
   }

   public CanHoldAttributesBuilder setQuantity(Integer quantity) {
      return add(attr -> attr.setQuantity(quantity));
   }
}
