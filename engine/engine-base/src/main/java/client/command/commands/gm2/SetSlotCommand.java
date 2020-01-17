package client.command.commands.gm2;

import client.*;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class SetSlotCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("SET_SLOT_COMMAND_SYNTAX"));
         return;
      }

      int slots = (Integer.parseInt(params[0]) / 4) * 4;
      for (int i = 1; i < 5; i++) {
         int curSlots = player.getSlots(i);
         if (slots <= -curSlots) {
            continue;
         }

         player.gainSlots(i, slots - curSlots, true);
      }

      MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("SET_SLOT_COMMAND_SUCCESS"));
   }
}
