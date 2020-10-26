package net.server.channel.handlers;

import client.MapleClient;
import client.inventory.Item;
import constants.MapleInventoryType;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.UseItemEffectPacket;
import net.server.channel.packet.reader.UseItemEffectReader;
import tools.MasterBroadcaster;
import tools.packet.foreigneffect.ShowItemEffect;

public final class UseItemEffectHandler extends AbstractPacketHandler<UseItemEffectPacket> {
   @Override
   public Class<UseItemEffectReader> getReaderClass() {
      return UseItemEffectReader.class;
   }

   @Override
   public void handlePacket(UseItemEffectPacket packet, MapleClient client) {
      Item toUse;
      int itemId = packet.itemId();
      if (itemId == 4290001 || itemId == 4290000) {
         toUse = client.getPlayer().getInventory(MapleInventoryType.ETC).findById(itemId);
      } else {
         toUse = client.getPlayer().getInventory(MapleInventoryType.CASH).findById(itemId);
      }
      if (toUse == null || toUse.quantity() < 1) {
         if (itemId != 0) {
            return;
         }
      }
      client.getPlayer().setItemEffect(itemId);
      MasterBroadcaster.getInstance()
            .sendToAllInMap(client.getPlayer().getMap(), new ShowItemEffect(client.getPlayer().getId(), itemId), false,
                  client.getPlayer());
   }
}
