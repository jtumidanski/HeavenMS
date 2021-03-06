package client.command.commands.gm0;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import net.server.Server;
import tools.I18nMessage;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.SimpleMessage;
import tools.StringUtil;
import tools.packet.message.YellowTip;

public class ReportBugCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();

      if (params.length < 1) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("REPORT_BUG_COMMAND_MESSAGE_TOO_SHORT"));
         return;
      }
      String message = player.getLastCommandMessage();
      Server.getInstance().broadcastGMMessage(c.getWorld(), PacketCreator.create(new YellowTip("[Bug]:" + StringUtil.makeMapleReadable(player.getName()) + ": " + message)));
      MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), ServerNoticeType.POP_UP, MapleCharacter::isGM, SimpleMessage.from(message));
      LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.COMMAND_BUG, StringUtil.makeMapleReadable(player.getName()) + ": " + message);
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("REPORT_BUG_COMMAND_MESSAGE_LOOPBACK").with(message));

   }
}
