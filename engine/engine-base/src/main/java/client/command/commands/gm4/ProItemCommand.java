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
package client.command.commands.gm4;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.processor.ItemProcessor;
import constants.inventory.ItemConstants;
import server.MapleItemInformationProvider;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class ProItemCommand extends Command {
   {
      setDescription("");
   }

   private static void hardsetItemStats(Equip equip, short stat, short spdjmp) {
      equip.str_$eq(stat);
      equip.dex_$eq(stat);
      equip._int_$eq(stat);
      equip.luk_$eq(stat);
      equip.matk_$eq(stat);
      equip.watk_$eq(stat);
      equip.acc_$eq(stat);
      equip.avoid_$eq(stat);
      equip.jump_$eq(spdjmp);
      equip.speed_$eq(spdjmp);
      equip.wdef_$eq(stat);
      equip.mdef_$eq(stat);
      equip.hp_$eq(stat);
      equip.mp_$eq(stat);

      short flag = equip.flag();
      flag |= ItemConstants.UNTRADEABLE;
      ItemProcessor.getInstance().setFlag(equip, flag);
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 2) {
         player.yellowMessage("Syntax: !proitem <itemid> <stat value> [<spdjmp value>]");
         return;
      }

      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      int itemid = Integer.parseInt(params[0]);

      if (ii.getName(itemid) == null) {
         player.yellowMessage("Item id '" + params[0] + "' does not exist.");
         return;
      }

      short stat = (short) Math.max(0, Short.parseShort(params[1]));
      short spdjmp = params.length >= 3 ? (short) Math.max(0, Short.parseShort(params[2])) : 0;

      MapleInventoryType type = ItemConstants.getInventoryType(itemid);
      if (type.equals(MapleInventoryType.EQUIP)) {
         Item it = ii.getEquipById(itemid);
         it.owner_$eq(player.getName());

         hardsetItemStats((Equip) it, stat, spdjmp);
         MapleInventoryManipulator.addFromDrop(c, it);
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, "Make sure it's an equippable item.");
      }
   }
}
