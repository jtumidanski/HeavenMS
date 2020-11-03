package com.ms.engine.rest.builders;

import com.ms.engine.rest.IntroAttributes;

import builder.AttributeResultBuilder;
import builder.Builder;

public class IntroAttributesBuilder extends Builder<IntroAttributes, IntroAttributesBuilder> implements AttributeResultBuilder {
   @Override
   public IntroAttributes construct() {
      return new IntroAttributes();
   }

   @Override
   public IntroAttributesBuilder getThis() {
      return this;
   }

   public IntroAttributesBuilder setPath(String path) {
      return add(attr -> attr.setPath(path));
   }
}
