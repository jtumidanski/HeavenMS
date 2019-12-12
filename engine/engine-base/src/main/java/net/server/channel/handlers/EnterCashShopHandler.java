/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.processor.CashShopProcessor;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import server.CashShop;
import tools.PacketCreator;
import tools.packet.SetCashShop;
import tools.packet.cashshop.ShowCash;
import tools.packet.cashshop.operation.ShowCashInventory;
import tools.packet.cashshop.operation.ShowWishList;

/**
 * @author Flav
 */
public class EnterCashShopHandler extends AbstractShopSystem<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   protected boolean featureDisabled(MapleClient client) {
      return client.getPlayer().cannotEnterCashShop();
   }

   @Override
   protected boolean failsShopSpecificValidation(MapleClient client) {
      return client.getPlayer().getCashShop().isOpened();
   }

   @Override
   protected void openShop(MapleClient client) {
      MapleCharacter character = client.getPlayer();
      CashShop cashShop = character.getCashShop();
      PacketCreator.announce(client, new SetCashShop(client));
      PacketCreator.announce(client, new ShowCashInventory(client.getAccID(), cashShop.getInventory(), character.getStorage().getSlots(), client.getCharacterSlots()));
      CashShopProcessor.getInstance().showGifts(character, cashShop);
      PacketCreator.announce(client, new ShowWishList(cashShop.getWishList(), false));
      PacketCreator.announce(client, new ShowCash(character.getCashShop().getCash(1), character.getCashShop().getCash(2), character.getCashShop().getCash(4)));
      character.getCashShop().open(true);
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      genericHandle(client);
   }
}
