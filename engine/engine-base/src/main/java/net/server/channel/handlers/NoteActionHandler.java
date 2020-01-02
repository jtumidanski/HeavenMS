package net.server.channel.handlers;

import client.MapleClient;
import database.administrator.NoteAdministrator;
import database.provider.NoteProvider;
import client.processor.NoteProcessor;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.BaseNoteActionPacket;
import net.server.channel.packet.ClearNotePacket;
import net.server.channel.packet.SendNotePacket;
import net.server.channel.packet.reader.NoteActionReader;
import database.DatabaseConnection;
import tools.PacketCreator;
import tools.packet.cashshop.operation.ShowCashInventory;

public final class NoteActionHandler extends AbstractPacketHandler<BaseNoteActionPacket> {
   @Override
   public Class<NoteActionReader> getReaderClass() {
      return NoteActionReader.class;
   }

   @Override
   public void handlePacket(BaseNoteActionPacket packet, MapleClient client) {
      if (packet instanceof SendNotePacket && client.getPlayer().getCashShop().getAvailableNotes() > 0) {
         sendNote((SendNotePacket) packet, client);
      } else if (packet instanceof ClearNotePacket) {
         clearNotes((ClearNotePacket) packet, client);
      }
   }

   private void clearNotes(ClearNotePacket packet, MapleClient client) {
      int fame = DatabaseConnection.getInstance().withConnectionResult(connection -> {
         int fameCount = 0;
         for (int i = 0; i < packet.ids().length; i++) {
            int id = packet.ids()[i];
            fameCount += NoteProvider.getInstance().getFameForActiveNotes(connection, id).orElse(0);
            NoteAdministrator.getInstance().clearNote(connection, id);
         }
         return fameCount;
      }).orElse(0);
      if (fame > 0) {
         client.getPlayer().gainFame(fame);
      }
   }

   private void sendNote(SendNotePacket packet, MapleClient client) {
      if (client.getPlayer().getCashShop().isOpened()) {
         PacketCreator.announce(client, new ShowCashInventory(client.getAccID(), client.getPlayer().getCashShop().getInventory(), client.getPlayer().getStorage().getSlots(), client.getCharacterSlots()));
      }

      NoteProcessor.getInstance().sendNote(packet.characterName(), client.getPlayer().getName(), packet.message(), (byte) 1);
      client.getPlayer().getCashShop().decreaseNotes();
   }
}
