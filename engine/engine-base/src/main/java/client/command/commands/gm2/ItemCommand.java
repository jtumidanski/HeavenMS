package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.manipulator.MapleInventoryManipulator;
import constants.ItemConstants;

public class ItemCommand extends AbstractItemProductionCommand {
   {
      setDescription("");
   }

   @Override
   protected String getSyntax() {
      return "ITEM_PRODUCTION_COMMAND_SYNTAX";
   }

   @Override
   protected void producePet(MapleClient client, int itemId, short quantity, int petId, long expiration) {
      MapleCharacter player = client.getPlayer();
      MapleInventoryManipulator.addById(client, itemId, quantity, player.getName(), petId, expiration);
   }

   @Override
   public void produceItem(MapleClient client, int itemId, short quantity) {
      MapleCharacter player = client.getPlayer();
      short flag = 0;
      if (player.gmLevel() < 3) {
         flag |= ItemConstants.ACCOUNT_SHARING;
         flag |= ItemConstants.UNTRADEABLE;
      }

      MapleInventoryManipulator.addById(client, itemId, quantity, player.getName(), -1, flag, -1);
   }
}
