/*
    This file is part of the HeavenMS MapleStory Server, commands OdinMS-based
    Copyleft (L) 2016 - 2018 RonanLana

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/*
   @Author: Arthur L - Refactored command content into modules
*/
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
