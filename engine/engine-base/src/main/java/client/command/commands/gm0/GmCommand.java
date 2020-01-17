package client.command.commands.gm0;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import tools.FilePrinter;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Randomizer;
import tools.ServerNoticeType;
import tools.StringUtil;
import tools.I18nMessage;
import tools.packet.message.YellowTip;

public class GmCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1 || params[0].length() < 3) { // #goodbye 'hi'
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("GM_COMMAND_MESSAGE_LENGTH_ERROR"));
         return;
      }
      String message = player.getLastCommandMessage();
      Server.getInstance().broadcastGMMessage(c.getWorld(), PacketCreator.create(new YellowTip("[GM Message]:" + StringUtil.makeMapleReadable(player.getName()) + ": " + message)));
      MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), ServerNoticeType.POP_UP, MapleCharacter::isGM, message);
      FilePrinter.printError(FilePrinter.COMMAND_GM, StringUtil.makeMapleReadable(player.getName()) + ": " + message);
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("GM_COMMAND_MESSAGE_LOOPBACK").with(message));
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("GM_COMMAND_TIP_" + Randomizer.nextInt(5)));
   }
}
