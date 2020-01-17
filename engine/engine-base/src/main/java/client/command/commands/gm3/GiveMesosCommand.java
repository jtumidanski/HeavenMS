package client.command.commands.gm3;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class GiveMesosCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("GIVE_MESOS_COMMAND_SYNTAX"));
         return;
      }

      String recv_, value_;
      long mesos_ = 0;

      if (params.length == 2) {
         recv_ = params[0];
         value_ = params[1];
      } else {
         recv_ = c.getPlayer().getName();
         value_ = params[0];
      }

      try {
         mesos_ = Long.parseLong(value_);
         if (mesos_ > Integer.MAX_VALUE) {
            mesos_ = Integer.MAX_VALUE;
         } else if (mesos_ < Integer.MIN_VALUE) {
            mesos_ = Integer.MIN_VALUE;
         }
      } catch (NumberFormatException nfe) {
         if (value_.contentEquals("max")) {
            mesos_ = Integer.MAX_VALUE;
         } else if (value_.contentEquals("min")) {
            mesos_ = Integer.MIN_VALUE;
         }
      }

      Optional<MapleCharacter> victim = c.getWorldServer().getPlayerStorage().getCharacterByName(recv_);
      if (victim.isPresent()) {
         victim.get().gainMeso((int) mesos_, true);
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("GIVE_MESOS_COMMAND_SUCCESS"));
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("PLAYER_NOT_FOUND").with(recv_));
      }
   }
}
