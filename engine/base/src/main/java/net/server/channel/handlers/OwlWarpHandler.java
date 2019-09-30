/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2018 RonanLana

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package net.server.channel.handlers;

import client.MapleClient;
import constants.GameConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.OwlWarpPacket;
import net.server.channel.packet.reader.OwlWarpReader;
import server.maps.MapleHiredMerchant;
import server.maps.MaplePlayerShop;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.playerinteraction.GetHiredMerchant;

/*
 * @author Ronan
 */
public final class OwlWarpHandler extends AbstractPacketHandler<OwlWarpPacket> {
   @Override
   public Class<OwlWarpReader> getReaderClass() {
      return OwlWarpReader.class;
   }

   @Override
   public void handlePacket(OwlWarpPacket packet, MapleClient client) {

      if (packet.ownerId() == client.getPlayer().getId()) {
         MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, "You cannot visit your own shop.");
         return;
      }

      MapleHiredMerchant hm = client.getWorldServer().getHiredMerchant(packet.ownerId());   // if both hired merchant and player shop is on the same map
      MaplePlayerShop ps;
      if (hm == null || hm.getMapId() != packet.mapId() || !hm.hasItem(client.getPlayer().getOwlSearch())) {
         ps = client.getWorldServer().getPlayerShop(packet.ownerId());
         if (ps == null || ps.getMapId() != packet.mapId() || !ps.hasItem(client.getPlayer().getOwlSearch())) {
            if (hm == null && ps == null) {
               client.announce(MaplePacketCreator.getOwlMessage(1));
            } else {
               client.announce(MaplePacketCreator.getOwlMessage(3));
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
                           client.announce(MaplePacketCreator.getOwlMessage(2));
                        } else {
                           client.announce(MaplePacketCreator.getOwlMessage(17));
                        }
                     }
                  } else {
                     //c.announce(MaplePacketCreator.serverNotice(1, "That merchant has either been closed or is under maintenance."));
                     client.announce(MaplePacketCreator.getOwlMessage(18));
                  }
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, "That shop is currently located in another channel. Current location: Channel " + hm.getChannel() + ", '" + hm.getMap().getMapName() + "'.");
               }
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, "That shop is currently located outside of the FM area. Current location: Channel " + hm.getChannel() + ", '" + hm.getMap().getMapName() + "'.");
            }
         } else {
            //c.announce(MaplePacketCreator.serverNotice(1, "That merchant has either been closed or is under maintenance."));
            client.announce(MaplePacketCreator.getOwlMessage(18));
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
                        client.announce(MaplePacketCreator.getOwlMessage(2));
                     }
                  } else {
                     //c.announce(MaplePacketCreator.serverNotice(1, "That merchant has either been closed or is under maintenance."));
                     client.announce(MaplePacketCreator.getOwlMessage(18));
                  }
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, "That merchant is currently located in another channel. Current location: Channel " + hm.getChannel() + ", '" + hm.getMap().getMapName() + "'.");
               }
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, "That merchant is currently located outside of the FM area. Current location: Channel " + hm.getChannel() + ", '" + hm.getMap().getMapName() + "'.");
            }
         } else {
            //c.announce(MaplePacketCreator.serverNotice(1, "That merchant has either been closed or is under maintenance."));
            client.announce(MaplePacketCreator.getOwlMessage(18));
         }
      }
   }
}