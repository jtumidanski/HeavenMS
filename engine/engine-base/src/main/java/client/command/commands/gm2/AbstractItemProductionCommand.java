package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import client.processor.PetProcessor;
import config.YamlConfig;
import constants.ItemConstants;
import server.MapleItemInformationProvider;
import tools.I18nMessage;
import tools.MessageBroadcaster;

public abstract class AbstractItemProductionCommand extends Command {
   protected abstract String getSyntax();

   protected abstract void produceItem(MapleClient client, int itemId, short quantity);

   protected abstract void producePet(MapleClient client, int itemId, short quantity, int petId, long expiration);

   @Override
   public void execute(MapleClient client, String[] params) {
      MapleCharacter player = client.getPlayer();

      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from(getSyntax()));
         return;
      }

      int itemId = Integer.parseInt(params[0]);
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

      if (ii.getName(itemId) == null) {
         MessageBroadcaster.getInstance()
               .yellowMessage(player, I18nMessage.from("ITEM_PRODUCTION_COMMAND_ITEM_NOT_FOUND").with(params[0]));
         return;
      }

      short quantity = 1;
      if (params.length >= 2) {
         quantity = Short.parseShort(params[1]);
      }

      if (YamlConfig.config.server.BLOCK_GENERATE_CASH_ITEM && ii.isCash(itemId)) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("ITEM_PRODUCTION_COMMAND_CANNOT_MAKE_CASH_ITEM"));
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
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("ITEM_PRODUCTION_COMMAND_PET_SYNTAX"));
         }
         return;
      }

      produceItem(client, itemId, quantity);
   }
}
