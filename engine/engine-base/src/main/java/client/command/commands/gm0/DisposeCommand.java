package client.command.commands.gm0;

import client.MapleClient;
import client.command.Command;
import scripting.npc.NPCScriptManager;
import server.processor.QuestProcessor;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.stat.EnableActions;

public class DisposeCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      NPCScriptManager.getInstance().dispose(c);
      QuestProcessor.getInstance().disposeScript(c.getPlayer().getId());
      PacketCreator.announce(c, new EnableActions());
      c.removeClickedNPC();
      MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("DISPOSE_COMMAND_SUCCESS"));
   }
}
