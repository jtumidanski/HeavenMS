package client.command.commands.gm0;

import client.MapleClient;
import client.command.Command;

public class EquipLvCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      c.getPlayer().showAllEquipFeatures();
   }
}
