package net.server.channel.handlers;

import java.awt.Point;
import java.util.Arrays;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.ItemFactory;
import constants.game.GameConstants;
import net.server.AbstractPacketHandler;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MaplePlayerShop;
import server.maps.MaplePortal;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.MiniRoomError;
import tools.packet.character.interaction.GetMiniRoomError;
import tools.packet.shop.RetrieveFirstMessage;
import tools.packet.shop.ShowHiredMerchantBox;

public final class HiredMerchantRequest extends AbstractPacketHandler<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      try {
         for (MapleMapObject mmo : chr.getMap().getMapObjectsInRange(chr.position(), 23000, Arrays.asList(MapleMapObjectType.HIRED_MERCHANT, MapleMapObjectType.PLAYER))) {
            if (mmo instanceof MapleCharacter) {
               MapleCharacter mc = (MapleCharacter) mmo;

               MaplePlayerShop shop = mc.getPlayerShop();
               if (shop != null && shop.isOwner(mc)) {
                  PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.CANT_ESTABLISH_MINI_ROOM));
                  return;
               }
            } else {
               PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.CANT_ESTABLISH_MINI_ROOM));
               return;
            }
         }

         Point characterPosition = chr.position();
         MaplePortal portal = chr.getMap().findClosestTeleportPortal(characterPosition);
         if (portal != null && portal.getPosition().distance(characterPosition) < 120.0) {
            PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.NOT_NEAR_PORTAL));
            return;
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      if (GameConstants.isFreeMarketRoom(chr.getMapId())) {
         if (!chr.hasMerchant()) {
            if (ItemFactory.MERCHANT.loadItems(chr.getId(), false).isEmpty() && chr.getMerchantMeso() == 0) {
               PacketCreator.announce(client, new ShowHiredMerchantBox());
            } else {
               PacketCreator.announce(chr, new RetrieveFirstMessage());
            }
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You already have a store open.");
         }
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You cannot open your hired merchant here.");
      }
   }
}
