package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.reader.RPSActionReader;
import net.server.channel.packet.rps.AnswerPacket;
import net.server.channel.packet.rps.BaseRPSActionPacket;
import net.server.channel.packet.rps.ContinuePacket;
import net.server.channel.packet.rps.LeavePacket;
import net.server.channel.packet.rps.RetryPacket;
import net.server.channel.packet.rps.StartGamePacket;
import net.server.channel.packet.rps.TimeOverPacket;
import server.minigame.MapleRockPaperScissor;
import tools.MaplePacketCreator;

/**
 * @Author Arnah
 * @Website http://Vertisy.ca/
 * @since Aug 15, 2016
 */
public final class RPSActionHandler extends AbstractPacketHandler<BaseRPSActionPacket, RPSActionReader> {
   @Override
   public Class<RPSActionReader> getReaderClass() {
      return RPSActionReader.class;
   }

   @Override
   public void handlePacket(BaseRPSActionPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      MapleRockPaperScissor rps = chr.getRPS();

      if (client.tryAcquireClient()) {
         try {
            if (packet.available() || !chr.getMap().containsNPC(9000019)) {
               if (rps != null) {
                  rps.dispose(client);
               }
               return;
            }
            if (packet instanceof StartGamePacket || packet instanceof RetryPacket) {
               if (rps != null) {
                  rps.reward(client);
               }
               if (chr.getMeso() >= 1000) {
                  chr.setRPS(new MapleRockPaperScissor(client, packet.mode()));
               } else {
                  client.announce(MaplePacketCreator.rpsMesoError(-1));
               }
            } else if (packet instanceof AnswerPacket) {
               if (rps == null || !rps.answer(client, ((AnswerPacket) packet).answer())) {
                  client.announce(MaplePacketCreator.rpsMode((byte) 0x0D));// 13
               }
            } else if (packet instanceof TimeOverPacket) {
               if (rps == null || !rps.timeOut(client)) {
                  client.announce(MaplePacketCreator.rpsMode((byte) 0x0D));
               }
            } else if (packet instanceof ContinuePacket) {
               if (rps == null || !rps.nextRound(client)) {
                  client.announce(MaplePacketCreator.rpsMode((byte) 0x0D));
               }
            } else if (packet instanceof LeavePacket) {
               if (rps != null) {
                  rps.dispose(client);
               } else {
                  client.announce(MaplePacketCreator.rpsMode((byte) 0x0D));
               }
            }
         } finally {
            client.releaseClient();
         }
      }
   }
}
