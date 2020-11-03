package com.ms.engine.rest.builders;

import com.ms.engine.rest.TitleAttributes;

import builder.AttributeResultBuilder;
import builder.Builder;

public class TitleAttributesBuilder extends Builder<TitleAttributes, TitleAttributesBuilder> implements AttributeResultBuilder {
   @Override
   public TitleAttributes construct() {
      return new TitleAttributes();
   }

   @Override
   public TitleAttributesBuilder getThis() {
      return this;
   }

   public TitleAttributesBuilder setName(String name) {
      return add(attr -> attr.setName(name));
   }
}
