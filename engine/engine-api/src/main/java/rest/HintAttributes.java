package rest;

import rest.AttributeResult;

public class HintAttributes implements AttributeResult {
   private String message;

   private Integer width;

   private Integer height;

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public Integer getWidth() {
      return width;
   }

   public void setWidth(Integer width) {
      this.width = width;
   }

   public Integer getHeight() {
      return height;
   }

   public void setHeight(Integer height) {
      this.height = height;
   }
}
