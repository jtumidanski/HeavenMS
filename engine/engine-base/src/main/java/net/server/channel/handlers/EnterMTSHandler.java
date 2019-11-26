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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.database.provider.MtsItemProvider;
import client.processor.action.BuybackProcessor;
import config.YamlConfig;
import net.server.Server;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import server.MTSItemInfo;
import server.maps.FieldLimit;
import server.maps.MapleMiniDungeonInfo;
import tools.DatabaseConnection;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.SetITC;
import tools.packet.mtsoperation.GetNotYetSoldMTSInventory;
import tools.packet.mtsoperation.MTSTransferInventory;
import tools.packet.mtsoperation.MTSWantedListingOver;
import tools.packet.mtsoperation.SendMTS;
import tools.packet.mtsoperation.ShowMTSCash;
import tools.packet.stat.EnableActions;
import tools.packet.ui.ShowBlockedUI;


public final class EnterMTSHandler extends AbstractShopSystem<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   protected boolean featureDisabled(MapleClient client) {
      return !YamlConfig.config.server.USE_MTS;
   }

   @Override
   protected boolean failsShopSpecificValidation(MapleClient client) {
      if (FieldLimit.CANNOTMIGRATE.check(client.getPlayer().getMap().getFieldLimit())) {
         MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, "You can't do it here in this map.");
         PacketCreator.announce(client, new EnableActions());
         return true;
      }

      if (!client.getPlayer().isAlive()) {
         PacketCreator.announce(client, new EnableActions());
         return true;
      }

      if (client.getPlayer().getLevel() < 10) {
         PacketCreator.announce(client, new ShowBlockedUI(5));
         PacketCreator.announce(client, new EnableActions());
         return true;
      }

      return false;
   }

   @Override
   protected void openShop(MapleClient client) {
      MapleCharacter character = client.getPlayer();
      PacketCreator.announce(client, new SetITC(client));
      character.getCashShop().open(true);// xD
      client.enableCSActions();
      PacketCreator.announce(client, new MTSWantedListingOver(0, 0));
      PacketCreator.announce(client, new ShowMTSCash(client.getPlayer().getCashShop().getCash(2), client.getPlayer().getCashShop().getCash(4)));

      DatabaseConnection.getInstance().withConnection(connection -> {
         List<MTSItemInfo> items = new ArrayList<>(MtsItemProvider.getInstance().getByTab(connection, 1, 16));
         long countForTab = MtsItemProvider.getInstance().countByTab(connection, 1);
         int pages = (int) Math.ceil(countForTab / 16);

         PacketCreator.announce(client, new SendMTS(items, 1, 0, 0, pages));
         PacketCreator.announce(client, new MTSTransferInventory(getTransfer(character.getId())));
         PacketCreator.announce(client, new GetNotYetSoldMTSInventory(getNotYetSold(character.getId())));
      });
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      if (!chr.isAlive() && YamlConfig.config.server.USE_BUYBACK_SYSTEM) {
         BuybackProcessor.processBuyback(client);
         PacketCreator.announce(client, new EnableActions());
      } else {
         genericHandle(client);
      }
   }

   private List<MTSItemInfo> getNotYetSold(int cid) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> MtsItemProvider.getInstance().getUnsoldItems(connection, cid)).orElseThrow();
   }

   private List<MTSItemInfo> getTransfer(int cid) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> MtsItemProvider.getInstance().getTransferItems(connection, cid)).orElse(Collections.emptyList());
   }
}