package net.server.channel.handlers;

import client.MapleClient;
import client.processor.stat.AssignAPProcessor;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.DistributeAPPacket;
import net.server.channel.packet.reader.DistributeAPReader;

public final class DistributeAPHandler extends AbstractPacketHandler<DistributeAPPacket> {
   @Override
   public Class<DistributeAPReader> getReaderClass() {
      return DistributeAPReader.class;
   }

   @Override
   public void handlePacket(DistributeAPPacket packet, MapleClient client) {
      AssignAPProcessor.APAssignAction(client, packet.number());
   }
}
