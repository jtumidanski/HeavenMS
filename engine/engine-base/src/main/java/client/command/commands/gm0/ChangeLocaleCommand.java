package client.command.commands.gm0;

import java.util.Locale;

import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class ChangeLocaleCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient client, String[] params) {
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(client.getPlayer(), I18nMessage.from("CHANGE_LOCALE_COMMAND_SYNTAX"));
         return;
      }
      client.setLocale(new Locale(params[0], params[1]));
   }
}
