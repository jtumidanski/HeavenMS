package net.server.channel.handlers;

import client.MapleClient;
import constants.game.GameConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.OwlWarpPacket;
import net.server.channel.packet.reader.OwlWarpReader;
import server.maps.MapleHiredMerchant;
import server.maps.MaplePlayerShop;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.character.interaction.GetHiredMerchant;
import tools.packet.owl.GetOwlMessage;

public final class OwlWarpHandler extends AbstractPacketHandler<OwlWarpPacket> {
   @Override
   public Class<OwlWarpReader> getReaderClass() {
      return OwlWarpReader.class;
   }

   @Override
   public void handlePacket(OwlWarpPacket packet, MapleClient client) {

      if (packet.ownerId() == client.getPlayer().getId()) {
         MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("OWL_OWN_SHOP_ERROR"));
         return;
      }

      MapleHiredMerchant hm = client.getWorldServer().getHiredMerchant(packet.ownerId());   // if both hired merchant and player shop is on the same map
      MaplePlayerShop ps;
      if (hm == null || hm.getMapId() != packet.mapId() || !hm.hasItem(client.getPlayer().getOwlSearch())) {
         ps = client.getWorldServer().getPlayerShop(packet.ownerId());
         if (ps == null || ps.getMapId() != packet.mapId() || !ps.hasItem(client.getPlayer().getOwlSearch())) {
            if (hm == null && ps == null) {
               PacketCreator.announce(client, new GetOwlMessage(1));
            } else {
               PacketCreator.announce(client, new GetOwlMessage(3));
            }
            return;
         }

         if (ps.isOpen()) {
            if (GameConstants.isFreeMarketRoom(packet.mapId())) {
               if (ps.getChannel() == client.getChannel()) {
                  client.getPlayer().changeMap(packet.mapId());

                  if (ps.isOpen()) {   //change map has a delay, must double check
                     if (!ps.visitShop(client.getPlayer())) {
                        if (!ps.isBanned(client.getPlayer().getName())) {
                           PacketCreator.announce(client, new GetOwlMessage(2));
                        } else {
                           PacketCreator.announce(client, new GetOwlMessage(17));
                        }
                     }
                  } else {
                     //c.announce(MaplePacketCreator.serverNotice(1, "That merchant has either been closed or is under maintenance."));
                     PacketCreator.announce(client, new GetOwlMessage(18));
                  }
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("OWL_OTHER_CHANNEL_ERROR").with(hm.getChannel(), hm.getMap().getMapName()));
               }
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("OWL_OTHER_AREA").with(hm.getChannel(), hm.getMap().getMapName()));
            }
         } else {
            //c.announce(MaplePacketCreator.serverNotice(1, "That merchant has either been closed or is under maintenance."));
            PacketCreator.announce(client, new GetOwlMessage(18));
         }
      } else {
         if (hm.isOpen()) {
            if (GameConstants.isFreeMarketRoom(packet.mapId())) {
               if (hm.getChannel() == client.getChannel()) {
                  client.getPlayer().changeMap(packet.mapId());

                  if (hm.isOpen()) {   //change map has a delay, must double check
                     if (hm.addVisitor(client.getPlayer())) {
                        PacketCreator.announce(client, new GetHiredMerchant(client.getPlayer(), hm, false));
                        client.getPlayer().setHiredMerchant(hm);
                     } else {
                        //c.announce(MaplePacketCreator.serverNotice(1, hm.getOwner() + "'s merchant is full. Wait awhile before trying again."));
                        PacketCreator.announce(client, new GetOwlMessage(2));
                     }
                  } else {
                     //c.announce(MaplePacketCreator.serverNotice(1, "That merchant has either been closed or is under maintenance."));
                     PacketCreator.announce(client, new GetOwlMessage(18));
                  }
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("OWL_OTHER_CHANNEL_ERROR").with(hm.getChannel(), hm.getMap().getMapName()));
               }
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("OWL_OTHER_AREA").with(hm.getChannel(), hm.getMap().getMapName()));
            }
         } else {
            //c.announce(MaplePacketCreator.serverNotice(1, "That merchant has either been closed or is under maintenance."));
            PacketCreator.announce(client, new GetOwlMessage(18));
         }
      }
   }
}