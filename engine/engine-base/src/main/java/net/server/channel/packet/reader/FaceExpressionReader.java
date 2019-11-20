package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.FaceExpressionPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class FaceExpressionReader implements PacketReader<FaceExpressionPacket> {
   @Override
   public FaceExpressionPacket read(SeekableLittleEndianAccessor accessor) {
      int emote = accessor.readInt();
      return new FaceExpressionPacket(emote);
   }
}
