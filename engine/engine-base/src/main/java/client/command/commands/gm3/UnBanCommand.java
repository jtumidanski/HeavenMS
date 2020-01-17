package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import database.administrator.AccountAdministrator;
import database.provider.AccountProvider;
import database.administrator.IpBanAdministrator;
import database.administrator.MacBanAdministrator;
import database.DatabaseConnection;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class UnBanCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("UN_BAN_COMMAND_SYNTAX"));
         return;
      }

      DatabaseConnection.getInstance().withConnection(connection -> {
         int aid = AccountProvider.getInstance().getAccountIdForName(connection, params[0]);
         AccountAdministrator.getInstance().removePermanentBan(connection, aid);
         IpBanAdministrator.getInstance().removeIpBan(connection, aid);
         MacBanAdministrator.getInstance().removeMacBan(connection, aid);
      });
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("UN_BAN_COMMAND_SUCCESS").with(params[0]));
   }
}
