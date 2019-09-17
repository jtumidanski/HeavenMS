package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.command.admin.ArtifactRankingPacket;
import net.server.channel.packet.command.admin.BanPlayerPacket;
import net.server.channel.packet.command.admin.BaseAdminCommandPacket;
import net.server.channel.packet.command.admin.BlockPlayerCommandPacket;
import net.server.channel.packet.command.admin.ChangeMapPacket;
import net.server.channel.packet.command.admin.DeleteInventoryByTypePacket;
import net.server.channel.packet.command.admin.EnteringMapPacket;
import net.server.channel.packet.command.admin.HidePacket;
import net.server.channel.packet.command.admin.KillMonsterPacket;
import net.server.channel.packet.command.admin.MonsterHpPacket;
import net.server.channel.packet.command.admin.PlayerWarnPacket;
import net.server.channel.packet.command.admin.QuestResetPacket;
import net.server.channel.packet.command.admin.SetPlayerExpPacket;
import net.server.channel.packet.command.admin.SummonMonsterPacket;
import net.server.channel.packet.command.admin.SummonMonstersItemPacket;
import net.server.channel.packet.command.admin.TestingPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class AdminCommandReader implements PacketReader<BaseAdminCommandPacket> {
   @Override
   public BaseAdminCommandPacket read(SeekableLittleEndianAccessor accessor) {
      byte mode = accessor.readByte();
      switch (mode) {
         case 0x00:
            return new SummonMonstersItemPacket(mode, accessor.readInt());
         case 0x01:
            return new DeleteInventoryByTypePacket(mode, accessor.readByte());
         case 0x02:
            return new SetPlayerExpPacket(mode, accessor.readInt());
         case 0x03:
            return new BanPlayerPacket(mode);
         case 0x04:
            return readBlockVictim(accessor, mode);
         case 0x10:
            return readHide(accessor, mode);
         case 0x11:
            return readEnteringMap(accessor, mode);
         case 0x12:
            return readChangeMap(accessor, mode);
         case 0x15:
            return readKillMonster(accessor, mode);
         case 0x16:
            return readQuestReset(accessor, mode);
         case 0x17:
            return readSummonMonsterPacket(accessor, mode);
         case 0x18:
            return readMobHp(accessor, mode);
         case 0x1E:
            return readPlayerWarn(accessor, mode);
         case 0x24:
            return new ArtifactRankingPacket(mode);
         case 0x77:
            if (accessor.available() == 4) {
               return new TestingPacket(mode, accessor.readInt());
            } else if (accessor.available() == 2) {
               return new TestingPacket(mode, accessor.readShort());
            }
      }

      return new BaseAdminCommandPacket(mode);
   }

   private BaseAdminCommandPacket readPlayerWarn(SeekableLittleEndianAccessor accessor, byte mode) {
      String victim = accessor.readMapleAsciiString();
      String message = accessor.readMapleAsciiString();
      return new PlayerWarnPacket(mode, victim, message);
   }

   private BaseAdminCommandPacket readMobHp(SeekableLittleEndianAccessor accessor, byte mode) {
      int mobHp = accessor.readInt();
      return new MonsterHpPacket(mode, mobHp);
   }

   private BaseAdminCommandPacket readSummonMonsterPacket(SeekableLittleEndianAccessor accessor, byte mode) {
      int mobId = accessor.readInt();
      int quantity = accessor.readInt();
      return new SummonMonsterPacket(mode, mobId, quantity);
   }

   private BaseAdminCommandPacket readQuestReset(SeekableLittleEndianAccessor accessor, byte mode) {
      int questId = accessor.readShort();
      return new QuestResetPacket(mode, questId);
   }

   private BaseAdminCommandPacket readKillMonster(SeekableLittleEndianAccessor accessor, byte mode) {
      int mobToKill = accessor.readInt();
      int amount = accessor.readInt();
      return new KillMonsterPacket(mode, mobToKill, amount);
   }

   private BaseAdminCommandPacket readChangeMap(SeekableLittleEndianAccessor accessor, byte mode) {
      return new ChangeMapPacket(mode, accessor.readMapleAsciiString(), accessor.readInt());
   }

   private BaseAdminCommandPacket readEnteringMap(SeekableLittleEndianAccessor accessor, byte mode) {
      return new EnteringMapPacket(mode, accessor.readByte());
   }

   private BaseAdminCommandPacket readHide(SeekableLittleEndianAccessor accessor, byte mode) {
      return new HidePacket(mode, accessor.readByte() == 1);
   }

   private BaseAdminCommandPacket readBlockVictim(SeekableLittleEndianAccessor accessor, byte mode) {
      String victim = accessor.readMapleAsciiString();
      int type = accessor.readByte(); //reason
      int duration = accessor.readInt();
      String description = accessor.readMapleAsciiString();
      return new BlockPlayerCommandPacket(mode, victim, type, duration, description);
   }
}
