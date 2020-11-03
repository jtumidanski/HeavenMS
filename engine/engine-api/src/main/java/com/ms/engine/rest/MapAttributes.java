package com.ms.engine.rest;

import java.util.List;

import rest.AttributeResult;

public class MapAttributes implements AttributeResult {
   private List<Integer> charactersInMap;

   public List<Integer> getCharactersInMap() {
      return charactersInMap;
   }

   public void setCharactersInMap(List<Integer> charactersInMap) {
      this.charactersInMap = charactersInMap;
   }
}
