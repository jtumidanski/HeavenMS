/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    Copyleft (L) 2016 - 2018 RonanLana (HeavenMS)

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
package client.processor.npc;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import client.MapleCharacter;
import client.MapleClient;
import client.database.administrator.CharacterAdministrator;
import client.database.administrator.FredStorageAdministrator;
import client.database.administrator.InventoryItemAdministrator;
import client.database.administrator.NoteAdministrator;
import client.database.data.CharacterNameNote;
import client.database.data.CharacterWorldData;
import client.database.provider.FredStorageProvider;
import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.processor.CharacterProcessor;
import client.processor.NoteProcessor;
import net.server.Server;
import net.server.world.World;
import server.MapleItemInformationProvider;
import server.maps.MapleHiredMerchant;
import tools.DatabaseConnection;
import tools.FilePrinter;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.ServerNoticeType;
import tools.packet.fredrick.FredrickMessage;

/**
 * @author RonanLana - synchronization of Fredrick modules & operation results
 */
public class FredrickProcessor {

   private static int[] dailyReminders = new int[]{2, 5, 10, 15, 30, 60, 90, Integer.MAX_VALUE};

   private static byte canRetrieveFromFredrick(MapleCharacter chr, List<Pair<Item, MapleInventoryType>> items) {
      if (!MapleInventory.checkSpotsAndOwnership(chr, items)) {
         List<Integer> itemids = new LinkedList<>();
         for (Pair<Item, MapleInventoryType> it : items) {
            itemids.add(it.getLeft().id());
         }

         if (chr.canHoldUniques(itemids)) {
            return 0x22;
         } else {
            return 0x20;
         }
      }

      int netMeso = chr.getMerchantNetMeso();
      if (netMeso > 0) {
         if (!chr.canHoldMeso(netMeso)) {
            return 0x1F;
         }
      } else {
         if (chr.getMeso() < -1 * netMeso) {
            return 0x21;
         }
      }

      return 0x0;
   }

   public static int timestampElapsedDays(Timestamp then, long timeNow) {
      return (int) ((timeNow - then.getTime()) / (1000 * 60 * 60 * 24));
   }

   private static String fredrickReminderMessage(int daynotes) {
      String msg;

      if (daynotes < 4) {
         msg = "Hi customer! I am Fredrick, the Union Chief of the Hired Merchant Union. A reminder that " + dailyReminders[daynotes] + " days have passed since you used our service. Please reclaim your stored goods at FM Entrance.";
      } else {
         msg = "Hi customer! I am Fredrick, the Union Chief of the Hired Merchant Union. " + dailyReminders[daynotes] + " days have passed since you used our service. Consider claiming back the items before we move them away for refund.";
      }

      return msg;
   }

   public static void removeFredrickLog(int cid) {
      DatabaseConnection.getInstance().withConnection(connection -> removeFredrickLog(connection, cid));
   }

   private static void removeFredrickLog(EntityManager entityManager, int cid) {
      FredStorageAdministrator.getInstance().deleteForCharacter(entityManager, cid);
   }

   public static void insertFredrickLog(int cid) {
      DatabaseConnection.getInstance().withConnection(connection -> {
         removeFredrickLog(connection, cid);
         FredStorageAdministrator.getInstance().create(connection, cid);
      });
   }

   public static void removeFredrickReminders(int cid) {
      removeFredrickReminders(Collections.singletonList(new CharacterWorldData(cid, 0)));
   }

   private static void removeFredrickReminders(List<CharacterWorldData> expiredCids) {
      DatabaseConnection.getInstance().withConnection(connection ->
            expiredCids.stream()
                  .map(pair -> CharacterProcessor.getInstance().getNameById(pair.characterId()))
                  .filter(Objects::nonNull)
                  .forEach(name -> NoteAdministrator.getInstance().deleteWhereNamesLike(connection, "FREDRICK", name)));
   }

