package tools.packet.factory;

import java.util.List;
import java.util.Map;

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
      Handler.handle(CloseRangeAttack.class).decorate(this::closeRangeAttack).register(registry);
      Handler.handle(RangedAttack.class).decorate(this::rangedAttack).register(registry);
      Handler.handle(MagicAttack.class).decorate(this::magicAttack).register(registry);
      Handler.handle(SummonAttack.class).decorate(this::summonAttack).register(registry);
      Handler.handle(ThrowGrenade.class).decorate(this::throwGrenade).register(registry);
   }

   protected void closeRangeAttack(MaplePacketLittleEndianWriter writer, CloseRangeAttack packet) {
      addAttackBody(writer, packet.characterId(), packet.skill(), packet.skillLevel(), packet.stance(),
            packet.numAttackedAndDamage(), 0, packet.damage(), packet.speed(), packet.direction(),
            packet.display());
   }

   protected void rangedAttack(MaplePacketLittleEndianWriter writer, RangedAttack packet) {
      addAttackBody(writer, packet.characterId(), packet.skill(), packet.skillLevel(), packet.stance(),
            packet.numAttackedAndDamage(), packet.projectile(), packet.damage(), packet.speed(), packet.direction(),
            packet.display());
      writer.writeInt(0);
   }

   protected void magicAttack(MaplePacketLittleEndianWriter writer, MagicAttack packet) {
      addAttackBody(writer, packet.characterId(), packet.skill(), packet.skillLevel(), packet.stance(),
            packet.numAttackedAndDamage(), 0, packet.damage(), packet.speed(), packet.direction(),
            packet.display());
      if (packet.charge() != -1) {
         writer.writeInt(packet.charge());
      }
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

   protected void summonAttack(MaplePacketLittleEndianWriter writer, SummonAttack packet) {
      //b2 00 29 f7 00 00 9a a3 04 00 c8 04 01 94 a3 04 00 06 ff 2b 00
      writer.writeInt(packet.characterId());
      writer.writeInt(packet.summonObjectId());
      writer.write(0);     // char level
      writer.write(packet.direction());
      writer.write(packet.damage().size());
      for (SummonAttackEntry attackEntry : packet.damage()) {
         writer.writeInt(attackEntry.monsterObjectId()); // oid
         writer.write(6); // who knows
         writer.writeInt(attackEntry.damage()); // damage
      }
   }

        /*
        public static byte[] summonAttack(int cid, int summonSkillId, byte direction, List<SummonAttackEntry> allDamage) {
                final MaplePacketLittleEndianWriter writer = new MaplePacketLittleEndianWriter();
                //b2 00 29 f7 00 00 9a a3 04 00 c8 04 01 94 a3 04 00 06 ff 2b 00
                writer.writeShort(SendOpcode.SUMMON_ATTACK.getValue());
                writer.writeInt(cid);
                writer.writeInt(summonSkillId);
                writer.write(direction);
                writer.write(4);
                writer.write(allDamage.size());
                for (SummonAttackEntry attackEntry : allDamage) {
                        writer.writeInt(attackEntry.getMonsterOid()); // oid
                        writer.write(6); // who knows
                        writer.writeInt(attackEntry.getDamage()); // damage
                }
                return writer.getPacket();
        }
        */

   protected void throwGrenade(MaplePacketLittleEndianWriter writer, ThrowGrenade packet) { // packets found thanks to GabrielSin
      writer.writeInt(packet.characterId());
      writer.writeInt(packet.position().x);
      writer.writeInt(packet.position().y);
      writer.writeInt(packet.keyDown());
      writer.writeInt(packet.skillId());
      writer.writeInt(packet.skillLevel());
   }
}