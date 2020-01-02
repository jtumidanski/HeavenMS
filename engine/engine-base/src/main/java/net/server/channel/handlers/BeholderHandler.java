package net.server.channel.handlers;

import java.util.Collection;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.BeholderPacket;
import net.server.channel.packet.reader.BeholderReader;
import server.maps.MapleSummon;

//TODO is anything actually happening here?
public final class BeholderHandler extends AbstractPacketHandler<BeholderPacket> {
   @Override
   public Class<BeholderReader> getReaderClass() {
      return BeholderReader.class;
   }

   @Override
   public void handlePacket(BeholderPacket packet, MapleClient client) {
      Collection<MapleSummon> summons = client.getPlayer().getSummonsValues();
      MapleSummon summon = null;
      for (MapleSummon sum : summons) {
         if (sum.objectId() == packet.objectId()) {
            summon = sum;
         }
      }
      if (summon != null) {
      } else {
         client.getPlayer().clearSummons();
      }
   }
}
