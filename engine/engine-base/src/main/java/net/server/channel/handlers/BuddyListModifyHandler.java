package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.processor.BuddyListProcessor;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.buddy.AcceptBuddyPacket;
import net.server.channel.packet.buddy.AddBuddyPacket;
import net.server.channel.packet.buddy.BaseBuddyPacket;
import net.server.channel.packet.buddy.DeleteBuddyPacket;
import net.server.channel.packet.reader.BuddyReader;

public class BuddyListModifyHandler extends AbstractPacketHandler<BaseBuddyPacket> {
   @Override
   public Class<BuddyReader> getReaderClass() {
      return BuddyReader.class;
   }

   @Override
   public void handlePacket(BaseBuddyPacket packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (packet instanceof AddBuddyPacket) {
         BuddyListProcessor.getInstance().addBuddy(player, ((AddBuddyPacket) packet).name(), ((AddBuddyPacket) packet).group());
      } else if (packet instanceof AcceptBuddyPacket) {
         BuddyListProcessor.getInstance().accept(player, ((AcceptBuddyPacket) packet).otherCharacterId());
      } else if (packet instanceof DeleteBuddyPacket) {
         BuddyListProcessor.getInstance().deleteBuddy(player, ((DeleteBuddyPacket) packet).otherCharacterId());
      }
   }
}
