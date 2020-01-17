package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.BetterItemFactory;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.processor.ItemProcessor;
import constants.inventory.ItemConstants;
import server.MapleItemInformationProvider;

public class ItemDropCommand extends AbstractItemProductionCommand {
   {
      setDescription("");
   }

   @Override
   protected String getSyntax() {
      return "ITEM_DROP_PRODUCTION_COMMAND_SYNTAX";
   }

   @Override
   protected void producePet(MapleClient client, int itemId, short quantity, int petId, long expiration) {
      MapleCharacter player = client.getPlayer();
      Item toDrop = BetterItemFactory.getInstance().create(itemId, (short) 0, quantity, petId);
      toDrop.expiration_(expiration);

      toDrop.owner_$eq("");
      setFlag(player, toDrop);
      client.getPlayer().getMap().spawnItemDrop(client.getPlayer(), client.getPlayer(), toDrop, client.getPlayer().position(), true, true);
   }

   private void setFlag(MapleCharacter player, Item toDrop) {
      if (player.gmLevel() < 3) {
         short f = toDrop.flag();
         f |= ItemConstants.ACCOUNT_SHARING;
         f |= ItemConstants.UNTRADEABLE;
         f |= ItemConstants.SANDBOX;

         ItemProcessor.getInstance().setFlag(toDrop, f);
         toDrop.owner_$eq("TRIAL-MODE");
      }
   }

   @Override
   public void produceItem(MapleClient client, int itemId, short quantity) {
      MapleCharacter player = client.getPlayer();
      Item toDrop;
      if (ItemConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
         toDrop = MapleItemInformationProvider.getInstance().getEquipById(itemId);
      } else {
         toDrop = new Item(itemId, (short) 0, quantity);
      }

      toDrop.owner_$eq(player.getName());
      setFlag(player, toDrop);
      player.getMap().spawnItemDrop(player, player, toDrop, player.position(), true, true);
   }
}
