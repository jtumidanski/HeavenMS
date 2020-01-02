package net.server.channel.handlers;

import client.MapleClient;
import client.processor.CharacterProcessor;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.TransferNameResultPacket;
import net.server.channel.packet.reader.TransferNameResultReader;
import tools.PacketCreator;
import tools.packet.transfer.name.CheckNameChange;

public final class TransferNameResultHandler extends AbstractPacketHandler<TransferNameResultPacket> {
   @Override
   public Class<TransferNameResultReader> getReaderClass() {
      return TransferNameResultReader.class;
   }

   @Override
   public void handlePacket(TransferNameResultPacket packet, MapleClient client) {
      PacketCreator.announce(client, new CheckNameChange(packet.name(), CharacterProcessor.getInstance().canCreateChar(packet.name())));
   }
}