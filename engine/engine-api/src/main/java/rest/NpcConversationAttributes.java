package rest;

public class NpcConversationAttributes implements AttributeResult {

   private NpcConversationType type;

   private String token;

   public NpcConversationType getType() {
      return type;
   }

   public void setType(NpcConversationType type) {
      this.type = type;
   }

   public String getToken() {
      return token;
   }

   public void setToken(String token) {
      this.token = token;
   }
}
