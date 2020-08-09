package net.server.channel.handlers;

import javax.persistence.EntityManager;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.processor.CharacterProcessor;
import client.processor.MapleRingProcessor;
import client.processor.NoteProcessor;
import client.processor.npc.DueyProcessor;
import database.DatabaseConnection;
import database.administrator.CharacterAdministrator;
import database.administrator.InventoryItemAdministrator;
import database.provider.CharacterProvider;
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
import tools.I18nMessage;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.ServerNoticeType;
import tools.packet.stat.EnableActions;
import tools.packet.wedding.MarriageRequest;
import tools.packet.wedding.MarriageResult;
import tools.packet.wedding.MarriageResultError;
import tools.packet.wedding.WeddingInvitation;
import tools.packet.wedding.WeddingPartnerTransfer;

public final class RingActionHandler extends AbstractPacketHandler<BaseRingPacket> {
   private static int getBoxId(int useItemId) {
      return useItemId == 2240000 ? 4031357 : (useItemId == 2240001 ? 4031359 : (useItemId == 2240002 ? 4031361 : (useItemId == 2240003 ? 4031363 : (1112300 + (useItemId - 2240004)))));
   }

   private void sendEngageProposal(final MapleClient c, final String name, final int itemId) {
      final int newBoxId = getBoxId(itemId);
      final MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(name).orElse(null);
      final MapleCharacter source = c.getPlayer();

      // TODO: get the correct packet bytes for these popups
      if (source.isMarried()) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, I18nMessage.from("MARRIAGE_ALREADY_MARRIED"));
         PacketCreator.announce(source, new MarriageResultError((byte) 0));
         return;
      } else if (source.getPartnerId() > 0) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, I18nMessage.from("MARRIAGE_ALREADY_ENGAGED"));
         PacketCreator.announce(source, new MarriageResultError((byte) 0));
         return;
      } else if (source.getMarriageItemId() > 0) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, I18nMessage.from("MARRIAGE_ALREADY_ENGAGING"));
         PacketCreator.announce(source, new MarriageResultError((byte) 0));
         return;
      } else if (target == null) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, I18nMessage.from("MARRIAGE_UNABLE_TO_FIND_IN_CHANNEL"));
         PacketCreator.announce(source, new MarriageResultError((byte) 0));
         return;
      } else if (target == source) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, I18nMessage.from("MARRIAGE_CANNOT_ENGAGE_YOURSELF"));
         PacketCreator.announce(source, new MarriageResultError((byte) 0));
         return;
      } else if (target.getLevel() < 50) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, I18nMessage.from("MARRIAGE_PARTNER_LEVEL_REQUIREMENT"));
         PacketCreator.announce(source, new MarriageResultError((byte) 0));
         return;
      } else if (source.getLevel() < 50) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, I18nMessage.from("MARRIAGE_LEVEL_REQUIREMENT"));
         PacketCreator.announce(source, new MarriageResultError((byte) 0));
         return;
      } else if (!target.getMap().equals(source.getMap())) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, I18nMessage.from("MARRIAGE_SAME_MAP"));
         PacketCreator.announce(source, new MarriageResultError((byte) 0));
         return;
      } else if (!source.haveItem(itemId) || itemId < 2240000 || itemId > 2240015) {
         PacketCreator.announce(source, new MarriageResultError((byte) 0));
         return;
      } else if (target.isMarried()) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, I18nMessage.from("MARRIAGE_PARTNER_ALREADY_MARRIED"));
         PacketCreator.announce(source, new MarriageResultError((byte) 0));
         return;
      } else if (target.getPartnerId() > 0 || target.getMarriageItemId() > 0) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, I18nMessage.from("MARRIAGE_PARTNER_ALREADY_ENGAGED"));
         PacketCreator.announce(source, new MarriageResultError((byte) 0));
         return;
      } else if (target.haveWeddingRing()) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, I18nMessage.from("MARRIAGE_PARTNER_ALREADY_HAS_RING"));
         PacketCreator.announce(source, new MarriageResultError((byte) 0));
         return;
      } else if (source.haveWeddingRing()) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, I18nMessage.from("MARRIAGE_ALREADY_HAS_RING"));
         PacketCreator.announce(source, new MarriageResultError((byte) 0));
         return;
      } else if (target.getGender() == source.getGender()) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, I18nMessage.from("MARRIAGE_ONLY_PROPOSE_TO_OPPOSITE_GENDER"));
         PacketCreator.announce(source, new MarriageResultError((byte) 0));
         return;
      } else if (!MapleInventoryManipulator.checkSpace(c, newBoxId, 1, "")) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.PINK_TEXT, I18nMessage.from("MARRIAGE_NEED_ETC_SPACE"));
         PacketCreator.announce(source, new MarriageResultError((byte) 0));
         return;
      } else if (!MapleInventoryManipulator.checkSpace(target.getClient(), newBoxId + 1, 1, "")) {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.PINK_TEXT, I18nMessage.from("MARRIAGE_PARTNER_NEED_ETC_SPACE"));
         PacketCreator.announce(source, new MarriageResultError((byte) 0));
         return;
      }

      source.setMarriageItemId(itemId);
      PacketCreator.announce(target, new MarriageRequest(source.getName(), source.getId()));
   }

   private static void eraseEngagementOffline(int characterId) {
      DatabaseConnection.getInstance().withConnection(connection -> eraseEngagementOffline(characterId, connection));
   }

   private static void eraseEngagementOffline(int characterId, EntityManager entityManager) {
      CharacterAdministrator.getInstance().eraseEngagement(entityManager, characterId);
   }

   private static void breakEngagementOffline(int characterId) {
      DatabaseConnection.getInstance().withConnection(connection -> {
         CharacterProvider.getInstance().getMarriageItem(connection, characterId)
               .ifPresent(itemId -> InventoryItemAdministrator.getInstance().expireItem(connection, itemId, characterId));
         eraseEngagementOffline(characterId, connection);
      });
   }

   private synchronized static void breakMarriage(MapleCharacter chr) {
      int partnerId = chr.getPartnerId();
      if (partnerId <= 0) {
         return;
      }

      chr.getClient().getWorldServer().deleteRelationship(chr.getId(), partnerId);
      MapleRingProcessor.getInstance().removeRing(chr.getMarriageRing());

      chr.getClient().getWorldServer().getPlayerStorage().getCharacterById(partnerId).ifPresentOrElse(partner -> {
         MessageBroadcaster.getInstance().sendServerNotice(partner, ServerNoticeType.PINK_TEXT, I18nMessage.from("MARRIAGE_ENDED_MARRIAGE").with(chr.getName()));

         //partner.announce(Wedding.OnMarriageResult((byte) 0)); ok, how to gracefully remove engagement from someone without the need to cc?
         PacketCreator.announce(partner, new WeddingPartnerTransfer(0, 0));
         resetRingId(partner);
         partner.setPartnerId(-1);
         partner.setMarriageItemId(-1);
         partner.addMarriageRing(null);
      }, () -> eraseEngagementOffline(partnerId));

      MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("MARRIAGE_ENDED_MARRIAGE_LOOPBACK").with(CharacterProcessor.getInstance().getNameById(partnerId)));

      //chr.announce(Wedding.OnMarriageResult((byte) 0));
      PacketCreator.announce(chr, new WeddingPartnerTransfer(0, 0));
      resetRingId(chr);
      chr.setPartnerId(-1);
      chr.setMarriageItemId(-1);
      chr.addMarriageRing(null);
   }

   private static void resetRingId(MapleCharacter player) {
      int ringItemId = player.getMarriageRing().itemId();

      MapleInventoryType type = MapleInventoryType.EQUIP;
      Item it = player.getInventory(type).findById(ringItemId);
      if (it == null) {
         type = MapleInventoryType.EQUIPPED;
         it = player.getInventory(type).findById(ringItemId);
      }

      if (it != null) {
         it = Equip.newBuilder((Equip) it).setRingId(-1).build();
      }
      player.getInventory(type).update(it);
   }

   private synchronized static void breakEngagement(MapleCharacter chr) {
      int partnerId = chr.getPartnerId();
      int marriageItemId = chr.getMarriageItemId();

      chr.getClient().getWorldServer().deleteRelationship(chr.getId(), partnerId);

      chr.getClient().getWorldServer().getPlayerStorage().getCharacterById(partnerId).ifPresentOrElse(partner -> {
         MessageBroadcaster.getInstance().sendServerNotice(partner, ServerNoticeType.PINK_TEXT, I18nMessage.from("MARRIAGE_ENDED_ENGAGEMENT").with(chr.getName()));

         int partnerMarriageItemId = marriageItemId + ((chr.getGender() == 0) ? 1 : -1);
         if (partner.haveItem(partnerMarriageItemId)) {
            MapleInventoryManipulator.removeById(partner.getClient(), MapleInventoryType.ETC, partnerMarriageItemId, (short) 1, false, false);
         }

         //partner.announce(Wedding.OnMarriageResult((byte) 0)); ok, how to gracefully remove engagement from someone without the need to cc?
         PacketCreator.announce(partner, new WeddingPartnerTransfer(0, 0));
         partner.setPartnerId(-1);
         partner.setMarriageItemId(-1);
      }, () -> breakEngagementOffline(partnerId));

      if (chr.haveItem(marriageItemId)) {
         MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.ETC, marriageItemId, (short) 1, false, false);
      }
      MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("MARRIAGE_ENDED_ENGAGEMENT_LOOPBACK").with(CharacterProcessor.getInstance().getNameById(partnerId)));

      //chr.announce(Wedding.OnMarriageResult((byte) 0));
      PacketCreator.announce(chr, new WeddingPartnerTransfer(0, 0));
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

         chr.getMap().disappearingItemDrop(chr, chr, wItem, chr.position());
      } else if (weddingToken) {
         if (chr.getPartnerId() > 0) {
            breakEngagement(chr);
         }

         chr.getMap().disappearingItemDrop(chr, chr, wItem, chr.position());
      }
   }

   public static void giveMarriageRings(MapleCharacter player, MapleCharacter partner, int marriageRingId) {
      Pair<Integer, Integer> rings = MapleRingProcessor.getInstance().createRing(marriageRingId, player, partner);
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

      Equip ringObj = ii.getEquipById(marriageRingId);
      ringObj = Equip.newBuilder(ringObj).setRingId(rings.getLeft()).build();
      player.addMarriageRing(MapleRingProcessor.getInstance().loadFromDb(rings.getLeft()));
      MapleInventoryManipulator.addFromDrop(player.getClient(), ringObj, false, -1);
      player.broadcastMarriageMessage();

      ringObj = ii.getEquipById(marriageRingId);
      ringObj = Equip.newBuilder(ringObj).setRingId(rings.getRight()).build();
      partner.addMarriageRing(MapleRingProcessor.getInstance().loadFromDb(rings.getRight()));
      MapleInventoryManipulator.addFromDrop(partner.getClient(), ringObj, false, -1);
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
      PacketCreator.announce(c, new EnableActions());
   }

   private void handleWishList(MapleClient c, String[] items) {
      try {
         MapleCharacter player = c.getPlayer();

         EventInstanceManager eim = player.getEventInstance();
         if (eim != null) {
            boolean isMarrying = (player.getId() == eim.getIntProperty("groomId") || player.getId() == eim.getIntProperty("brideId"));

            if (isMarrying) {
               StringBuilder wishListItems = new StringBuilder();
               for (String item : items) {
                  wishListItems.append(item).append("\r\n");
               }

               String wlKey;
               if (player.getId() == eim.getIntProperty("groomId")) {
                  wlKey = "groomWishlist";
               } else {
                  wlKey = "brideWishlist";
               }

               if (eim.getProperty(wlKey).contentEquals("")) {
                  eim.setProperty(wlKey, wishListItems.toString());
               }
            }
         }
      } catch (NumberFormatException ignored) {
      }
   }

   private boolean openWeddingInvitation(MapleClient c, byte slot, int invitationId) {
      if (invitationId == 4031406 || invitationId == 4031407) {
         Item item = c.getPlayer().getInventory(MapleInventoryType.ETC).getItem(slot);
         if (item == null || item.id() != invitationId) {
            PacketCreator.announce(c, new EnableActions());
            return true;
         }

         // collision case: most soon-to-come wedding will show up
         Pair<Integer, Integer> coupleId = c.getWorldServer().getWeddingCoupleForGuest(c.getPlayer().getId(), invitationId == 4031407);
         if (coupleId != null) {
            int groomId = coupleId.getLeft(), brideId = coupleId.getRight();
            PacketCreator.announce(c, new WeddingInvitation(CharacterProcessor.getInstance().getNameById(groomId), CharacterProcessor.getInstance().getNameById(brideId)));
         }
      }
      return false;
   }

   private boolean inviteToWedding(MapleClient c, String name, int marriageId, byte slot) {
      int itemId;
      try {
         itemId = c.getPlayer().getInventory(MapleInventoryType.ETC).getItem(slot).id();
      } catch (NullPointerException npe) {
         PacketCreator.announce(c, new EnableActions());
         return true;
      }

      if ((itemId != 4031377 && itemId != 4031395) || !c.getPlayer().haveItem(itemId)) {
         PacketCreator.announce(c, new EnableActions());
         return true;
      }

      String groom = c.getPlayer().getName(), bride = CharacterProcessor.getInstance().getNameById(c.getPlayer().getPartnerId());
      int guest = CharacterProcessor.getInstance().getIdByName(name);
      if (groom == null || bride == null || groom.equals("") || bride.equals("") || guest <= 0) {
         MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("MARRIAGE_PARTNER_NOT_FOUND").with(name));
         return true;
      }

      World world = c.getWorldServer();
      Pair<Boolean, Boolean> registration = world.getMarriageQueuedLocation(marriageId);

      if (registration != null) {
         if (world.addMarriageGuest(marriageId, guest)) {
            boolean cathedral = registration.getLeft();
            int newItemId = cathedral ? 4031407 : 4031406;

            Channel channel = c.getChannelServer();
            int resStatus = channel.getWeddingReservationStatus(marriageId, cathedral);
            if (resStatus > 0) {
               long expiration = channel.getWeddingTicketExpireTime(resStatus + 1);

               MapleCharacter guestChr = c.getWorldServer().getPlayerStorage().getCharacterById(guest).orElse(null);
               if (guestChr != null && MapleInventoryManipulator.checkSpace(guestChr.getClient(), newItemId, 1, "") && MapleInventoryManipulator.addById(guestChr.getClient(), newItemId, (short) 1, expiration)) {
                  MessageBroadcaster.getInstance().sendServerNotice(guestChr, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("MARRIAGE_WEDDING_MESSAGE").with(groom, bride));
               } else {
                  if (guestChr != null && guestChr.isLoggedInWorld()) {
                     MessageBroadcaster.getInstance().sendServerNotice(guestChr, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("MARRIAGE_WEDDING_INVITE_MESSAGE").with(groom, bride));
                  } else {
                     NoteProcessor.getInstance().sendNote(name, c.getPlayer().getName(), "You've been invited to " + groom + " and " + bride + "'s Wedding! Receive your invitation from Duey!", (byte) 0);
                  }

                  Item weddingTicket = new Item(newItemId, (short) 0, (short) 1);
                  weddingTicket = Item.newBuilder(weddingTicket).setExpiration(expiration).build();
                  DueyProcessor.dueyCreatePackage(weddingTicket, 0, groom, guest);
               }
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("MARRIAGE_WEDDING_UNDERWAY_CANNOT_INVITE"));
            }
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("MARRIAGE_WEDDING_ALREADY_INVITED").with(name));
         }
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("MARRIAGE_WEDDING_INVITATION_FAIL").with(name));
      }

      c.getAbstractPlayerInteraction().gainItem(itemId, (short) -1);
      return false;
   }

   private boolean respondToProposal(MapleClient c, boolean accepted, String name, int id) {
      final MapleCharacter source = c.getWorldServer().getPlayerStorage().getCharacterByName(name).orElse(null);
      final MapleCharacter target = c.getPlayer();

      if (source == null) {
         PacketCreator.announce(target, new EnableActions());
         return true;
      }

      final int itemId = source.getMarriageItemId();
      if (target.getPartnerId() > 0 || source.getId() != id || itemId <= 0 || !source.haveItem(itemId) || source.getPartnerId() > 0 || !source.isAlive() || !target.isAlive()) {
         PacketCreator.announce(target, new EnableActions());
         return true;
      }

      if (accepted) {
         final int newItemId = getBoxId(itemId);
         if (!MapleInventoryManipulator.checkSpace(c, newItemId, 1, "") || !MapleInventoryManipulator.checkSpace(source.getClient(), newItemId, 1, "")) {
            PacketCreator.announce(target, new EnableActions());
            return true;
         }

         try {
            MapleInventoryManipulator.removeById(source.getClient(), MapleInventoryType.USE, itemId, 1, false, false);

            int marriageId = c.getWorldServer().createRelationship(source.getId(), target.getId());
            source.setPartnerId(target.getId()); // engage them (new marriageItemId, partnerId for both)
            target.setPartnerId(source.getId());

            source.setMarriageItemId(newItemId);
            target.setMarriageItemId(newItemId + 1);

            MapleInventoryManipulator.addById(source.getClient(), newItemId, (short) 1);
            MapleInventoryManipulator.addById(c, (newItemId + 1), (short) 1);

            PacketCreator.announce(source, new MarriageResult(marriageId, source, false));
            PacketCreator.announce(target, new MarriageResult(marriageId, source, false));

            PacketCreator.announce(source, new WeddingPartnerTransfer(target.getId(), target.getMapId()));
            PacketCreator.announce(target, new WeddingPartnerTransfer(source.getId(), source.getMapId()));
         } catch (Exception e) {
            System.out.println("Error with engagement " + e.getMessage());
         }
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(source, ServerNoticeType.POP_UP, I18nMessage.from("MARRIAGE_PARTNER_DECLINED"));
         PacketCreator.announce(source, new MarriageResultError((byte) 0));

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
