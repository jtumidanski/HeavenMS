package client.command.commands.gm3;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

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
         if (weaponAttackBuff == null) {
            weaponAttackBuff = 0;
         }
         if (magicAttackBuff == null) {
            magicAttackBuff = 0;
         }

         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("CHECK_DAMAGE_COMMAND_ATTRIBUTES").with(victim.getTotalStr(), victim.getTotalDex(), victim.getTotalInt(), victim.getTotalLuk()));
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("CHECK_DAMAGE_COMMAND_ATTACK").with(victim.getTotalWeaponAttack(), victim.getTotalMagic()));
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("CHECK_DAMAGE_COMMAND_ATTACK_BUFFED").with(weaponAttackBuff, magicAttackBuff, blessing));
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("CHECK_DAMAGE_COMMAND_ATTACK_MAX_BASE").with(victim.getName(), maxBase));
      }, () -> MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("PLAYER_NOT_FOUND").with(params[0])));
   }
}
