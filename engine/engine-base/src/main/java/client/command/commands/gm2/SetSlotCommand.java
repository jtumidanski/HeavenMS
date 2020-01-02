package client.command.commands.gm2;

import client.*;
import client.command.Command;

public class SetSlotCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !setslot <new level>");
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

      player.yellowMessage("Slots updated.");
   }
}
