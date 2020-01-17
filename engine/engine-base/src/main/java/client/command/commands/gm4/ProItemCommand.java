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
import tools.I18nMessage;

public class ProItemCommand extends Command {
   {
      setDescription("");
   }

   private static void hardSetItemStats(Equip equip, short stat, short speedJump) {
      equip.str_$eq(stat);
      equip.dex_$eq(stat);
      equip._int_$eq(stat);
      equip.luk_$eq(stat);
      equip.matk_$eq(stat);
      equip.watk_$eq(stat);
      equip.acc_$eq(stat);
      equip.avoid_$eq(stat);
      equip.jump_$eq(speedJump);
      equip.speed_$eq(speedJump);
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
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("PRO_ITEM_COMMAND_SYNTAX"));
         return;
      }

      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      int itemId = Integer.parseInt(params[0]);

      if (ii.getName(itemId) == null) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("PRO_ITEM_DOES_NOT_EXIST"));
         return;
      }

      short stat = (short) Math.max(0, Short.parseShort(params[1]));
      short speedJump = params.length >= 3 ? (short) Math.max(0, Short.parseShort(params[2])) : 0;

      MapleInventoryType type = ItemConstants.getInventoryType(itemId);
      if (type.equals(MapleInventoryType.EQUIP)) {
         Item it = ii.getEquipById(itemId);
         it.owner_$eq(player.getName());

         hardSetItemStats((Equip) it, stat, speedJump);
         MapleInventoryManipulator.addFromDrop(c, it);
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("PRO_ITEM_NOT_EQUIP"));
      }
   }
}
