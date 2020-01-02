package net.server.channel.handlers;

import client.MapleClient;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.ItemMovePacket;
import net.server.channel.packet.reader.ItemMoveReader;
import tools.PacketCreator;
import tools.packet.stat.EnableActions;

public final class ItemMoveHandler extends AbstractPacketHandler<ItemMovePacket> {
   @Override
   public boolean successfulProcess(MapleClient client) {
      if (client.getPlayer().getAutoBanManager().getLastSpam(6) + 300 > currentServerTime()) {
         PacketCreator.announce(client, new EnableActions());
         return false;
      }
      return true;
   }

   @Override
   public Class<ItemMoveReader> getReaderClass() {
      return ItemMoveReader.class;
   }

   @Override
   public void handlePacket(ItemMovePacket packet, MapleClient client) {
      MapleInventoryType type = MapleInventoryType.getByType(packet.inventoryType());

      if (packet.source() < 0 && packet.action() > 0) {
         MapleInventoryManipulator.unequip(client, packet.source(), packet.action());
      } else if (packet.action() < 0) {
         MapleInventoryManipulator.equip(client, packet.source(), packet.action());
      } else if (packet.action() == 0) {
         MapleInventoryManipulator.drop(client, type, packet.source(), packet.quantity());
      } else {
         MapleInventoryManipulator.move(client, type, packet.source(), packet.action());
      }

      if (client.getPlayer().getMap().getHPDec() > 0) {
         client.getPlayer().resetHpDecreaseTask();
      }
      client.getPlayer().getAutoBanManager().spam(6);
   }
}