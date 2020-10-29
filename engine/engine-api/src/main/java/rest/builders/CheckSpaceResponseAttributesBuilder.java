package rest.builders;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.CheckSpaceResponseAttributes;

public class CheckSpaceResponseAttributesBuilder extends Builder<CheckSpaceResponseAttributes, CheckSpaceResponseAttributesBuilder>
      implements AttributeResultBuilder {
   @Override
   public CheckSpaceResponseAttributes construct() {
      return new CheckSpaceResponseAttributes();
   }

   @Override
   public CheckSpaceResponseAttributesBuilder getThis() {
      return this;
   }

   public CheckSpaceResponseAttributesBuilder setResult(Integer result) {
      return add(attr -> attr.setResult(result));
   }
}
