package net.server.channel.handlers;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import database.DatabaseConnection;
import database.provider.NameChangeProvider;
import config.YamlConfig;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.TransferNamePacket;
import net.server.channel.packet.reader.TransferNameReader;
import tools.PacketCreator;
import tools.packet.cashshop.CashShopMessage;
import tools.packet.cashshop.operation.ShowCashShopMessage;
import tools.packet.stat.EnableActions;
import tools.packet.transfer.name.NameChangeError;

public final class TransferNameHandler extends AbstractPacketHandler<TransferNamePacket> {
   @Override
   public Class<TransferNameReader> getReaderClass() {
      return TransferNameReader.class;
   }

   @Override
   public void handlePacket(TransferNamePacket packet, MapleClient client) {
      if (!CashOperationHandler.checkBirthday(client, packet.birthday())) {
         PacketCreator.announce(client, new ShowCashShopMessage(CashShopMessage.CHECK_BIRTHDAY_CODE));
         PacketCreator.announce(client, new EnableActions());
         return;
      }

      if (!YamlConfig.config.server.ALLOW_CASHSHOP_NAME_CHANGE) {
         PacketCreator.announce(client, new NameChangeError(4));
         return;
      }

      MapleCharacter chr = client.getPlayer();
      if (chr.getLevel() < 10) {
         PacketCreator.announce(client, new NameChangeError(4));
         return;
      } else if (client.getTempBanCalendar() != null && client.getTempBanCalendar().getTimeInMillis() + (30 * 24 * 60 * 60 * 1000) < Calendar.getInstance().getTimeInMillis()) {
         PacketCreator.announce(client, new NameChangeError(2));
         return;
      }

      //sql queries
      Optional<Timestamp> completionTime = DatabaseConnection.getInstance().withConnectionResult(connection -> NameChangeProvider.getInstance().getCompletionTimeByCharacterId(connection, chr.getId()).orElse(null));

      if (completionTime.isEmpty()) {
         PacketCreator.announce(client, new NameChangeError(1));
         return;
      }

      if (completionTime.get().getTime() + YamlConfig.config.server.NAME_CHANGE_COOLDOWN > System.currentTimeMillis()) {
         PacketCreator.announce(client, new NameChangeError(3));
         return;
      }

      PacketCreator.announce(client, new NameChangeError(0));

   }
}