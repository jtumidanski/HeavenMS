package rest.builders;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.NpcAttributes;

public class NpcAttributesBuilder extends Builder<NpcAttributes, NpcAttributesBuilder> implements AttributeResultBuilder {
   @Override
   public NpcAttributes construct() {
      return new NpcAttributes();
   }

   @Override
   public NpcAttributesBuilder getThis() {
      return this;
   }

   public NpcAttributesBuilder setX(Integer x) {
      return add(attr -> attr.setX(x));
   }

   public NpcAttributesBuilder setY(Integer y) {
      return add(attr -> attr.setY(y));
   }
}
