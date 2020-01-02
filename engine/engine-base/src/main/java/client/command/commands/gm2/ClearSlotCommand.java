package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;

public class ClearSlotCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient client, String[] params) {
      MapleCharacter player = client.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !clearslot <all, equip, use, setup, etc or cash.>");
         return;
      }
      String type = params[0];
      switch (type) {
         case "all":
            clearSlotsForType(client, MapleInventoryType.EQUIP);
            clearSlotsForType(client, MapleInventoryType.USE);
            clearSlotsForType(client, MapleInventoryType.ETC);
            clearSlotsForType(client, MapleInventoryType.SETUP);
            clearSlotsForType(client, MapleInventoryType.CASH);
            player.yellowMessage("All Slots Cleared.");
            break;
         case "equip":
            clearSlotsForType(client, MapleInventoryType.EQUIP);
            player.yellowMessage("Equipment Slot Cleared.");
            break;
         case "use":
            clearSlotsForType(client, MapleInventoryType.USE);
            player.yellowMessage("Use Slot Cleared.");
            break;
         case "setup":
            clearSlotsForType(client, MapleInventoryType.SETUP);
            player.yellowMessage("Set-Up Slot Cleared.");
            break;
         case "etc":
            clearSlotsForType(client, MapleInventoryType.ETC);
            player.yellowMessage("ETC Slot Cleared.");
            break;
         case "cash":
            clearSlotsForType(client, MapleInventoryType.CASH);
            player.yellowMessage("Cash Slot Cleared.");
            break;
         default:
            player.yellowMessage("Slot" + type + " does not exist!");
            break;
      }
   }

   private void clearSlotsForType(MapleClient client, MapleInventoryType type) {
      for (int i = 0; i < 101; i++) {
         Item tempItem = client.getPlayer().getInventory(type).getItem((byte) i);
         if (tempItem == null)
            continue;
         MapleInventoryManipulator.removeFromSlot(client, type, (byte) i, tempItem.quantity(), false, false);
      }
   }
}
