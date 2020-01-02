package client.command.commands.gm0;

import client.MapleClient;
import client.command.Command;

public class ChangeLanguageCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      if (params.length < 1) {
         c.getPlayer().yellowMessage("Syntax: !changel <0=ptb, 1=esp, 2=eng>");
         return;
      }
      c.setLanguage(Integer.parseInt(params[0]));
   }
}
