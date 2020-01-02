package net.server.channel.handlers;

import client.MapleClient;
import client.autoban.AutoBanFactory;
import constants.inventory.ItemConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.RemoteGachaponPacket;
import net.server.channel.packet.reader.RemoteGachaponReader;
import scripting.npc.NPCScriptManager;

public final class RemoteGachaponHandler extends AbstractPacketHandler<RemoteGachaponPacket> {
   @Override
   public Class<RemoteGachaponReader> getReaderClass() {
      return RemoteGachaponReader.class;
   }

   @Override
   public void handlePacket(RemoteGachaponPacket packet, MapleClient client) {
      if (packet.ticket() != 5451000) {
         AutoBanFactory.GENERAL.alert(client.getPlayer(), " Tried to use RemoteGachaponHandler with item id: " + packet.ticket());
         client.disconnect(false, false);
         return;
      } else if (packet.gachaponId() < 0 || packet.gachaponId() > 11) {
         AutoBanFactory.GENERAL.alert(client.getPlayer(), " Tried to use RemoteGachaponHandler with mode: " + packet.gachaponId());
         client.disconnect(false, false);
         return;
      } else if (client.getPlayer().getInventory(ItemConstants.getInventoryType(packet.ticket())).countById(packet.ticket()) < 1) {
         AutoBanFactory.GENERAL.alert(client.getPlayer(), " Tried to use RemoteGachaponHandler without a ticket.");
         client.disconnect(false, false);
         return;
      }
      int npcId = 9100100;
      if (packet.gachaponId() != 8 && packet.gachaponId() != 9) {
         npcId += packet.gachaponId();
      } else {
         npcId = packet.gachaponId() == 8 ? 9100109 : 9100117;
      }
      NPCScriptManager.getInstance().start(client, npcId, "gachaponRemote", null);
   }
}
