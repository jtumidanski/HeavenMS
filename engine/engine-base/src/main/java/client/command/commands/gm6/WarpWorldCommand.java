package client.command.commands.gm6;

import java.net.InetAddress;
import java.net.UnknownHostException;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.ChangeChannel;

public class WarpWorldCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("WARP_WORLD_COMMAND_SYNTAX"));
         return;
      }

      Server server = Server.getInstance();
      byte worldDb = Byte.parseByte(params[0]);
      if (worldDb <= (server.getWorldsSize() - 1)) {
         try {
            String[] socket = server.getInetSocket(worldDb, c.getChannel());
            c.getWorldServer().removePlayer(player);
            player.getMap().removePlayer(player);//LOL FORGOT THIS ><
            player.setSessionTransitionState();
            player.setWorld(worldDb);
            player.saveCharToDB();//To set the new world :O (true because else 2 player instances are created, one in both worlds)
            PacketCreator.announce(c, new ChangeChannel(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1])));
         } catch (UnknownHostException | NumberFormatException ex) {
            ex.printStackTrace();
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("WARP_WORLD_COMMAND_CHANNEL_ERROR"));
         }
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("WARP_WORLD_COMMAND_INVALID_WORLD").with(server.getWorldsSize() - 1));
      }
   }
}
