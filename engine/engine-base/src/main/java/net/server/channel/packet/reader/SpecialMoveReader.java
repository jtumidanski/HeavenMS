package net.server.channel.packet.reader;

import java.awt.Point;

import constants.skills.DarkKnight;
import constants.skills.Hero;
import constants.skills.Paladin;
import constants.skills.SuperGM;
import net.server.PacketReader;
import net.server.channel.packet.special.BaseSpecialMovePacket;
import net.server.channel.packet.special.MonsterMagnetData;
import net.server.channel.packet.special.MonsterMagnetPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class SpecialMoveReader implements PacketReader<BaseSpecialMovePacket> {
   @Override
   public BaseSpecialMovePacket read(SeekableLittleEndianAccessor accessor) {
      accessor.readInt();
      int skillId = accessor.readInt();
      int skillLevel = accessor.readByte();

      //specific skill stuff
      if (skillId == Hero.MONSTER_MAGNET || skillId == Paladin.MONSTER_MAGNET || skillId == DarkKnight.MONSTER_MAGNET) { // Monster Magnet
         int num = accessor.readInt();
         MonsterMagnetData[] magnetData = new MonsterMagnetData[num];
         for (int i = 0; i < num; i++) {
            int mobOid = accessor.readInt();
            byte success = accessor.readByte();
            magnetData[i] = new MonsterMagnetData(mobOid, success);
         }
         byte direction = accessor.readByte();
         return new MonsterMagnetPacket(skillId, skillLevel, null, magnetData, direction);
      } else if (skillId == SuperGM.HEAL_PLUS_DISPEL) {
         accessor.skip(11);
      } else if (skillId % 10000000 == 1004) {
         accessor.readShort();
      }

      Point pos = null;
      if (accessor.available() == 5) {
         pos = new Point(accessor.readShort(), accessor.readShort());
      }
      return new BaseSpecialMovePacket(skillId, skillLevel, pos);
   }
}
