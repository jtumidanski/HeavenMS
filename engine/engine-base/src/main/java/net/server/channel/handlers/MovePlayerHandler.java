package net.server.channel.handlers;

import client.MapleClient;
import net.server.PacketReader;
import net.server.channel.packet.movement.MovePlayerPacket;
import net.server.channel.packet.reader.MovePlayerReader;
import tools.MasterBroadcaster;
import tools.packet.movement.MovePlayer;

public final class MovePlayerHandler extends AbstractMoveHandler<MovePlayerPacket> {
   @Override
   public Class<? extends PacketReader<MovePlayerPacket>> getReaderClass() {
      return MovePlayerReader.class;
   }

   @Override
   public void handlePacket(MovePlayerPacket packet, MapleClient client) {
      if (packet == null) {
         return;
      }

      processMovementList(packet.movementDataList(), client.getPlayer());

      if (packet.hasMovement()) {
         client.getPlayer().getMap().movePlayer(client.getPlayer(), client.getPlayer().position());
         if (client.getPlayer().isHidden()) {
            client.getPlayer().getMap().broadcastGMMessage(client.getPlayer(), new MovePlayer(client.getPlayer().getId(), packet.movementList()), false);
         } else {
            MasterBroadcaster.getInstance().sendToAllInMap(client.getPlayer().getMap(), new MovePlayer(client.getPlayer().getId(), packet.movementList()), false, client.getPlayer());
         }
      }
   }
}
