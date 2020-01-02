package client.command.commands.gm0;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import client.MapleClient;
import client.command.Command;

public class TimeCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient client, String[] params) {
      DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
      dateFormat.setTimeZone(TimeZone.getDefault());
      client.getPlayer().yellowMessage("HeavenMS Server Time: " + dateFormat.format(new Date()));
   }
}
