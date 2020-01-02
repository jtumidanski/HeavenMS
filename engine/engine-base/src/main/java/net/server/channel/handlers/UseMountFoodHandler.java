package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleMount;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import constants.game.ExpTable;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.UseMountFoodPacket;
import net.server.channel.packet.reader.UseMountFoodReader;
import tools.MasterBroadcaster;
import tools.packet.character.UpdateMount;

public final class UseMountFoodHandler extends AbstractPacketHandler<UseMountFoodPacket> {
   @Override
   public Class<UseMountFoodReader> getReaderClass() {
      return UseMountFoodReader.class;
   }

   @Override
   public void handlePacket(UseMountFoodPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      MapleMount mount = chr.getMount();
      MapleInventory useInv = chr.getInventory(MapleInventoryType.USE);

      if (client.tryAcquireClient()) {
         try {
            Boolean mountLevelUp = null;

            useInv.lockInventory();
            try {
               Item item = useInv.getItem(packet.position());
               if (item != null && item.id() == packet.itemId() && mount != null) {
                  int curTiredness = mount.tiredness();
                  int healedTiredness = Math.min(curTiredness, 30);

                  float healedFactor = (float) healedTiredness / 30;
                  mount.tiredness_$eq(curTiredness - healedTiredness);

                  if (healedFactor > 0.0f) {
                     mount.exp_$eq(mount.exp() + (int) Math.ceil(healedFactor * (2 * mount.level() + 6)));
                     int level = mount.level();
                     boolean levelUp = mount.exp() >= ExpTable.getMountExpNeededForLevel(level) && level < 31;
                     if (levelUp) {
                        mount.level_$eq(level + 1);
                     }

                     mountLevelUp = levelUp;
                  }

                  MapleInventoryManipulator.removeById(client, MapleInventoryType.USE, packet.itemId(), 1, true, false);
               }
            } finally {
               useInv.unlockInventory();
            }

            if (mountLevelUp != null) {
               MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new UpdateMount(chr.getId(), mount.level(), mount.exp(), mount.tiredness(), mountLevelUp));
            }
         } finally {
            client.releaseClient();
         }
      }
   }
}