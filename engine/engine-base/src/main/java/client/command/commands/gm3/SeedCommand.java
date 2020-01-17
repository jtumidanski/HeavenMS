package client.command.commands.gm3;

import java.awt.Point;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import client.inventory.Item;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class SeedCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (player.getMapId() != 910010000) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("SEED_COMMAND_FAILURE"));
         return;
      }
      Point[] pos = {new Point(7, -207), new Point(179, -447), new Point(-3, -687), new Point(-357, -687), new Point(-538, -447), new Point(-359, -207)};
      int[] seed = {4001097, 4001096, 4001095, 4001100, 4001099, 4001098};
      for (int i = 0; i < pos.length; i++) {
         Item item = new Item(seed[i], (byte) 0, (short) 1);
         player.getMap().spawnItemDrop(player, player, item, pos[i], false, true);
         try {
            Thread.sleep(100);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }
}
