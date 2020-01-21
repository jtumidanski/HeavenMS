package tools;

import client.MapleCharacter;
import client.MapleClient;

public interface UserMessage {
   String evaluate();

   UserMessage to(MapleCharacter character);

   UserMessage to(MapleClient client);
}
