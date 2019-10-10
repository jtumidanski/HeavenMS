package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
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
      registry.setHandler(OpenRPSNPC.class, packet -> create(SendOpcode.RPS_GAME, this::openRPSNPC, packet));
      registry.setHandler(RPSMesoError.class, packet -> create(SendOpcode.RPS_GAME, this::rpsMesoError, packet));
      registry.setHandler(RPSSelection.class, packet -> create(SendOpcode.RPS_GAME, this::rpsSelection, packet));
      registry.setHandler(RPSMode.class, packet -> create(SendOpcode.RPS_GAME, this::rpsMode, packet));
   }

   protected void openRPSNPC(MaplePacketLittleEndianWriter writer, OpenRPSNPC packet) {
      writer.write(8);// open npc
      writer.writeInt(9000019);
   }

   protected void rpsMesoError(MaplePacketLittleEndianWriter writer, RPSMesoError packet) {
      writer.write(0x06);
      if (packet.mesos() != -1) {
         writer.writeInt(packet.mesos());
      }
   }

   protected void rpsSelection(MaplePacketLittleEndianWriter writer, RPSSelection packet) {
      writer.write(0x0B);// 11l
      writer.write(packet.selection());
      writer.write(packet.answer());
   }

   protected void rpsMode(MaplePacketLittleEndianWriter writer, RPSMode packet) {
      writer.write(packet.mode());
   }
}