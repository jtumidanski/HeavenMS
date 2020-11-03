package com.ms.engine.rest.builders;

import com.ms.engine.rest.EventAttributes;

import builder.AttributeResultBuilder;
import builder.Builder;

public class EventAttributesBuilder extends Builder<EventAttributes, EventAttributesBuilder> implements AttributeResultBuilder {
   @Override
   public EventAttributes construct() {
      return new EventAttributes();
   }

   @Override
   public EventAttributesBuilder getThis() {
      return this;
   }

   public EventAttributesBuilder setName(String name) {
      return add(attr -> attr.setName(name));
   }
}
