package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import client.inventory.Item;
import constants.ItemConstants;
import constants.MapleInventoryType;
import server.MapleItemInformationProvider;
import tools.I18nMessage;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class RechargeCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      for (Item toRecharge : c.getPlayer().getInventory(MapleInventoryType.USE).list()) {
         if (ItemConstants.isThrowingStar(toRecharge.id())
               || ItemConstants.isArrow(toRecharge.id())
               || ItemConstants.isBullet(toRecharge.id())
               || ItemConstants.isConsumable(toRecharge.id())) {
            c.getPlayer().forceUpdateItem(Item.newBuilder(toRecharge).setQuantity(ii.getSlotMax(c, toRecharge.id())).build());
         }
      }
      MessageBroadcaster.getInstance()
            .sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("RECHARGE_COMMAND_SUCCESS"));
   }
}
