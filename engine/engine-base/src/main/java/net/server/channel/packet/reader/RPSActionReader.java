package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.rps.AnswerPacket;
import net.server.channel.packet.rps.BaseRPSActionPacket;
import net.server.channel.packet.rps.ContinuePacket;
import net.server.channel.packet.rps.LeavePacket;
import net.server.channel.packet.rps.RetryPacket;
import net.server.channel.packet.rps.StartGamePacket;
import net.server.channel.packet.rps.TimeOverPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class RPSActionReader implements PacketReader<BaseRPSActionPacket> {
   @Override
   public BaseRPSActionPacket read(SeekableLittleEndianAccessor accessor) {
      boolean available = accessor.available() != 0;
      byte mode = -1;
      if (available) {
         mode = accessor.readByte();
         switch (mode) {
            case 0:
               return new StartGamePacket(available, mode);
            case 1:
               byte answer = accessor.readByte();
               return new AnswerPacket(available, mode, answer);
            case 2:
               return new TimeOverPacket(available, mode);
            case 3:
               return new ContinuePacket(available, mode);
            case 4:
               return new LeavePacket(available, mode);
            case 5:
               return new RetryPacket(available, mode);
         }
      }
      return new BaseRPSActionPacket(available, mode);
   }
}
