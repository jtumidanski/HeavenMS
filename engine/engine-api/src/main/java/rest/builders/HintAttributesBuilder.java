package rest.builders;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.HintAttributes;

public class HintAttributesBuilder extends Builder<HintAttributes, HintAttributesBuilder> implements AttributeResultBuilder {
   @Override
   public HintAttributes construct() {
      return new HintAttributes();
   }

   @Override
   public HintAttributesBuilder getThis() {
      return this;
   }

   public HintAttributesBuilder setMessage(String message) {
      return add(attr -> attr.setMessage(message));
   }

   public HintAttributesBuilder setWidth(Integer width) {
      return add(attr -> attr.setWidth(width));
   }

   public HintAttributesBuilder setHeight(Integer height) {
      return add(attr -> attr.setHeight(height));
   }
}
