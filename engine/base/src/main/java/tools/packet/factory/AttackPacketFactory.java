package tools.packet.factory;

import java.util.List;
import java.util.Map;

import net.opcodes.SendOpcode;
import net.server.channel.handlers.SummonAttackEntry;
import tools.data.output.LittleEndianWriter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.attack.CloseRangeAttack;
import tools.packet.attack.MagicAttack;
import tools.packet.attack.RangedAttack;
import tools.packet.attack.SummonAttack;
import tools.packet.attack.ThrowGrenade;

public class AttackPacketFactory extends AbstractPacketFactory {
   private static AttackPacketFactory instance;

   public static AttackPacketFactory getInstance() {
      if (instance == null) {
         instance = new AttackPacketFactory();
      }
      return instance;
   }

   private AttackPacketFactory() {
      registry.setHandler(CloseRangeAttack.class, packet -> this.closeRangeAttack((CloseRangeAttack) packet));
      registry.setHandler(RangedAttack.class, packet -> this.rangedAttack((RangedAttack) packet));
      registry.setHandler(MagicAttack.class, packet -> this.magicAttack((MagicAttack) packet));
      registry.setHandler(SummonAttack.class, packet -> this.summonAttack((SummonAttack) packet));
      registry.setHandler(ThrowGrenade.class, packet -> this.throwGrenade((ThrowGrenade) packet));
   }

   protected byte[] closeRangeAttack(CloseRangeAttack packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CLOSE_RANGE_ATTACK.getValue());
      addAttackBody(mplew, packet.characterId(), packet.skill(), packet.skillLevel(), packet.stance(),
            packet.numAttackedAndDamage(), 0, packet.damage(), packet.speed(), packet.direction(),
            packet.display());
      return mplew.getPacket();
   }

   protected byte[] rangedAttack(RangedAttack packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.RANGED_ATTACK.getValue());
      addAttackBody(mplew, packet.characterId(), packet.skill(), packet.skillLevel(), packet.stance(),
            packet.numAttackedAndDamage(), packet.projectile(), packet.damage(), packet.speed(), packet.direction(),
            packet.display());
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   protected byte[] magicAttack(MagicAttack packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MAGIC_ATTACK.getValue());
      addAttackBody(mplew, packet.characterId(), packet.skill(), packet.skillLevel(), packet.stance(),
            packet.numAttackedAndDamage(), 0, packet.damage(), packet.speed(), packet.direction(),
            packet.display());
      if (packet.charge() != -1) {
         mplew.writeInt(packet.charge());
      }
      return mplew.getPacket();
   }

   protected void addAttackBody(LittleEndianWriter lew, int characterId, int skill, int skilllevel, int stance,
                                int numAttackedAndDamage, int projectile, Map<Integer, List<Integer>> damage,
                                int speed, int direction, int display) {
      lew.writeInt(characterId);
      lew.write(numAttackedAndDamage);
      lew.write(0x5B);//?
      lew.write(skilllevel);
      if (skilllevel > 0) {
         lew.writeInt(skill);
      }
      lew.write(display);
      lew.write(direction);
      lew.write(stance);
      lew.write(speed);
      lew.write(0x0A);
      lew.writeInt(projectile);
      for (Integer oned : damage.keySet()) {
         List<Integer> onedList = damage.get(oned);
         if (onedList != null) {
            lew.writeInt(oned);
            lew.write(0x0);
            if (skill == 4211006) {
               lew.write(onedList.size());
            }
            for (Integer eachd : onedList) {
               lew.writeInt(eachd);
            }
         }
      }
   }

   protected byte[] summonAttack(SummonAttack packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      //b2 00 29 f7 00 00 9a a3 04 00 c8 04 01 94 a3 04 00 06 ff 2b 00
      mplew.writeShort(SendOpcode.SUMMON_ATTACK.getValue());
      mplew.writeInt(packet.characterId());
      mplew.writeInt(packet.summonObjectId());
      mplew.write(0);     // char level
      mplew.write(packet.direction());
      mplew.write(packet.damage().size());
      for (SummonAttackEntry attackEntry : packet.damage()) {
         mplew.writeInt(attackEntry.monsterObjectId()); // oid
         mplew.write(6); // who knows
         mplew.writeInt(attackEntry.damage()); // damage
      }

      return mplew.getPacket();
   }

        /*
        public static byte[] summonAttack(int cid, int summonSkillId, byte direction, List<SummonAttackEntry> allDamage) {
                final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
                //b2 00 29 f7 00 00 9a a3 04 00 c8 04 01 94 a3 04 00 06 ff 2b 00
                mplew.writeShort(SendOpcode.SUMMON_ATTACK.getValue());
                mplew.writeInt(cid);
                mplew.writeInt(summonSkillId);
                mplew.write(direction);
                mplew.write(4);
                mplew.write(allDamage.size());
                for (SummonAttackEntry attackEntry : allDamage) {
                        mplew.writeInt(attackEntry.getMonsterOid()); // oid
                        mplew.write(6); // who knows
                        mplew.writeInt(attackEntry.getDamage()); // damage
                }
                return mplew.getPacket();
        }
        */

   protected byte[] throwGrenade(ThrowGrenade packet) { // packets found thanks to GabrielSin
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.THROW_GRENADE.getValue());
      mplew.writeInt(packet.characterId());
      mplew.writeInt(packet.position().x);
      mplew.writeInt(packet.position().y);
      mplew.writeInt(packet.keyDown());
      mplew.writeInt(packet.skillId());
      mplew.writeInt(packet.skillLevel());
      return mplew.getPacket();
   }
}