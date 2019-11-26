/*
    This file is part of the HeavenMS MapleStory Server, commands OdinMS-based
    Copyleft (L) 2016 - 2018 RonanLana

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

/*
   @Author: Arthur L - Refactored command content into modules
*/
package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.processor.PetProcessor;
import config.YamlConfig;
import constants.inventory.ItemConstants;
import server.MapleItemInformationProvider;

public class ItemCommand extends AbstractItemProductionCommand {
   {
      setDescription("");
   }

   @Override
   protected String getSyntax() {
      return "Syntax: !item <itemid> <quantity>";
   }

   @Override
   protected void producePet(MapleClient client, int itemId, short quantity, int petId, long expiration) {
      MapleCharacter player = client.getPlayer();
      MapleInventoryManipulator.addById(client, itemId, quantity, player.getName(), petId, expiration);
   }

   @Override
   public void produceItem(MapleClient client, int itemId, short quantity) {
      MapleCharacter player = client.getPlayer();
      short flag = 0;
      if (player.gmLevel() < 3) {
         flag |= ItemConstants.ACCOUNT_SHARING;
         flag |= ItemConstants.UNTRADEABLE;
      }

      MapleInventoryManipulator.addById(client, itemId, quantity, player.getName(), -1, flag, -1);
   }
}
