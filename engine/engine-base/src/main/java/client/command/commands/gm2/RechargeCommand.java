package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.inventory.ItemConstants;
import server.MapleItemInformationProvider;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class RechargeCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      for (Item toRecharge : c.getPlayer().getInventory(MapleInventoryType.USE).list()) {
         if (ItemConstants.isThrowingStar(toRecharge.id())) {
            toRecharge.quantity_$eq(ii.getSlotMax(c, toRecharge.id()));
            c.getPlayer().forceUpdateItem(toRecharge);
         } else if (ItemConstants.isArrow(toRecharge.id())) {
            toRecharge.quantity_$eq(ii.getSlotMax(c, toRecharge.id()));
            c.getPlayer().forceUpdateItem(toRecharge);
         } else if (ItemConstants.isBullet(toRecharge.id())) {
            toRecharge.quantity_$eq(ii.getSlotMax(c, toRecharge.id()));
            c.getPlayer().forceUpdateItem(toRecharge);
         } else if (ItemConstants.isConsumable(toRecharge.id())) {
            toRecharge.quantity_$eq(ii.getSlotMax(c, toRecharge.id()));
            c.getPlayer().forceUpdateItem(toRecharge);
         }
      }
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("RECHARGE_COMMAND_SUCCESS"));
   }
}
