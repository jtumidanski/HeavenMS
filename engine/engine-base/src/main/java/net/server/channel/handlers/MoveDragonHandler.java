package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.server.PacketReader;
import net.server.channel.packet.movement.MoveDragonPacket;
import net.server.channel.packet.reader.MoveDragonReader;
import server.maps.MapleDragon;
import tools.MasterBroadcaster;
import tools.packet.movement.MoveDragon;

public class MoveDragonHandler extends AbstractMoveHandler<MoveDragonPacket> {
   @Override
   public Class<? extends PacketReader<MoveDragonPacket>> getReaderClass() {
      return MoveDragonReader.class;
   }

   @Override
   public void handlePacket(MoveDragonPacket packet, MapleClient client) {
      if (packet == null) {
         return;
      }

      final MapleCharacter chr = client.getPlayer();
      final MapleDragon dragon = chr.getDragon();
      if (dragon != null) {
         processMovementList(packet.movementDataList(), dragon);
         if (packet.hasMovement()) {
            if (chr.isHidden()) {
               chr.getMap().broadcastGMMessage(chr, new MoveDragon(dragon.ownerId(), packet.startPosition(), packet.movementList()));
            } else {
               MasterBroadcaster.getInstance().sendToAllInMapRange(chr.getMap(), new MoveDragon(dragon.ownerId(), packet.startPosition(), packet.movementList()), chr, dragon.position());
            }
         }
      }
   }
}