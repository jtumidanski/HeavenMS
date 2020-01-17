package client.command.commands.gm3;

import java.util.Objects;

import client.AbstractMapleCharacterObject;
import client.MapleClient;
import client.command.Command;

public class HealMapCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient client, String[] params) {
      client.getPlayer().getMap().getCharacters().stream()
            .filter(Objects::nonNull)
            .forEach(AbstractMapleCharacterObject::healHpMp);
   }
}
