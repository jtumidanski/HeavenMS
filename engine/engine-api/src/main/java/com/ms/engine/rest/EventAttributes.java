package com.ms.engine.rest;

import rest.AttributeResult;

public class EventAttributes implements AttributeResult {
   private String name;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
