package tools;

import client.MapleCharacter;
import client.MapleClient;

public class SimpleMessage implements UserMessage {
   private final String message;

   protected SimpleMessage(String message) {
      this.message = message;
   }

   public static SimpleMessage from(String message) {
      return new SimpleMessage(message);
   }

   @Override
   public String evaluate() {
      return message;
   }

   @Override
   public UserMessage to(MapleCharacter character) {
      return this;
   }

   @Override
   public UserMessage to(MapleClient client) {
      return this;
   }
}
