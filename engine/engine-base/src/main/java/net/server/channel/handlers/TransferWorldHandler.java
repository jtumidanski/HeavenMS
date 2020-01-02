package net.server.channel.handlers;

import java.sql.Timestamp;
import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import database.provider.WorldTransferProvider;
import config.YamlConfig;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.TransferWorldPacket;
import net.server.channel.packet.reader.TransferWorldReader;
import database.DatabaseConnection;
import tools.PacketCreator;
import tools.packet.cashshop.CashShopMessage;
import tools.packet.cashshop.operation.ShowCashShopMessage;
import tools.packet.stat.EnableActions;
import tools.packet.transfer.world.WorldTransferError;

public final class TransferWorldHandler extends AbstractPacketHandler<TransferWorldPacket> {
   @Override
   public Class<TransferWorldReader> getReaderClass() {
      return TransferWorldReader.class;
   }

   @Override
   public void handlePacket(TransferWorldPacket packet, MapleClient client) {
      if (!CashOperationHandler.checkBirthday(client, packet.birthday())) {
         PacketCreator.announce(client, new ShowCashShopMessage(CashShopMessage.CHECK_BIRTHDAY_CODE));
         PacketCreator.announce(client, new EnableActions());
         return;
      }

      MapleCharacter chr = client.getPlayer();
      if (!YamlConfig.config.server.ALLOW_CASHSHOP_WORLD_TRANSFER || Server.getInstance().getWorldsSize() <= 1) {
         PacketCreator.announce(client, new WorldTransferError(9));
         return;
      }
      int worldTransferError = chr.checkWorldTransferEligibility();
      if (worldTransferError != 0) {
         PacketCreator.announce(client, new WorldTransferError(worldTransferError));
         return;
      }

      Optional<Timestamp> completionTime = DatabaseConnection.getInstance().withConnectionResult(connection -> WorldTransferProvider.getInstance().getCompletionTimeByCharacterId(connection, chr.getId()));

      if (completionTime.isEmpty()) {
         PacketCreator.announce(client, new WorldTransferError(6));
         return;
      }

      if (completionTime.get().getTime() + YamlConfig.config.server.WORLD_TRANSFER_COOLDOWN > System.currentTimeMillis()) {
         PacketCreator.announce(client, new WorldTransferError(7));
         return;
      }

      PacketCreator.announce(client, new WorldTransferError(0));
   }
}