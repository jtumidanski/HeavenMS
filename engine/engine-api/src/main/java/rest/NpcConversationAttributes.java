package rest;

import java.util.List;

public class NpcConversationAttributes implements AttributeResult {

   private NpcConversationType type;

   private Byte speaker;

   private String token;

   private List<String> arguments;

   private Integer def;

   private Integer min;

   private Integer max;

   public NpcConversationType getType() {
      return type;
   }

   public void setType(NpcConversationType type) {
      this.type = type;
   }

   public Byte getSpeaker() {
      return speaker;
   }

   public void setSpeaker(Byte speaker) {
      this.speaker = speaker;
   }

   public String getToken() {
      return token;
   }

   public void setToken(String token) {
      this.token = token;
   }

   public List<String> getArguments() {
      return arguments;
   }

   public void setArguments(List<String> arguments) {
      this.arguments = arguments;
   }

   public Integer getDef() {
      return def;
   }

   public void setDef(Integer def) {
      this.def = def;
   }

   public Integer getMin() {
      return min;
   }

   public void setMin(Integer min) {
      this.min = min;
   }

   public Integer getMax() {
      return max;
   }

   public void setMax(Integer max) {
      this.max = max;
   }
}
