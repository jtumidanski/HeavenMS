package com.ms.engine.rest.builders;

import com.ms.engine.rest.MountAttributes;

import builder.AttributeResultBuilder;
import builder.Builder;

public class MountAttributesBuilder extends Builder<MountAttributes, MountAttributesBuilder> implements AttributeResultBuilder {
   @Override
   public MountAttributes construct() {
      return new MountAttributes();
   }

   @Override
   public MountAttributesBuilder getThis() {
      return this;
   }

   public MountAttributesBuilder setLevel(Integer level) {
      return add(attr -> attr.setLevel(level));
   }
}
