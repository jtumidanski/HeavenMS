package com.ms.engine.rest.builders;

import com.ms.engine.rest.PortalAttributes;

import builder.AttributeResultBuilder;
import builder.Builder;

public class PortalAttributesBuilder extends Builder<PortalAttributes, PortalAttributesBuilder> implements AttributeResultBuilder {
   @Override
   public PortalAttributes construct() {
      return new PortalAttributes();
   }

   @Override
   public PortalAttributesBuilder getThis() {
      return this;
   }

   public PortalAttributesBuilder setName(String name) {
      return add(attr -> attr.setName(name));
   }
}
