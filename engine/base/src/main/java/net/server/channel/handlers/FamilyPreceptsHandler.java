package net.server.channel.handlers;

import client.MapleClient;
import client.MapleFamily;
import client.processor.MapleFamilyProcessor;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.family.FamilyPreceptsPacket;
import net.server.channel.packet.reader.FamilyPreceptsReader;
import tools.MaplePacketCreator;
import tools.PacketCreator;
import tools.packet.family.GetFamilyInfo;

public class FamilyPreceptsHandler extends AbstractPacketHandler<FamilyPreceptsPacket> {
   @Override
   public Class<FamilyPreceptsReader> getReaderClass() {
      return FamilyPreceptsReader.class;
   }

   @Override
   public void handlePacket(FamilyPreceptsPacket packet, MapleClient client) {
      MapleFamily family = client.getPlayer().getFamily();
      if (family == null) {
         return;
      }
      if (family.getLeader().getChr() != client.getPlayer()) {
         return; //only the leader can set the precepts
      }

      if (packet.newPrecepts().length() > 200) {
         return;
      }
      MapleFamilyProcessor.getInstance().setMessage(family, packet.newPrecepts(), true);
      //family.broadcastFamilyInfoUpdate(); //probably don't need to broadcast for this?
      PacketCreator.announce(client, new GetFamilyInfo(client.getPlayer().getFamilyEntry()));
   }

}
