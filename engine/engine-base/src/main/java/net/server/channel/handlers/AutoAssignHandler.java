package net.server.channel.handlers;

import client.MapleClient;
import client.processor.stat.AssignAPProcessor;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.AssignAPPacket;
import net.server.channel.packet.reader.AssignAPReader;

public class AutoAssignHandler extends AbstractPacketHandler<AssignAPPacket> {
   @Override
   public Class<AssignAPReader> getReaderClass() {
      return AssignAPReader.class;
   }

   @Override
   public void handlePacket(AssignAPPacket packet, MapleClient client) {
      AssignAPProcessor.APAutoAssignAction(client, packet.jobId(), packet.types(), packet.gains());
   }
}
