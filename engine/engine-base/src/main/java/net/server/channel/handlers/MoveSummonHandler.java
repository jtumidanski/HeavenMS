package net.server.channel.handlers;

import java.util.Collection;

import client.MapleCharacter;
import client.MapleClient;
import net.server.PacketReader;
import net.server.channel.packet.movement.MoveSummonPacket;
import net.server.channel.packet.reader.MoveSummonReader;
import server.maps.MapleSummon;
import tools.MasterBroadcaster;
import tools.packet.movement.MoveSummon;

public final class MoveSummonHandler extends AbstractMoveHandler<MoveSummonPacket> {
   @Override
   public Class<? extends PacketReader<MoveSummonPacket>> getReaderClass() {
      return MoveSummonReader.class;
   }

   @Override
   public void handlePacket(MoveSummonPacket packet, MapleClient client) {
      if (packet == null) {
         return;
      }

      MapleCharacter player = client.getPlayer();
      Collection<MapleSummon> summons = player.getSummonsValues();
      MapleSummon summon = null;
      for (MapleSummon sum : summons) {
         if (sum.objectId() == packet.objectId()) {
            summon = sum;
            break;
         }
      }
      if (summon != null) {
         processMovementList(packet.movementDataList(), summon);
         MasterBroadcaster.getInstance().sendToAllInMapRange(player.getMap(),
               new MoveSummon(player.getId(), packet.objectId(), packet.startPosition(), packet.movementList()), player, summon.position());
      }
   }
}
