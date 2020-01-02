package client.command.commands.gm3;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class CheckDmgCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]).ifPresentOrElse(victim -> {
         int maxBase = victim.calculateMaxBaseDamage(victim.getTotalWeaponAttack());
         Integer weaponAttackBuff = victim.getBuffedValue(MapleBuffStat.WEAPON_ATTACK);
         Integer magicAttackBuff = victim.getBuffedValue(MapleBuffStat.MAGIC_ATTACK);
         int blessing = victim.getSkillLevel(10000000 * player.getJobType() + 12);
         if (weaponAttackBuff == null) weaponAttackBuff = 0;
         if (magicAttackBuff == null) magicAttackBuff = 0;

         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Cur Str: " + victim.getTotalStr() + " Cur Dex: " + victim.getTotalDex() + " Cur Int: " + victim.getTotalInt() + " Cur Luk: " + victim.getTotalLuk());
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Cur WATK: " + victim.getTotalWeaponAttack() + " Cur MATK: " + victim.getTotalMagic());
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Cur WATK Buff: " + weaponAttackBuff + " Cur MATK Buff: " + magicAttackBuff + " Cur Blessing Level: " + blessing);
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, victim.getName() + "'s maximum base damage (before skills) is " + maxBase);
      }, () -> MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Player '" + params[0] + "' could not be found on this world."));
   }
}
