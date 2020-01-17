package client.command.commands.gm0;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class TimeCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient client, String[] params) {
      DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
      dateFormat.setTimeZone(TimeZone.getDefault());
      MessageBroadcaster.getInstance().yellowMessage(client.getPlayer(), I18nMessage.from("TIME_COMMAND").with(dateFormat.format(new Date())));
   }
}
