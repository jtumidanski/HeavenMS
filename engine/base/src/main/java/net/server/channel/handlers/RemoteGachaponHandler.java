/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.server.channel.handlers;

import client.MapleClient;
import client.autoban.AutobanFactory;
import constants.ItemConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.RemoteGachaponPacket;
import net.server.channel.packet.reader.RemoteGachaponReader;
import scripting.npc.NPCScriptManager;

/**
 * @author Generic
 */
public final class RemoteGachaponHandler extends AbstractPacketHandler<RemoteGachaponPacket, RemoteGachaponReader> {
   @Override
   public Class<RemoteGachaponReader> getReaderClass() {
      return RemoteGachaponReader.class;
   }

   @Override
   public void handlePacket(RemoteGachaponPacket packet, MapleClient client) {
      if (packet.ticket() != 5451000) {
         AutobanFactory.GENERAL.alert(client.getPlayer(), " Tried to use RemoteGachaponHandler with item id: " + packet.ticket());
         client.disconnect(false, false);
         return;
      } else if (packet.gachaponId() < 0 || packet.gachaponId() > 11) {
         AutobanFactory.GENERAL.alert(client.getPlayer(), " Tried to use RemoteGachaponHandler with mode: " + packet.gachaponId());
         client.disconnect(false, false);
         return;
      } else if (client.getPlayer().getInventory(ItemConstants.getInventoryType(packet.ticket())).countById(packet.ticket()) < 1) {
         AutobanFactory.GENERAL.alert(client.getPlayer(), " Tried to use RemoteGachaponHandler without a ticket.");
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
