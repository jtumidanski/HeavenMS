package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.ItemBuilder;
import client.inventory.MaplePet;
import client.processor.PetProcessor;
import constants.ItemConstants;
import constants.MapleInventoryType;
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
      MaplePet pet = PetProcessor.getInstance().loadFromDb(itemId, (short) 0, petId);
      Item toDrop = Item.newBuilder(itemId)
            .setPosition((short) 0)
            .setQuantity(quantity)
            .setPet(pet)
            .setPetId(petId)
            .setExpiration(expiration)
            .setOwner("")
            .build();
      toDrop = setFlag(player, toDrop);
      client.getPlayer().getMap()
            .spawnItemDrop(client.getPlayer(), client.getPlayer(), toDrop, client.getPlayer().position(), true, true);
   }

   private Item setFlag(MapleCharacter player, Item toDrop) {
      ItemBuilder builder = Item.newBuilder(toDrop);

      if (player.gmLevel() < 3) {
         short f = toDrop.flag();
         f |= ItemConstants.ACCOUNT_SHARING;
         f |= ItemConstants.UNTRADEABLE;
         f |= ItemConstants.SANDBOX;
         builder.setFlag(f).setOwner("TRIAL-MODE");
      }
      return builder.build();
   }

   @Override
   public void produceItem(MapleClient client, int itemId, short quantity) {
      MapleCharacter player = client.getPlayer();
      Item toDrop;
      if (ItemConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
         toDrop = MapleItemInformationProvider.getInstance().getEquipById(itemId);
      } else {
         toDrop = Item.newBuilder(itemId).setPosition((short) 0).setQuantity(quantity).build();
      }

      toDrop = Item.newBuilder(toDrop).setOwner(player.getName()).build();
      toDrop = setFlag(player, toDrop);
      player.getMap().spawnItemDrop(player, player, toDrop, player.position(), true, true);
   }
}
