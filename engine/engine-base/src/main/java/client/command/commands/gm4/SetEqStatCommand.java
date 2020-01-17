package client.command.commands.gm4;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import client.inventory.Equip;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.processor.ItemProcessor;
import constants.inventory.ItemConstants;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class SetEqStatCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("SET_EQUIP_STAT_COMMAND_SYNTAX"));
         return;
      }

      short newStat = (short) Math.max(0, Integer.parseInt(params[0]));
      short newSpdJmp = params.length >= 2 ? (short) Integer.parseInt(params[1]) : 0;
      MapleInventory equip = player.getInventory(MapleInventoryType.EQUIP);

      for (byte i = 1; i <= equip.getSlotLimit(); i++) {
         try {
            Equip eq = (Equip) equip.getItem(i);
            if (eq == null) continue;

            eq.wdef_$eq(newStat);
            eq.acc_$eq(newStat);
            eq.avoid_$eq(newStat);
            eq.jump_$eq(newSpdJmp);
            eq.matk_$eq(newStat);
            eq.mdef_$eq(newStat);
            eq.hp_$eq(newStat);
            eq.mp_$eq(newStat);
            eq.speed_$eq(newSpdJmp);
            eq.watk_$eq(newStat);
            eq.dex_$eq(newStat);
            eq._int_$eq(newStat);
            eq.str_$eq(newStat);
            eq.luk_$eq(newStat);

            short flag = eq.flag();
            flag |= ItemConstants.UNTRADEABLE;
            ItemProcessor.getInstance().setFlag(eq, flag);

            player.forceUpdateItem(eq);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }
}
