package com.ms.engine.rest;

import rest.AttributeResult;

public class InfoTextAttributes implements AttributeResult {
   private String text;

   public String getText() {
      return text;
   }

   public void setText(String text) {
      this.text = text;
   }
}
