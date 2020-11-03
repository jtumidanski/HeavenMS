package com.ms.engine.rest.builders;

import com.ms.engine.rest.InfoTextAttributes;

import builder.AttributeResultBuilder;
import builder.Builder;

public class InfoTextAttributesBuilder extends Builder<InfoTextAttributes, InfoTextAttributesBuilder>
      implements AttributeResultBuilder {
   @Override
   public InfoTextAttributes construct() {
      return new InfoTextAttributes();
   }

   @Override
   public InfoTextAttributesBuilder getThis() {
      return this;
   }

   public InfoTextAttributesBuilder setText(String text) {
      return add(attr -> attr.setText(text));
   }
}
