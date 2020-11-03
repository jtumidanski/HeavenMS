package rest.builders;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.SoundAttributes;

public class SoundAttributesBuilder extends Builder<SoundAttributes, SoundAttributesBuilder> implements AttributeResultBuilder {
   @Override
   public SoundAttributes construct() {
      return new SoundAttributes();
   }

   @Override
   public SoundAttributesBuilder getThis() {
      return this;
   }

   public SoundAttributesBuilder setPath(String path) {
      return add(attr -> attr.setPath(path));
   }
}
