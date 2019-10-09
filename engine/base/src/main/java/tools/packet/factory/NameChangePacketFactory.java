package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.transfer.name.CheckNameChange;
import tools.packet.transfer.name.NameChangeCancel;
import tools.packet.transfer.name.NameChangeError;

public class NameChangePacketFactory extends AbstractPacketFactory {
   private static NameChangePacketFactory instance;

   public static NameChangePacketFactory getInstance() {
      if (instance == null) {
         instance = new NameChangePacketFactory();
      }
      return instance;
   }

   private NameChangePacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof NameChangeError) {
         return create(this::sendNameTransferRules, packetInput);
      } else if (packetInput instanceof CheckNameChange) {
         return create(this::sendNameTransferCheck, packetInput);
      } else if (packetInput instanceof NameChangeCancel) {
         return create(this::showNameChangeCancel, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   /*  1: name change already submitted
            2: name change within a month
            3: recently banned
            4: unknown error
        */
   protected byte[] sendNameTransferRules(NameChangeError packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CASHSHOP_CHECK_NAME_CHANGE_POSSIBLE_RESULT.getValue());
      mplew.writeInt(0);
      mplew.write(packet.error());
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   protected byte[] sendNameTransferCheck(CheckNameChange packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CASHSHOP_CHECK_NAME_CHANGE.getValue());
      //Send provided name back to client to add to temporary cache of checked & accepted names
      mplew.writeMapleAsciiString(packet.availableName());
      mplew.writeBool(!packet.canUseName());
      return mplew.getPacket();
   }

   protected byte[] showNameChangeCancel(NameChangeCancel packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CANCEL_NAME_CHANGE_RESULT.getValue());
      mplew.writeBool(packet.success());
      if (!packet.success()) {
         mplew.write(0);
      }
      //mplew.writeMapleAsciiString("Custom message."); //only if ^ != 0
      return mplew.getPacket();
   }
}