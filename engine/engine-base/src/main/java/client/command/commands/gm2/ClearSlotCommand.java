package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import client.inventory.Item;
import client.inventory.manipulator.MapleInventoryManipulator;
import constants.MapleInventoryType;
import tools.I18nMessage;
import tools.MessageBroadcaster;

public class ClearSlotCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient client, String[] params) {
      MapleCharacter player = client.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("CLEAR_SLOT_COMMAND_SYNTAX"));
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
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("CLEAR_SLOT_COMMAND_ALL_SUCCESS"));
            break;
         case "equip":
            clearSlotsForType(client, MapleInventoryType.EQUIP);
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("CLEAR_SLOT_COMMAND_EQUIP_SUCCESS"));
            break;
         case "use":
            clearSlotsForType(client, MapleInventoryType.USE);
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("CLEAR_SLOT_COMMAND_USE_SUCCESS"));
            break;
         case "setup":
            clearSlotsForType(client, MapleInventoryType.SETUP);
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("CLEAR_SLOT_COMMAND_SET_UP_SUCCESS"));
            break;
         case "etc":
            clearSlotsForType(client, MapleInventoryType.ETC);
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("CLEAR_SLOT_COMMAND_ETC_SUCCESS"));
            break;
         case "cash":
            clearSlotsForType(client, MapleInventoryType.CASH);
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("CLEAR_SLOT_COMMAND_CASH_SUCCESS"));
            break;
         default:
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("CLEAR_SLOT_COMMAND_ERROR"));
            break;
      }
   }

   private void clearSlotsForType(MapleClient client, MapleInventoryType type) {
      for (int i = 0; i < 101; i++) {
         Item tempItem = client.getPlayer().getInventory(type).getItem((byte) i);
         if (tempItem == null) {
            continue;
         }
         MapleInventoryManipulator.removeFromSlot(client, type, (byte) i, tempItem.quantity(), false, false);
      }
   }
}
