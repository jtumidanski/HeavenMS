package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import scripting.portal.PortalScriptManager;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class ReloadPortalsCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      PortalScriptManager.getInstance().reloadPortalScripts();
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Reloaded Portals");
   }
}
