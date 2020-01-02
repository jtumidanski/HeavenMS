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
import tools.packet.message.YellowTip;

public class GmCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      String[] tips = {
            "Please only use @gm in emergencies or to report somebody.",
            "To report a bug or make a suggestion, use the forum.",
            "Please do not use @gm to ask if a GM is online.",
            "Do not ask if you can receive help, just state your issue.",
            "Do not say 'I have a bug to report', just state it.",
      };
      MapleCharacter player = c.getPlayer();
      if (params.length < 1 || params[0].length() < 3) { // #goodbye 'hi'
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Your message was too short. Please provide as much detail as possible.");
         return;
      }
      String message = player.getLastCommandMessage();
      Server.getInstance().broadcastGMMessage(c.getWorld(), PacketCreator.create(new YellowTip("[GM Message]:" + StringUtil.makeMapleReadable(player.getName()) + ": " + message)));
      MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), ServerNoticeType.POP_UP, MapleCharacter::isGM, message);
      FilePrinter.printError(FilePrinter.COMMAND_GM, StringUtil.makeMapleReadable(player.getName()) + ": " + message);
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Your message '" + message + "' was sent to GMs.");
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, tips[Randomizer.nextInt(tips.length)]);
   }
}
