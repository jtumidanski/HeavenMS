/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

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

import java.sql.Connection;

import client.MapleCharacter;
import client.MapleClient;
import client.database.administrator.CharacterAdministrator;
import client.database.administrator.InventoryItemAdministrator;
import client.database.provider.CharacterProvider;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.processor.CharacterProcessor;
import client.processor.DueyProcessor;
import client.processor.MapleRingProcessor;
import client.processor.NoteProcessor;
import net.server.AbstractPacketHandler;
import net.server.channel.Channel;
import net.server.channel.packet.reader.RingActionReader;
import net.server.channel.packet.ring.BaseRingPacket;
import net.server.channel.packet.ring.BreakEngagementPacket;
import net.server.channel.packet.ring.CancelProposal;
import net.server.channel.packet.ring.EngagementProposalPacket;
import net.server.channel.packet.ring.HandleWishListPacket;
import net.server.channel.packet.ring.InviteToWeddingPacket;
import net.server.channel.packet.ring.OpenWeddingInvitationPacket;
import net.server.channel.packet.ring.RespondToProposalPacket;
import net.server.world.World;
import scripting.event.EventInstanceManager;
import server.MapleItemInformationProvider;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.Pair;
import tools.ServerNoticeType;
import tools.packets.Wedding;

/**
 * @author Jvlaple
 * @author Ronan - major overhaul on Ring handling mechanics
 * @author Drago/Dragohe4rt - on Wishlist
 */
public final class RingActionHandler extends AbstractPacketHandler<BaseRingPacket> {
   private static int getBoxId(int useItemId) {
      return useItemId == 2240000 ? 4031357 : (useItemId == 2240001 ? 4031359 : (useItemId == 2240002 ? 4031361 : (useItemId == 2240003 ? 4031363 : (1112300 + (useItemId - 2240004)))));
   }

