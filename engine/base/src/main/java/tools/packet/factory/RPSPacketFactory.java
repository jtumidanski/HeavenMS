package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.rps.OpenRPSNPC;
import tools.packet.rps.RPSMesoError;
import tools.packet.rps.RPSMode;
import tools.packet.rps.RPSSelection;

public class RPSPacketFactory extends AbstractPacketFactory {
   private static RPSPacketFactory instance;

   public static RPSPacketFactory getInstance() {
      if (instance == null) {
         instance = new RPSPacketFactory();
      }
      return instance;
   }

   private RPSPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof OpenRPSNPC) {
         return create(this::openRPSNPC, packetInput);
      } else if (packetInput instanceof RPSMesoError) {
         return create(this::rpsMesoError, packetInput);
      } else if (packetInput instanceof RPSSelection) {
         return create(this::rpsSelection, packetInput);
      } else if (packetInput instanceof RPSMode) {
         return create(this::rpsMode, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] openRPSNPC(OpenRPSNPC packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.RPS_GAME.getValue());
      mplew.write(8);// open npc
      mplew.writeInt(9000019);
      return mplew.getPacket();
   }

   protected byte[] rpsMesoError(RPSMesoError packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.RPS_GAME.getValue());
      mplew.write(0x06);
      if (packet.mesos() != -1) {
         mplew.writeInt(packet.mesos());
      }
      return mplew.getPacket();
   }

   protected byte[] rpsSelection(RPSSelection packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.RPS_GAME.getValue());
      mplew.write(0x0B);// 11l
      mplew.write(packet.selection());
      mplew.write(packet.answer());
      return mplew.getPacket();
   }

   protected byte[] rpsMode(RPSMode packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.RPS_GAME.getValue());
      mplew.write(packet.mode());
      return mplew.getPacket();
   }
}