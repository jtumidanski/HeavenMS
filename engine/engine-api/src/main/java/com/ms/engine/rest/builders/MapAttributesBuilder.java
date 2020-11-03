package com.ms.engine.rest.builders;

import java.util.List;

import com.ms.engine.rest.MapAttributes;

import builder.AttributeResultBuilder;
import builder.Builder;

public class MapAttributesBuilder extends Builder<MapAttributes, MapAttributesBuilder> implements AttributeResultBuilder {
   @Override
   public MapAttributes construct() {
      return new MapAttributes();
   }

   @Override
   public MapAttributesBuilder getThis() {
      return this;
   }

   public MapAttributesBuilder setCharactersInMap(List<Integer> charactersInMap) {
      return add(attr -> attr.setCharactersInMap(charactersInMap));
   }
}
