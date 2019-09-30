/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.server.channel.handlers;


import java.util.Collections;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.inventory.manipulator.MapleKarmaManipulator;
import client.processor.ItemProcessor;
import constants.ItemConstants;
import constants.ServerConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.reader.WeddingReader;
import net.server.channel.packet.wedding.AddRegistryItemPacket;
import net.server.channel.packet.wedding.BaseWeddingPacket;
import net.server.channel.packet.wedding.OutOfRegistryPacket;
import net.server.channel.packet.wedding.TakeRegistryItemsPacket;
import server.MapleMarriage;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.stat.EnableActions;
import tools.packets.Wedding;

/**
 * @author By Drago/Dragohe4rt
 */
public final class WeddingHandler extends AbstractPacketHandler<BaseWeddingPacket> {
   @Override
   public Class<WeddingReader> getReaderClass() {
      return WeddingReader.class;
   }

   @Override
   public void handlePacket(BaseWeddingPacket packet, MapleClient client) {
      if (client.tryAcquireClient()) {
         try {
            MapleCharacter chr = client.getPlayer();
            if (packet instanceof AddRegistryItemPacket) {
               additem(client, chr, ((AddRegistryItemPacket) packet).slot(), ((AddRegistryItemPacket) packet).itemId(),
                     ((AddRegistryItemPacket) packet).quantity());
            } else if (packet instanceof TakeRegistryItemsPacket) {
               takeItems(client, chr, ((TakeRegistryItemsPacket) packet).itemPosition());
            } else if (packet instanceof OutOfRegistryPacket) {
               outOfRegistry(client);
            }
         } finally {
            client.releaseClient();
         }
      }
   }

   private void outOfRegistry(MapleClient client) {
      PacketCreator.announce(client, new EnableActions());
   }

   private void takeItems(MapleClient c, MapleCharacter chr, int itemPos) {
      MapleMarriage marriage = chr.getMarriageInstance();
      if (marriage != null) {
         Boolean groomWishlist = marriage.isMarriageGroom(chr);
         if (groomWishlist != null) {
            Item item = marriage.getGiftItem(c, groomWishlist, itemPos);
            if (item != null) {
               if (MapleInventory.checkSpot(chr, item)) {
                  marriage.removeGiftItem(groomWishlist, item);
                  marriage.saveGiftItemsToDb(c, groomWishlist, chr.getId());

                  MapleInventoryManipulator.addFromDrop(c, item, true);

                  c.announce(Wedding.OnWeddingGiftResult((byte) 0xF, marriage.getWishlistItems(groomWishlist), marriage.getGiftItems(c, groomWishlist)));
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "Free a slot on your inventory before collecting this item.");
                  c.announce(Wedding.OnWeddingGiftResult((byte) 0xE, marriage.getWishlistItems(groomWishlist), marriage.getGiftItems(c, groomWishlist)));
               }
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "You have already collected this item.");
               c.announce(Wedding.OnWeddingGiftResult((byte) 0xE, marriage.getWishlistItems(groomWishlist), marriage.getGiftItems(c, groomWishlist)));
            }
         }
      } else {
         List<Item> items = c.getAbstractPlayerInteraction().getUnclaimedMarriageGifts();
         try {
            Item item = items.get(itemPos);
            if (MapleInventory.checkSpot(chr, item)) {
               items.remove(itemPos);
               MapleMarriage.saveGiftItemsToDb(c, items, chr.getId());

               MapleInventoryManipulator.addFromDrop(c, item, true);
               c.announce(Wedding.OnWeddingGiftResult((byte) 0xF, Collections.singletonList(""), items));
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "Free a slot on your inventory before collecting this item.");
               c.announce(Wedding.OnWeddingGiftResult((byte) 0xE, Collections.singletonList(""), items));
            }
         } catch (Exception e) {
            MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "You have already collected this item.");
            c.announce(Wedding.OnWeddingGiftResult((byte) 0xE, Collections.singletonList(""), items));
         }
      }
   }

   private void additem(MapleClient client, MapleCharacter chr, short slot, int itemid, short quantity) {
      MapleMarriage marriage = client.getPlayer().getMarriageInstance();
      if (marriage != null) {
         try {
            boolean groomWishlist = marriage.giftItemToSpouse(chr.getId());
            String groomWishlistProp = "giftedItem" + (groomWishlist ? "G" : "B") + chr.getId();

            int giftCount = marriage.getIntProperty(groomWishlistProp);
            if (giftCount < ServerConstants.WEDDING_GIFT_LIMIT) {
               int cid = marriage.getIntProperty(groomWishlist ? "groomId" : "brideId");
               if (chr.getId() != cid) {   // cannot gift yourself
                  MapleCharacter spouse = marriage.getPlayerById(cid);
                  if (spouse != null) {
                     MapleInventoryType type = ItemConstants.getInventoryType(itemid);
                     MapleInventory chrInv = chr.getInventory(type);

                     chrInv.lockInventory();
                     try {
                        Item item = chrInv.getItem((byte) slot);
                        if (item != null) {
                           if (!ItemProcessor.getInstance().isUntradeable(item)) {
                              if (itemid == item.id() && quantity <= item.quantity()) {
                                 Item newItem = item.copy();

                                 marriage.addGiftItem(groomWishlist, newItem);
                                 MapleInventoryManipulator.removeFromSlot(client, type, slot, quantity, false, false);

                                 if (ServerConstants.USE_ENFORCE_MERCHANT_SAVE) {
                                    chr.saveCharToDB(false);
                                 }
                                 marriage.saveGiftItemsToDb(client, groomWishlist, cid);

                                 MapleKarmaManipulator.toggleKarmaFlagToUntradeable(newItem);
                                 marriage.setIntProperty(groomWishlistProp, giftCount + 1);

                                 client.announce(Wedding.OnWeddingGiftResult((byte) 0xB, marriage.getWishlistItems(groomWishlist), Collections.singletonList(newItem)));
                              }
                           } else {
                              client.announce(Wedding.OnWeddingGiftResult((byte) 0xE, marriage.getWishlistItems(groomWishlist), null));
                           }
                        }
                     } finally {
                        chrInv.unlockInventory();
                     }
                  } else {
                     client.announce(Wedding.OnWeddingGiftResult((byte) 0xE, marriage.getWishlistItems(groomWishlist), null));
                  }
               } else {
                  client.announce(Wedding.OnWeddingGiftResult((byte) 0xE, marriage.getWishlistItems(groomWishlist), null));
               }
            } else {
               client.announce(Wedding.OnWeddingGiftResult((byte) 0xC, marriage.getWishlistItems(groomWishlist), null));
            }
         } catch (NumberFormatException ignored) {
         }
      } else {
         PacketCreator.announce(client, new EnableActions());
      }
   }
}