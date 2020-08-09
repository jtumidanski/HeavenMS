package client.command.commands.gm4;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import client.inventory.Equip;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.processor.ItemProcessor;
import constants.inventory.ItemConstants;
import tools.I18nMessage;
import tools.MessageBroadcaster;

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
            if (eq == null) {
               continue;
            }

            Equip updated = Equip.newBuilder(eq)
                  .setWdef(newStat)
                  .setAcc(newStat)
                  .setAvoid(newStat)
                  .setJump(newSpdJmp)
                  .setMatk(newStat)
                  .setMdef(newStat)
                  .setHp(newStat)
                  .setMp(newStat)
                  .setSpeed(newSpdJmp)
                  .setWatk(newStat)
                  .setDex(newStat)
                  .setIntelligence(newStat)
                  .setStr(newStat)
                  .setLuk(newStat)
                  .setFlag(ItemProcessor.getInstance().setFlag(eq.id(), (short) (eq.flag() | ItemConstants.UNTRADEABLE)))
                  .build();
            player.forceUpdateItem(updated);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }
}
