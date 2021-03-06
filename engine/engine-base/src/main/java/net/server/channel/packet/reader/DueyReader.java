package net.server.channel.packet.reader;

import client.DueyAction;
import net.server.PacketReader;
import net.server.channel.packet.duey.BaseDueyPacket;
import net.server.channel.packet.duey.DueyClaimPackagePacket;
import net.server.channel.packet.duey.DueyReceiveItemPacket;
import net.server.channel.packet.duey.DueyRemovePackagePacket;
import net.server.channel.packet.duey.DueySendItemPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class DueyReader implements PacketReader<BaseDueyPacket> {
   @Override
   public BaseDueyPacket read(SeekableLittleEndianAccessor accessor) {
      byte operation = accessor.readByte();
      if (operation == DueyAction.TO_SERVER_RECV_ITEM.getValue()) {
         return readReceiveItem(operation);
      } else if (operation == DueyAction.TO_SERVER_SEND_ITEM.getValue()) {
         return readSendItem(accessor, operation);
      } else if (operation == DueyAction.TO_SERVER_REMOVE_PACKAGE.getValue()) {
         return readRemovePackage(accessor, operation);
      } else if (operation == DueyAction.TO_SERVER_CLAIM_PACKAGE.getValue()) {
         return readClaimPackage(accessor, operation);
      }
      return new BaseDueyPacket(operation);
   }

   private BaseDueyPacket readClaimPackage(SeekableLittleEndianAccessor accessor, byte operation) {
      int packageId = accessor.readInt();
      return new DueyClaimPackagePacket(operation, packageId);
   }

   private BaseDueyPacket readReceiveItem(byte operation) {
      return new DueyReceiveItemPacket(operation);
   }

   private BaseDueyPacket readSendItem(SeekableLittleEndianAccessor accessor, byte operation) {
      byte inventId = accessor.readByte();
      short itemPos = accessor.readShort();
      short amount = accessor.readShort();
      int mesos = accessor.readInt();
      String recipient = accessor.readMapleAsciiString();
      boolean quick = accessor.readByte() != 0;
      String message = quick ? accessor.readMapleAsciiString() : null;
      return new DueySendItemPacket(operation, inventId, itemPos, amount, mesos, recipient, quick, message);
   }

   private BaseDueyPacket readRemovePackage(SeekableLittleEndianAccessor accessor, byte operation) {
      int packageId = accessor.readInt();
      return new DueyRemovePackagePacket(operation, packageId);
   }
}
