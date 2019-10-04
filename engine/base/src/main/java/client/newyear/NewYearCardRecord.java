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
package client.newyear;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import client.MapleCharacter;
import client.database.administrator.NewYearAdministrator;
import client.database.provider.NewYearCardProvider;
import net.server.Server;
import server.TimerManager;
import tools.DatabaseConnection;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.NewYearCardResolution;

/**
 * @author Ronan - credits to Eric for showing the New Year opcodes and handler layout
 */
public class NewYearCardRecord {
   private int id;

   private int senderId;
   private String senderName;
   private boolean senderDiscardCard;

   private int receiverId;
   private String receiverName;
   private boolean receiverDiscardCard;
   private boolean receiverReceivedCard;

   private String stringContent;
   private long dateSent = 0;
   private long dateReceived = 0;

   private ScheduledFuture<?> sendTask = null;

   public NewYearCardRecord(int senderid, String sender, int receiverid, String receiver, String message) {
      this.id = -1;

      this.senderId = senderid;
      this.senderName = sender;
      this.senderDiscardCard = false;

      this.receiverId = receiverid;
      this.receiverName = receiver;
      this.receiverDiscardCard = false;
      this.receiverReceivedCard = false;

      this.stringContent = message;

      this.dateSent = System.currentTimeMillis();
      this.dateReceived = 0;
   }

   public static void saveNewYearCard(NewYearCardRecord newyear) {
      DatabaseConnection.getInstance().withConnection(connection -> newyear.id = NewYearAdministrator.getInstance().create(connection, newyear.getSenderId(), newyear.getSenderName(),
            newyear.getReceiverId(), newyear.getReceiverName(), newyear.stringContent,
            newyear.isSenderCardDiscarded(), newyear.isReceiverCardDiscarded(), newyear.isReceiverCardReceived(),
            newyear.getDateSent(), newyear.getDateReceived()));
   }

   public static void updateNewYearCard(NewYearCardRecord newyear) {
      newyear.receiverReceivedCard = true;
      newyear.dateReceived = System.currentTimeMillis();

      DatabaseConnection.getInstance().withConnection(connection -> NewYearAdministrator.getInstance().setReceived(connection,
            newyear.getId(), newyear.getDateReceived()));
   }

   public static NewYearCardRecord loadNewYearCard(int cardId) {
      NewYearCardRecord nyc = Server.getInstance().getNewYearCard(cardId);
      if (nyc != null) {
         return nyc;
      }

      Optional<NewYearCardRecord> newYearCardRecord = DatabaseConnection.getInstance().withConnectionResultOpt(connection -> NewYearCardProvider.getInstance().getById(connection, cardId));
      if (newYearCardRecord.isPresent()) {
         Server.getInstance().setNewYearCard(newYearCardRecord.get());
         return newYearCardRecord.get();
      } else {
         return null;
      }
   }

   public static void loadPlayerNewYearCards(MapleCharacter chr) {
      DatabaseConnection.getInstance().withConnection(connection ->
            NewYearCardProvider.getInstance().getBySenderOrReceiver(connection, chr.getId(), chr.getId())
                  .forEach(chr::addNewYearRecord));
   }

   public static void printNewYearRecords(MapleCharacter chr) {
      MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "New Years: " + chr.getNewYearRecords().size());