   public static void runFredrickSchedule() {
      DatabaseConnection.getInstance().withConnection(connection -> {
         List<CharacterWorldData> expiredCharacterIds = new LinkedList<>();
         List<CharacterNameNote> characterIdsToNotify = new LinkedList<>();
         long curTime = System.currentTimeMillis();

         FredStorageProvider.getInstance().get(connection).forEach(fredStorageData -> {
            int dayNotes = Math.min(dailyReminders.length - 1, fredStorageData.dayNotes());

            int elapsedDays = timestampElapsedDays(fredStorageData.timestamp(), curTime);
            if (elapsedDays > 100) {
               expiredCharacterIds.add(new CharacterWorldData(fredStorageData.characterId(), fredStorageData.worldId()));
            } else {
               int notifyDay = dailyReminders[dayNotes];

               if (elapsedDays >= notifyDay) {
                  do {
                     dayNotes++;
                     notifyDay = dailyReminders[dayNotes];
                  } while (elapsedDays >= notifyDay);
                  int inactivityDays = timestampElapsedDays(fredStorageData.lastLogoutTime(), curTime);
                  if (inactivityDays < 7 || dayNotes >= dailyReminders.length - 1) {  // don't spam inactive players
                     characterIdsToNotify.add(new CharacterNameNote(fredStorageData.characterId(), fredStorageData.name(), dayNotes));
                  }
               }
            }
         });

         if (!expiredCharacterIds.isEmpty()) {
            InventoryItemAdministrator.getInstance().deleteByCharacterAndTypeBatch(connection,
                  expiredCharacterIds.stream().map(pair -> new Pair<>(ItemFactory.MERCHANT.getValue(), pair.characterId())).collect(Collectors.toList()));

            CharacterAdministrator.getInstance().setMerchantMesosBatch(connection, expiredCharacterIds.stream().map(pair -> new Pair<>(pair.characterId(), 0)).collect(Collectors.toList()));

            expiredCharacterIds.forEach(pair -> {
               World world = Server.getInstance().getWorld(pair.worldId());
               if (world != null) {
                  world.getPlayerStorage().getCharacterById(pair.characterId()).ifPresent(character -> character.setMerchantMeso(0));
               }
            });

            removeFredrickReminders(expiredCharacterIds);
            FredStorageAdministrator.getInstance().deleteForCharacterBatch(connection, expiredCharacterIds.stream().map(CharacterWorldData::characterId).collect(Collectors.toList()));
         }

         if (!characterIdsToNotify.isEmpty()) {
            FredStorageAdministrator.getInstance().updateNotesBatch(connection,
                  characterIdsToNotify.stream().map(pair -> new Pair<>(pair.note(), pair.characterId())).collect(Collectors.toList()));
            characterIdsToNotify.forEach(pair -> {
               String msg = fredrickReminderMessage(pair.note() - 1);
               NoteProcessor.getInstance().sendNote(pair.characterName(), "FREDRICK", msg, (byte) 0);
            });
         }
      });
   }

   private static boolean deleteFredrickItems(int cid) {
      DatabaseConnection.getInstance().withConnection(connection ->
            InventoryItemAdministrator.getInstance().deleteForCharacterByType(connection, cid, ItemFactory.MERCHANT.getValue()));
      return true;
   }

   public static void fredrickRetrieveItems(MapleClient c) {     // thanks Gustav for pointing out the dupe on Fredrick handling
      if (c.tryAcquireClient()) {
         try {
            MapleCharacter chr = c.getPlayer();

            List<Pair<Item, MapleInventoryType>> items;
            items = ItemFactory.MERCHANT.loadItems(chr.getId(), false);

            byte response = canRetrieveFromFredrick(chr, items);
            if (response != 0) {
               PacketCreator.announce(chr, new FredrickMessage(response));
               return;
            }

            chr.withdrawMerchantMesos();

            if (deleteFredrickItems(chr.getId())) {
               MapleHiredMerchant merchant = chr.getHiredMerchant();

               if (merchant != null) {
                  merchant.clearItems();
               }

               for (Pair<Item, MapleInventoryType> it : items) {
                  Item item = it.getLeft();
                  MapleInventoryManipulator.addFromDrop(chr.getClient(), item, false);
                  String itemName = MapleItemInformationProvider.getInstance().getName(item.id());
                  FilePrinter.print(FilePrinter.FREDRICK + chr.getName() + ".txt", chr.getName() + " gained " + item.quantity() + " " + itemName + " (" + item.id() + ")");
               }

               PacketCreator.announce(chr, new FredrickMessage((byte) 0x1E));
               removeFredrickLog(chr.getId());
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "An unknown error has occurred.");
            }
         } finally {
            c.releaseClient();
         }
      }
   }
}