   private void sendEngageProposal(final MapleClient c, final String name, final int itemid) {
      final int newBoxId = getBoxId(itemid);
      final MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(name).orElse(null);
      final MapleCharacter source = c.getPlayer();

      // TODO: get the correct packet bytes for these popups
      if (source.isMarried()) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, "You're already married!");
         source.announce(Wedding.OnMarriageResult((byte) 0));
         return;
      } else if (source.getPartnerId() > 0) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, "You're already engaged!");
         source.announce(Wedding.OnMarriageResult((byte) 0));
         return;
      } else if (source.getMarriageItemId() > 0) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, "You're already engaging someone!");
         source.announce(Wedding.OnMarriageResult((byte) 0));
         return;
      } else if (target == null) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, "Unable to find " + name + " on this channel.");
         source.announce(Wedding.OnMarriageResult((byte) 0));
         return;
      } else if (target == source) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, "You can't engage yourself.");
         source.announce(Wedding.OnMarriageResult((byte) 0));
         return;
      } else if (target.getLevel() < 50) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, "You can only propose to someone level 50 or higher.");
         source.announce(Wedding.OnMarriageResult((byte) 0));
         return;
      } else if (source.getLevel() < 50) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, "You can only propose being level 50 or higher.");
         source.announce(Wedding.OnMarriageResult((byte) 0));
         return;
      } else if (!target.getMap().equals(source.getMap())) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, "Make sure your partner is on the same map!");
         source.announce(Wedding.OnMarriageResult((byte) 0));
         return;
      } else if (!source.haveItem(itemid) || itemid < 2240000 || itemid > 2240015) {
         source.announce(Wedding.OnMarriageResult((byte) 0));
         return;
      } else if (target.isMarried()) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, "The player is already married!");
         source.announce(Wedding.OnMarriageResult((byte) 0));
         return;
      } else if (target.getPartnerId() > 0 || target.getMarriageItemId() > 0) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, "The player is already engaged!");
         source.announce(Wedding.OnMarriageResult((byte) 0));
         return;
      } else if (target.haveWeddingRing()) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, "The player already holds a marriage ring...");
         source.announce(Wedding.OnMarriageResult((byte) 0));
         return;
      } else if (source.haveWeddingRing()) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, "You can't propose while holding a marriage ring!");
         source.announce(Wedding.OnMarriageResult((byte) 0));
         return;
      } else if (target.getGender() == source.getGender()) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, "You may only propose to a " + (source.getGender() == 1 ? "male" : "female") + "!");
         source.announce(Wedding.OnMarriageResult((byte) 0));
         return;
      } else if (!MapleInventoryManipulator.checkSpace(c, newBoxId, 1, "")) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.PINK_TEXT, "You don't have a ETC slot available right now!");
         source.announce(Wedding.OnMarriageResult((byte) 0));
         return;
      } else if (!MapleInventoryManipulator.checkSpace(target.getClient(), newBoxId + 1, 1, "")) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.PINK_TEXT, "The girl you proposed doesn't have a ETC slot available right now.");
         source.announce(Wedding.OnMarriageResult((byte) 0));
         return;
      }

      source.setMarriageItemId(itemid);
      target.announce(Wedding.OnMarriageRequest(source.getName(), source.getId()));
   }

   private static void eraseEngagementOffline(int characterId) {
      DatabaseConnection.getInstance().withConnection(connection -> eraseEngagementOffline(characterId, connection));
   }

   private static void eraseEngagementOffline(int characterId, Connection con) {
      CharacterAdministrator.getInstance().eraseEngagement(con, characterId);
   }

   private static void breakEngagementOffline(int characterId) {
      DatabaseConnection.getInstance().withConnection(connection -> {
         CharacterProvider.getInstance().getMarriageItem(connection, characterId)
               .ifPresent(itemId -> InventoryItemAdministrator.getInstance().expireItem(connection, itemId, characterId));
         eraseEngagementOffline(characterId, connection);
      });
   }

   private synchronized static void breakMarriage(MapleCharacter chr) {
      int partnerid = chr.getPartnerId();
      if (partnerid <= 0) {
         return;
      }

      chr.getClient().getWorldServer().deleteRelationship(chr.getId(), partnerid);
      MapleRingProcessor.getInstance().removeRing(chr.getMarriageRing());

      chr.getClient().getWorldServer().getPlayerStorage().getCharacterById(partnerid).ifPresentOrElse(partner -> {
         MessageBroadcaster.getInstance().sendServerNotice(partner, ServerNoticeType.PINK_TEXT, chr.getName() + " has decided to break up the marriage.");

         //partner.announce(Wedding.OnMarriageResult((byte) 0)); ok, how to gracefully unengage someone without the need to cc?
         partner.announce(Wedding.OnNotifyWeddingPartnerTransfer(0, 0));
         resetRingId(partner);
         partner.setPartnerId(-1);
         partner.setMarriageItemId(-1);
         partner.addMarriageRing(null);
      }, () -> eraseEngagementOffline(partnerid));

      MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "You have successfully break the marriage with " + CharacterProcessor.getInstance().getNameById(partnerid) + ".");

      //chr.announce(Wedding.OnMarriageResult((byte) 0));
      chr.announce(Wedding.OnNotifyWeddingPartnerTransfer(0, 0));
      resetRingId(chr);
      chr.setPartnerId(-1);
      chr.setMarriageItemId(-1);
      chr.addMarriageRing(null);
   }

   private static void resetRingId(MapleCharacter player) {
      int ringitemid = player.getMarriageRing().itemId();

      Item it = player.getInventory(MapleInventoryType.EQUIP).findById(ringitemid);
      if (it == null) {
         it = player.getInventory(MapleInventoryType.EQUIPPED).findById(ringitemid);
      }

      if (it != null) {
         Equip eqp = (Equip) it;
         eqp.setRingId(-1);
      }
   }

   private synchronized static void breakEngagement(MapleCharacter chr) {
      int partnerId = chr.getPartnerId();
      int marriageItemId = chr.getMarriageItemId();

      chr.getClient().getWorldServer().deleteRelationship(chr.getId(), partnerId);

      chr.getClient().getWorldServer().getPlayerStorage().getCharacterById(partnerId).ifPresentOrElse(partner -> {
         MessageBroadcaster.getInstance().sendServerNotice(partner, ServerNoticeType.PINK_TEXT, chr.getName() + " has decided to break up the engagement.");

         int partnerMarriageItemId = marriageItemId + ((chr.getGender() == 0) ? 1 : -1);
         if (partner.haveItem(partnerMarriageItemId)) {
            MapleInventoryManipulator.removeById(partner.getClient(), MapleInventoryType.ETC, partnerMarriageItemId, (short) 1, false, false);
         }

         //partner.announce(Wedding.OnMarriageResult((byte) 0)); ok, how to gracefully unengage someone without the need to cc?
         partner.announce(Wedding.OnNotifyWeddingPartnerTransfer(0, 0));
         partner.setPartnerId(-1);
         partner.setMarriageItemId(-1);
      }, () -> breakEngagementOffline(partnerId));

      if (chr.haveItem(marriageItemId)) {
         MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.ETC, marriageItemId, (short) 1, false, false);
      }
      MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "You have successfully break the engagement with " + CharacterProcessor.getInstance().getNameById(partnerId) + ".");

      //chr.announce(Wedding.OnMarriageResult((byte) 0));
      chr.announce(Wedding.OnNotifyWeddingPartnerTransfer(0, 0));
      chr.setPartnerId(-1);
      chr.setMarriageItemId(-1);
   }

   public static void breakMarriageRing(MapleCharacter chr, final int wItemId) {
      final MapleInventoryType type = MapleInventoryType.getByType((byte) (wItemId / 1000000));
      final Item wItem = chr.getInventory(type).findById(wItemId);
      final boolean weddingToken = (wItem != null && type == MapleInventoryType.ETC && wItemId / 10000 == 403);
      final boolean weddingRing = (wItem != null && wItemId / 10 == 111280);

      if (weddingRing) {
         if (chr.getPartnerId() > 0) {
            breakMarriage(chr);
         }

         chr.getMap().disappearingItemDrop(chr, chr, wItem, chr.getPosition());
      } else if (weddingToken) {
         if (chr.getPartnerId() > 0) {
            breakEngagement(chr);
         }

         chr.getMap().disappearingItemDrop(chr, chr, wItem, chr.getPosition());
      }
   }

   public static void giveMarriageRings(MapleCharacter player, MapleCharacter partner, int marriageRingId) {
      Pair<Integer, Integer> rings = MapleRingProcessor.getInstance().createRing(marriageRingId, player, partner);
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

      Item ringObj = ii.getEquipById(marriageRingId);
      Equip ringEqp = (Equip) ringObj;
      ringEqp.setRingId(rings.getLeft());
      player.addMarriageRing(MapleRingProcessor.getInstance().loadFromDb(rings.getLeft()));
      MapleInventoryManipulator.addFromDrop(player.getClient(), ringEqp, false, -1);
      player.broadcastMarriageMessage();

      ringObj = ii.getEquipById(marriageRingId);
      ringEqp = (Equip) ringObj;
      ringEqp.setRingId(rings.getRight());
      partner.addMarriageRing(MapleRingProcessor.getInstance().loadFromDb(rings.getRight()));
      MapleInventoryManipulator.addFromDrop(partner.getClient(), ringEqp, false, -1);
      partner.broadcastMarriageMessage();
   }

   @Override
   public Class<RingActionReader> getReaderClass() {
      return RingActionReader.class;
   }

   @Override
   public final void handlePacket(BaseRingPacket packet, MapleClient c) {
      if (packet instanceof EngagementProposalPacket) {
         sendEngageProposal(c, ((EngagementProposalPacket) packet).name(), ((EngagementProposalPacket) packet).itemId());
      } else if (packet instanceof CancelProposal) {
         cancelProposal(c);
      } else if (packet instanceof RespondToProposalPacket) {
         if (respondToProposal(c, ((RespondToProposalPacket) packet).accepted(),
               ((RespondToProposalPacket) packet).name(), ((RespondToProposalPacket) packet).itemId())) {
            return;
         }
      } else if (packet instanceof BreakEngagementPacket) {
         breakMarriageRing(c.getPlayer(), ((BreakEngagementPacket) packet).itemId());
      } else if (packet instanceof InviteToWeddingPacket) {
         if (inviteToWedding(c, ((InviteToWeddingPacket) packet).name(), ((InviteToWeddingPacket) packet).marriageId(),
               ((InviteToWeddingPacket) packet).slot())) {
            return;
         }
      } else if (packet instanceof OpenWeddingInvitationPacket) {
         if (openWeddingInvitation(c, ((OpenWeddingInvitationPacket) packet).slot(),
               ((OpenWeddingInvitationPacket) packet).invitationId())) {
            return;
         }
      } else if (packet instanceof HandleWishListPacket) {
         handleWishList(c, ((HandleWishListPacket) packet).items());
      } else {
         System.out.println("Unhandled RING_ACTION Mode: " + packet.toString());
      }
      c.announce(MaplePacketCreator.enableActions());
   }

   private void handleWishList(MapleClient c, String[] items) {
      try {
         // By Drago/Dragohe4rt
         // Groom and Bride's Wishlist

         MapleCharacter player = c.getPlayer();

         EventInstanceManager eim = player.getEventInstance();
         if (eim != null) {
            boolean isMarrying = (player.getId() == eim.getIntProperty("groomId") || player.getId() == eim.getIntProperty("brideId"));

            if (isMarrying) {
               StringBuilder wishlistItems = new StringBuilder();
               for (int i = 0; i < items.length; i++) {
                  wishlistItems.append(items[i]).append("\r\n");
               }

               String wlKey;
               if (player.getId() == eim.getIntProperty("groomId")) {
                  wlKey = "groomWishlist";
               } else {
                  wlKey = "brideWishlist";
               }

               if (eim.getProperty(wlKey).contentEquals("")) {
                  eim.setProperty(wlKey, wishlistItems.toString());
               }
            }
         }
      } catch (NumberFormatException ignored) {
      }
   }

   private boolean openWeddingInvitation(MapleClient c, byte slot, int invitationId) {
      if (invitationId == 4031406 || invitationId == 4031407) {
         Item item = c.getPlayer().getInventory(MapleInventoryType.ETC).getItem(slot);
         if (item == null || item.getItemId() != invitationId) {
            c.announce(MaplePacketCreator.enableActions());
            return true;
         }

         // collision case: most soon-to-come wedding will show up
         Pair<Integer, Integer> coupleId = c.getWorldServer().getWeddingCoupleForGuest(c.getPlayer().getId(), invitationId == 4031407);
         if (coupleId != null) {
            int groomId = coupleId.getLeft(), brideId = coupleId.getRight();
            c.announce(Wedding.sendWeddingInvitation(CharacterProcessor.getInstance().getNameById(groomId), CharacterProcessor.getInstance().getNameById(brideId)));
         }
      }
      return false;
   }

   private boolean inviteToWedding(MapleClient c, String name, int marriageId, byte slot) {
      int itemId;
      try {
         itemId = c.getPlayer().getInventory(MapleInventoryType.ETC).getItem(slot).getItemId();
      } catch (NullPointerException npe) {
         c.announce(MaplePacketCreator.enableActions());
         return true;
      }

      if ((itemId != 4031377 && itemId != 4031395) || !c.getPlayer().haveItem(itemId)) {
         c.announce(MaplePacketCreator.enableActions());
         return true;
      }

      String groom = c.getPlayer().getName(), bride = CharacterProcessor.getInstance().getNameById(c.getPlayer().getPartnerId());
      int guest = CharacterProcessor.getInstance().getIdByName(name);
      if (groom == null || bride == null || groom.equals("") || bride.equals("") || guest <= 0) {
         MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.PINK_TEXT, "Unable to find " + name + "!");
         return true;
      }

      World wserv = c.getWorldServer();
      Pair<Boolean, Boolean> registration = wserv.getMarriageQueuedLocation(marriageId);

      if (registration != null) {
         if (wserv.addMarriageGuest(marriageId, guest)) {
            boolean cathedral = registration.getLeft();
            int newItemId = cathedral ? 4031407 : 4031406;

            Channel cserv = c.getChannelServer();
            int resStatus = cserv.getWeddingReservationStatus(marriageId, cathedral);
            if (resStatus > 0) {
               long expiration = cserv.getWeddingTicketExpireTime(resStatus + 1);

               MapleCharacter guestChr = c.getWorldServer().getPlayerStorage().getCharacterById(guest).orElse(null);
               if (guestChr != null && MapleInventoryManipulator.checkSpace(guestChr.getClient(), newItemId, 1, "") && MapleInventoryManipulator.addById(guestChr.getClient(), newItemId, (short) 1, expiration)) {
                  MessageBroadcaster.getInstance().sendServerNotice(guestChr, ServerNoticeType.LIGHT_BLUE, "[Wedding] You've been invited to " + groom + " and " + bride + "'s Wedding!");
               } else {
                  if (guestChr != null && guestChr.isLoggedinWorld()) {
                     MessageBroadcaster.getInstance().sendServerNotice(guestChr, ServerNoticeType.LIGHT_BLUE, "[Wedding] You've been invited to " + groom + " and " + bride + "'s Wedding! Receive your invitation from Duey!");
                  } else {
                     NoteProcessor.getInstance().sendNote(name, c.getPlayer().getName(), "You've been invited to " + groom + " and " + bride + "'s Wedding! Receive your invitation from Duey!", (byte) 0);
                  }

                  Item weddingTicket = new Item(newItemId, (short) 0, (short) 1);
                  weddingTicket.setExpiration(expiration);

                  DueyProcessor.dueyCreatePackage(weddingTicket, 0, groom, guest);
               }
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.PINK_TEXT, "Wedding is already under way. You cannot invite any more guests for the event.");
            }
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.PINK_TEXT, "'" + name + "' is already invited for your marriage.");
         }
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.PINK_TEXT, "Invitation was not sent to '" + name + "'. Either the time for your marriage reservation already came or it was not found.");
      }

      c.getAbstractPlayerInteraction().gainItem(itemId, (short) -1);
      return false;
   }

   private boolean respondToProposal(MapleClient c, boolean accepted, String name, int id) {
      final MapleCharacter source = c.getWorldServer().getPlayerStorage().getCharacterByName(name).orElse(null);
      final MapleCharacter target = c.getPlayer();

      if (source == null) {
         target.announce(MaplePacketCreator.enableActions());
         return true;
      }

      final int itemid = source.getMarriageItemId();
      if (target.getPartnerId() > 0 || source.getId() != id || itemid <= 0 || !source.haveItem(itemid) || source.getPartnerId() > 0 || !source.isAlive() || !target.isAlive()) {
         target.announce(MaplePacketCreator.enableActions());
         return true;
      }

      if (accepted) {
         final int newItemId = getBoxId(itemid);
         if (!MapleInventoryManipulator.checkSpace(c, newItemId, 1, "") || !MapleInventoryManipulator.checkSpace(source.getClient(), newItemId, 1, "")) {
            target.announce(MaplePacketCreator.enableActions());
            return true;
         }

         try {
            MapleInventoryManipulator.removeById(source.getClient(), MapleInventoryType.USE, itemid, 1, false, false);

            int marriageId = c.getWorldServer().createRelationship(source.getId(), target.getId());
            source.setPartnerId(target.getId()); // engage them (new marriageitemid, partnerid for both)
            target.setPartnerId(source.getId());

            source.setMarriageItemId(newItemId);
            target.setMarriageItemId(newItemId + 1);

            MapleInventoryManipulator.addById(source.getClient(), newItemId, (short) 1);
            MapleInventoryManipulator.addById(c, (newItemId + 1), (short) 1);

            source.announce(Wedding.OnMarriageResult(marriageId, source, false));
            target.announce(Wedding.OnMarriageResult(marriageId, source, false));

            source.announce(Wedding.OnNotifyWeddingPartnerTransfer(target.getId(), target.getMapId()));
            target.announce(Wedding.OnNotifyWeddingPartnerTransfer(source.getId(), source.getMapId()));
         } catch (Exception e) {
            System.out.println("Error with engagement " + e.getMessage());
         }
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, "She has politely declined your engagement request.");
         source.announce(Wedding.OnMarriageResult((byte) 0));

         source.setMarriageItemId(-1);
      }
      return false;
   }

   private void cancelProposal(MapleClient c) {
      if (c.getPlayer().getMarriageItemId() / 1000000 != 4) {
         c.getPlayer().setMarriageItemId(-1);
      }
   }
}