      for (NewYearCardRecord nyc : chr.getNewYearRecords()) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "-------------------------------");

         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Id: " + nyc.id);

         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Sender id: " + nyc.senderId);
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Sender name: " + nyc.senderName);
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Sender discard: " + nyc.senderDiscardCard);

         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Receiver id: " + nyc.receiverId);
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Receiver name: " + nyc.receiverName);
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Receiver discard: " + nyc.receiverDiscardCard);
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Received: " + nyc.receiverReceivedCard);

         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Message: " + nyc.stringContent);
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Date sent: " + nyc.dateSent);
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Date recv: " + nyc.dateReceived);
      }
   }

   private static void deleteNewYearCard(int id) {
      Server.getInstance().removeNewYearCard(id);
      DatabaseConnection.getInstance().withConnection(connection -> NewYearAdministrator.getInstance().deleteById(connection, id));
   }

   public static void removeAllNewYearCard(boolean send, MapleCharacter chr) {
      int cid = chr.getId();

      Set<NewYearCardRecord> set = new HashSet<>(chr.getNewYearRecords());
      for (NewYearCardRecord nyc : set) {
         if (send) {
            if (nyc.senderId == cid) {
               nyc.senderDiscardCard = true;
               nyc.receiverReceivedCard = false;

               chr.removeNewYearRecord(nyc);
               deleteNewYearCard(nyc.id);

               MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new NewYearCardResolution(chr, nyc, 0xE, 0));

               chr.getClient().getWorldServer().getPlayerStorage().getCharacterById(nyc.getReceiverId()).filter(MapleCharacter::isLoggedinWorld).ifPresent(other -> {
                  other.removeNewYearRecord(nyc);
                  MasterBroadcaster.getInstance().sendToAllInMap(other.getMap(), new NewYearCardResolution(other, nyc, 0xE, 0));
                  MessageBroadcaster.getInstance().sendServerNotice(other, ServerNoticeType.LIGHT_BLUE, "[New Year] " + chr.getName() + " threw away the New Year card.");
               });
            }
         } else {
            if (nyc.receiverId == cid) {
               nyc.receiverDiscardCard = true;
               nyc.receiverReceivedCard = false;

               chr.removeNewYearRecord(nyc);
               deleteNewYearCard(nyc.id);

               MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new NewYearCardResolution(chr, nyc, 0xE, 0));

               chr.getClient().getWorldServer().getPlayerStorage().getCharacterById(nyc.getSenderId())
                     .filter(MapleCharacter::isLoggedinWorld)
                     .ifPresent(other -> {
                        other.removeNewYearRecord(nyc);
                        MasterBroadcaster.getInstance().sendToAllInMap(other.getMap(), new NewYearCardResolution(other, nyc, 0xE, 0));
                        MessageBroadcaster.getInstance().sendServerNotice(other, ServerNoticeType.LIGHT_BLUE, "[New Year] " + chr.getName() + " threw away the New Year card.");
                     });
            }
         }
      }
   }

   public static void startPendingNewYearCardRequests() {
      DatabaseConnection.getInstance().withConnection(connection -> NewYearCardProvider.getInstance().getNotReceived(connection).forEach(newYearCard -> Server.getInstance().setNewYearCard(newYearCard)));
   }

   public void setExtraNewYearCardRecord(int id, boolean senderDiscardCard, boolean receiverDiscardCard, boolean receiverReceivedCard, long dateSent, long dateReceived) {
      this.id = id;
      this.senderDiscardCard = senderDiscardCard;
      this.receiverDiscardCard = receiverDiscardCard;
      this.receiverReceivedCard = receiverReceivedCard;

      this.dateSent = dateSent;
      this.dateReceived = dateReceived;
   }

   public int getId() {
      return this.id;
   }

   public void setId(int cardid) {
      this.id = cardid;
   }

   public int getSenderId() {
      return senderId;
   }

   public String getSenderName() {
      return senderName;
   }

   public boolean isSenderCardDiscarded() {
      return senderDiscardCard;
   }

   public int getReceiverId() {
      return receiverId;
   }

   public String getReceiverName() {
      return receiverName;
   }

   public boolean isReceiverCardDiscarded() {
      return receiverDiscardCard;
   }

   public boolean isReceiverCardReceived() {
      return receiverReceivedCard;
   }

   public String getMessage() {
      return stringContent;
   }

   public long getDateSent() {
      return dateSent;
   }

   public long getDateReceived() {
      return dateReceived;
   }

   public void startNewYearCardTask() {
      if (sendTask != null) {
         return;
      }

      sendTask = TimerManager.getInstance().register(new Runnable() {
         @Override
         public void run() {
            Server server = Server.getInstance();

            int world = server.getCharacterWorld(receiverId);
            if (world == -1) {
               sendTask.cancel(false);
               sendTask = null;

               return;
            }

            server.getWorld(world).getPlayerStorage().getCharacterById(receiverId)
                  .filter(MapleCharacter::isLoggedinWorld)
                  .ifPresent(target -> PacketCreator.announce(target, new NewYearCardResolution(target, NewYearCardRecord.this, 0xC, 0)));
         }
      }, 1000 * 60 * 60); //1 Hour
   }

   public void stopNewYearCardTask() {
      if (sendTask != null) {
         sendTask.cancel(false);
         sendTask = null;
      }
   }
}
