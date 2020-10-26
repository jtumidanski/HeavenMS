package net.server.channel.handlers;

import client.MapleClient;
import client.inventory.Item;
import constants.ItemConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.ScriptedItemPacket;
import net.server.channel.packet.reader.ScriptedItemReader;
import scripting.item.ItemScriptManager;
import server.MapleItemInformationProvider;
import server.ScriptedItem;

public final class ScriptedItemHandler extends AbstractPacketHandler<ScriptedItemPacket> {
   @Override
   public Class<ScriptedItemReader> getReaderClass() {
      return ScriptedItemReader.class;
   }

   @Override
   public void handlePacket(ScriptedItemPacket packet, MapleClient client) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      ScriptedItem info = ii.getScriptedItemInfo(packet.itemId());
      if (info == null) {
         return;
      }

      Item item = client.getPlayer().getInventory(ItemConstants.getInventoryType(packet.itemId())).getItem(packet.itemSlot());
      if (item == null || item.id() != packet.itemId() || item.quantity() < 1) {
         return;
      }

      ItemScriptManager ism = ItemScriptManager.getInstance();
      ism.runItemScript(client, info);
   }
}
