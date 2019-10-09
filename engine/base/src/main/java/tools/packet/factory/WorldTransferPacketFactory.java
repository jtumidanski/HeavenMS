package tools.packet.factory;

import java.util.List;

import constants.GameConstants;
import net.opcodes.SendOpcode;
import net.server.Server;
import net.server.world.World;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.transfer.world.WorldTransferCancel;
import tools.packet.transfer.world.WorldTransferError;

public class WorldTransferPacketFactory extends AbstractPacketFactory {
   private static WorldTransferPacketFactory instance;

   public static WorldTransferPacketFactory getInstance() {
      if (instance == null) {
         instance = new WorldTransferPacketFactory();
      }
      return instance;
   }

   private WorldTransferPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof WorldTransferError) {
         return create(this::sendWorldTransferRules, packetInput);
      } else if (packetInput instanceof WorldTransferCancel) {
         return create(this::showWorldTransferCancel, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   /*  1: cannot find char info,
            2: cannot transfer under 20,
            3: cannot send banned,
            4: cannot send married,
            5: cannot send guild leader,
            6: cannot send if account already requested transfer,
            7: cannot transfer within 30days,
            8: must quit family,
            9: unknown error
        */
   protected byte[] sendWorldTransferRules(WorldTransferError packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CASHSHOP_CHECK_TRANSFER_WORLD_POSSIBLE_RESULT.getValue());
      mplew.writeInt(0); //ignored
      mplew.write(packet.error());
      mplew.writeInt(0);
      mplew.writeBool(packet.error() == 0); //0 = ?, otherwise list servers
      if (packet.error() == 0) {
         List<World> worlds = Server.getInstance().getWorlds();
         mplew.writeInt(worlds.size());
         for (World world : worlds) {
            mplew.writeMapleAsciiString(GameConstants.WORLD_NAMES[world.getId()]);
         }
      }
      return mplew.getPacket();
   }

   protected byte[] showWorldTransferCancel(WorldTransferCancel packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CANCEL_TRANSFER_WORLD_RESULT.getValue());
      mplew.writeBool(packet.success());
      if (!packet.success()) {
         mplew.write(0);
      }
      //mplew.writeMapleAsciiString("Custom message."); //only if ^ != 0
      return mplew.getPacket();
   }
}