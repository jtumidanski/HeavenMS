package client.command.commands.gm4;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import client.inventory.Equip;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.processor.ItemProcessor;
import constants.ItemConstants;
import constants.MapleInventoryType;
import server.MapleItemInformationProvider;
import tools.I18nMessage;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class ProItemCommand extends Command {
   {
      setDescription("");
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
         Equip equip = Equip.newBuilder(ii.getEquipById(itemId))
               .setOwner(player.getName())
               .setStr(stat)
               .setDex(stat)
               .setIntelligence(stat)
               .setLuk(stat)
               .setMatk(stat)
               .setWatk(stat)
               .setAcc(stat)
               .setAvoid(stat)
               .setJump(speedJump)
               .setSpeed(speedJump)
               .setWdef(stat)
               .setMdef(stat)
               .setHp(stat)
               .setMp(stat)
               .setFlag(ItemProcessor.getInstance().setFlag(itemId, ItemConstants.UNTRADEABLE))
               .build();
         MapleInventoryManipulator.addFromDrop(c, equip);
      } else {
         MessageBroadcaster.getInstance()
               .sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("PRO_ITEM_NOT_EQUIP"));
      }
   }
}
