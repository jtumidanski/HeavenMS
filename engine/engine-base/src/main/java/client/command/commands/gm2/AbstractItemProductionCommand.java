package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import client.processor.PetProcessor;
import config.YamlConfig;
import constants.inventory.ItemConstants;
import server.MapleItemInformationProvider;

public abstract class AbstractItemProductionCommand extends Command {
   protected abstract String getSyntax();

   protected abstract void produceItem(MapleClient client, int itemId, short quantity);

   protected abstract void producePet(MapleClient client, int itemId, short quantity, int petId, long expiration);

   @Override
   public void execute(MapleClient client, String[] params) {
      MapleCharacter player = client.getPlayer();

      if (params.length < 1) {
         player.yellowMessage(getSyntax());
         return;
      }

      int itemId = Integer.parseInt(params[0]);
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

      if (ii.getName(itemId) == null) {
         player.yellowMessage("Item id '" + params[0] + "' does not exist.");
         return;
      }

      short quantity = 1;
      if (params.length >= 2) {
         quantity = Short.parseShort(params[1]);
      }

      if (YamlConfig.config.server.BLOCK_GENERATE_CASH_ITEM && ii.isCash(itemId)) {
         player.yellowMessage("You cannot create a cash item with this command.");
         return;
      }

      if (ItemConstants.isPet(itemId)) {
         if (params.length >= 2) {
            quantity = 1;
            long days = Math.max(1, Integer.parseInt(params[1]));
            long expiration = System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000);
            int petId = PetProcessor.getInstance().createPet(itemId);
            producePet(client, itemId, quantity, petId, expiration);
         } else {
            player.yellowMessage("Pet Syntax: !item <item id> <expiration>");
         }
         return;
      }

      produceItem(client, itemId, quantity);
   }
}
