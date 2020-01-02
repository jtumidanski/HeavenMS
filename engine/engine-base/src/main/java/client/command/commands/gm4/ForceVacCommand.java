package client.command.commands.gm4;

import java.util.Collections;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.processor.PetProcessor;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.packet.remove.RemoveItem;

public class ForceVacCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      List<MapleMapObject> items = player.getMap().getMapObjectsInRange(player.position(), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.ITEM));
      for (MapleMapObject item : items) {
         MapleMapItem mapItem = (MapleMapItem) item;

         mapItem.lockItem();
         try {
            if (mapItem.isPickedUp()) continue;

            if (mapItem.getMeso() > 0) {
               player.gainMeso(mapItem.getMeso(), true);
            } else if (player.applyConsumeOnPickup(mapItem.getItemId())) {
            } else if (mapItem.getItemId() == 4031865 || mapItem.getItemId() == 4031866) {
               // Add NX to account, show effect and make item disappear
               player.getCashShop().gainCash(1, mapItem.getItemId() == 4031865 ? 100 : 250);
            } else if (mapItem.getItem().id() >= 5000000 && mapItem.getItem().id() <= 5000100) {
               int petId = PetProcessor.getInstance().createPet(mapItem.getItem().id());
               if (petId == -1) {
                  continue;
               }
               MapleInventoryManipulator.addById(c, mapItem.getItem().id(), mapItem.getItem().quantity(), null, petId);
            } else if (MapleInventoryManipulator.addFromDrop(c, mapItem.getItem(), true)) {
               if (mapItem.getItemId() == 4031868) {
                  player.updateAriantScore();
               }
            }

            player.getMap().pickItemDrop(new RemoveItem(mapItem.objectId(), 2, player.getId()), mapItem);
         } finally {
            mapItem.unlockItem();
         }
      }
   }
}
