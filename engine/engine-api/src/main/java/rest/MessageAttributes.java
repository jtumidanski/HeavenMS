package rest;

import java.util.ArrayList;
import java.util.List;

public class MessageAttributes implements AttributeResult {
   private String type;

   private String token;

   private List<String> replacements;

   public MessageAttributes() {
      replacements = new ArrayList<>();
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getToken() {
      return token;
   }

   public void setToken(String token) {
      this.token = token;
   }

   public List<String> getReplacements() {
      return replacements;
   }

   public void setReplacements(List<String> replacements) {
      this.replacements = replacements;
   }
}
