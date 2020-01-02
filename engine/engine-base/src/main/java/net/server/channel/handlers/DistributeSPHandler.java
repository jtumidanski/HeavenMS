package net.server.channel.handlers;

import client.MapleClient;
import client.processor.stat.AssignSPProcessor;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.DistributeSPPacket;
import net.server.channel.packet.reader.DistributeSPReader;

public final class DistributeSPHandler extends AbstractPacketHandler<DistributeSPPacket> {
   @Override
   public Class<DistributeSPReader> getReaderClass() {
      return DistributeSPReader.class;
   }

   @Override
   public void handlePacket(DistributeSPPacket packet, MapleClient client) {
      AssignSPProcessor.SPAssignAction(client, packet.skillId());
   }
}