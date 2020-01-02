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
import config.YamlConfig;
import constants.inventory.ItemConstants;
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
import tools.packet.wedding.WeddingGiftResult;

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
               addItem(client, chr, ((AddRegistryItemPacket) packet).slot(), ((AddRegistryItemPacket) packet).itemId(),
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
         Boolean groomWishList = marriage.isMarriageGroom(chr);
         if (groomWishList != null) {
            Item item = marriage.getGiftItem(c, groomWishList, itemPos);
            if (item != null) {
               if (MapleInventory.checkSpot(chr, item)) {
                  marriage.removeGiftItem(groomWishList, item);
                  marriage.saveGiftItemsToDb(c, groomWishList, chr.getId());

                  MapleInventoryManipulator.addFromDrop(c, item, true);

                  PacketCreator.announce(c, new WeddingGiftResult((byte) 0xF, marriage.getWishListItems(groomWishList), marriage.getGiftItems(c, groomWishList)));
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "Free a slot on your inventory before collecting this item.");
                  PacketCreator.announce(c, new WeddingGiftResult((byte) 0xE, marriage.getWishListItems(groomWishList), marriage.getGiftItems(c, groomWishList)));
               }
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "You have already collected this item.");
               PacketCreator.announce(c, new WeddingGiftResult((byte) 0xE, marriage.getWishListItems(groomWishList), marriage.getGiftItems(c, groomWishList)));
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
               PacketCreator.announce(c, new WeddingGiftResult((byte) 0xF, Collections.singletonList(""), items));
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "Free a slot on your inventory before collecting this item.");
               PacketCreator.announce(c, new WeddingGiftResult((byte) 0xE, Collections.singletonList(""), items));
            }
         } catch (Exception e) {
            MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "You have already collected this item.");
            PacketCreator.announce(c, new WeddingGiftResult((byte) 0xE, Collections.singletonList(""), items));
         }
      }
   }

   private void addItem(MapleClient client, MapleCharacter chr, short slot, int itemId, short quantity) {
      MapleMarriage marriage = client.getPlayer().getMarriageInstance();
      if (marriage != null) {
         try {
            boolean groomWishList = marriage.giftItemToSpouse(chr.getId());
            String groomWishListProp = "giftedItem" + (groomWishList ? "G" : "B") + chr.getId();

            int giftCount = marriage.getIntProperty(groomWishListProp);
            if (giftCount < YamlConfig.config.server.WEDDING_GIFT_LIMIT) {
               int cid = marriage.getIntProperty(groomWishList ? "groomId" : "brideId");
               if (chr.getId() != cid) {   // cannot gift yourself
                  MapleCharacter spouse = marriage.getPlayerById(cid);
                  if (spouse != null) {
                     MapleInventoryType type = ItemConstants.getInventoryType(itemId);
                     MapleInventory chrInv = chr.getInventory(type);

                     Item newItem = null;
                     chrInv.lockInventory();
                     try {
                        Item item = chrInv.getItem((byte) slot);
                        if (item != null) {
                           if (!ItemProcessor.getInstance().isUnableToBeTraded(item)) {
                              if (itemId == item.id() && quantity <= item.quantity()) {
                                 newItem = item.copy();

                                 marriage.addGiftItem(groomWishList, newItem);
                                 MapleInventoryManipulator.removeFromSlot(client, type, slot, quantity, false, false);

                                 marriage.saveGiftItemsToDb(client, groomWishList, cid);

                                 MapleKarmaManipulator.toggleKarmaFlagToUntradeable(newItem);
                                 marriage.setIntProperty(groomWishListProp, giftCount + 1);

                                 PacketCreator.announce(client, new WeddingGiftResult((byte) 0xB, marriage.getWishListItems(groomWishList), Collections.singletonList(newItem)));
                              }
                           } else {
                              PacketCreator.announce(client, new WeddingGiftResult((byte) 0xE, marriage.getWishListItems(groomWishList), null));
                           }
                        }
                     } finally {
                        chrInv.unlockInventory();
                     }

                     if (newItem != null) {
                        if (YamlConfig.config.server.USE_ENFORCE_MERCHANT_SAVE) chr.saveCharToDB(false);
                        marriage.saveGiftItemsToDb(client, groomWishList, cid);
                     }
                  } else {
                     PacketCreator.announce(client, new WeddingGiftResult((byte) 0xE, marriage.getWishListItems(groomWishList), null));
                  }
               } else {
                  PacketCreator.announce(client, new WeddingGiftResult((byte) 0xE, marriage.getWishListItems(groomWishList), null));
               }
            } else {
               PacketCreator.announce(client, new WeddingGiftResult((byte) 0xC, marriage.getWishListItems(groomWishList), null));
            }
         } catch (NumberFormatException ignored) {
         }
      } else {
         PacketCreator.announce(client, new EnableActions());
      }
   }
}