package net.server.channel.handlers;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import config.YamlConfig;
import constants.game.GameConstants;
import constants.skills.Bishop;
import constants.skills.Evan;
import constants.skills.FirePoisonArchMage;
import constants.skills.IceLighteningArchMagician;
import net.server.channel.packet.AttackPacket;
import net.server.channel.packet.PacketReaderFactory;
import net.server.channel.packet.reader.DamageReader;
import server.MapleStatEffect;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.GetEnergy;
import tools.packet.PacketInput;
import tools.packet.attack.MagicAttack;

public final class MagicDamageHandler extends AbstractDealDamageHandler<AttackPacket> {
   @Override
   public Class<DamageReader> getReaderClass() {
      return DamageReader.class;
   }

   @Override
   public void handlePacket(SeekableLittleEndianAccessor accessor, MapleClient client) {
      DamageReader damageReader = (DamageReader) PacketReaderFactory.getInstance().get(getReaderClass());
      handlePacket(damageReader.read(accessor, client.getPlayer(), false, false), client);
   }

   @Override
   public void handlePacket(AttackPacket attack, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr.getBuffEffect(MapleBuffStat.MORPH) != null) {
         if (chr.getBuffEffect(MapleBuffStat.MORPH).isMorphWithoutAttack()) {
            // How are they attacking when the client won't let them?
            chr.getClient().disconnect(false, false);
            return;
         }
      }

      if (GameConstants.isDojo(chr.getMap().getId()) && attack.numAttacked() > 0) {
         chr.setDojoEnergy(chr.getDojoEnergy() + +YamlConfig.config.server.DOJO_ENERGY_ATK);
         PacketCreator.announce(c, new GetEnergy("energy", chr.getDojoEnergy()));
      }

      PacketInput packet;
      int charge = (attack.skill() == Evan.FIRE_BREATH || attack.skill() == Evan.ICE_BREATH || attack.skill() == FirePoisonArchMage.BIG_BANG || attack.skill() == IceLighteningArchMagician.BIG_BANG || attack.skill() == Bishop.BIG_BANG) ? attack.charge() : -1;
      packet = new MagicAttack(chr.getId(), attack.skill(), attack.skillLevel(), attack.stance(), attack.numAttackedAndDamage(), attack.allDamage(), charge, attack.speed(), attack.direction(), attack.display());


      MasterBroadcaster.getInstance().sendToAllInMapRange(chr.getMap(), packet, false, chr, true);
      MapleStatEffect effect = getAttackEffect(attack, chr, null);
      SkillFactory.getSkill(attack.skill()).ifPresent(skill -> applyCoolDownIfPresent(skill, chr));

      applyAttack(attack, chr, effect.getAttackCount());

      // MP Eater, works with right job
      int mpEaterSkillId = (chr.getJob().getId() - (chr.getJob().getId() % 10)) * 10000;
      SkillFactory.executeIfHasSkill(chr, mpEaterSkillId, (skill, skillLevel) -> {
         for (Integer singleDamage : attack.allDamage().keySet()) {
            skill.getEffect(skillLevel).applyPassive(chr, chr.getMap().getMapObject(singleDamage), 0);
         }
      });
   }
}
