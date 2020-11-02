package rest.builders;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.InfoPlayerInteractionAttributes;

public class InfoPlayerInteractionAttributesBuilder
      extends Builder<InfoPlayerInteractionAttributes, InfoPlayerInteractionAttributesBuilder> implements AttributeResultBuilder {
   @Override
   public InfoPlayerInteractionAttributes construct() {
      return new InfoPlayerInteractionAttributes();
   }

   @Override
   public InfoPlayerInteractionAttributesBuilder getThis() {
      return this;
   }

   public InfoPlayerInteractionAttributesBuilder setPath(String path) {
      return add(attr -> attr.setPath(path));
   }
}
