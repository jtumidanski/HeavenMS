package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
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
      registry.setHandler(NameChangeError.class, packet -> create(SendOpcode.CASHSHOP_CHECK_NAME_CHANGE_POSSIBLE_RESULT, this::sendNameTransferRules, packet));
      registry.setHandler(CheckNameChange.class, packet -> create(SendOpcode.CASHSHOP_CHECK_NAME_CHANGE, this::sendNameTransferCheck, packet));
      registry.setHandler(NameChangeCancel.class, packet -> create(SendOpcode.CANCEL_NAME_CHANGE_RESULT, this::showNameChangeCancel, packet));
   }

   /*  1: name change already submitted
            2: name change within a month
            3: recently banned
            4: unknown error
        */
   protected void sendNameTransferRules(MaplePacketLittleEndianWriter writer, NameChangeError packet) {
      writer.writeInt(0);
      writer.write(packet.error());
      writer.writeInt(0);
   }

   protected void sendNameTransferCheck(MaplePacketLittleEndianWriter writer, CheckNameChange packet) {
      //Send provided name back to client to add to temporary cache of checked & accepted names
      writer.writeMapleAsciiString(packet.availableName());
      writer.writeBool(!packet.canUseName());
   }

   protected void showNameChangeCancel(MaplePacketLittleEndianWriter writer, NameChangeCancel packet) {
      writer.writeBool(packet.success());
      if (!packet.success()) {
         writer.write(0);
      }
      //writer.writeMapleAsciiString("Custom message."); //only if ^ != 0
   }
}