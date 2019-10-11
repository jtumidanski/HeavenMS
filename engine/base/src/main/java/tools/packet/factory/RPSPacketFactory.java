package tools.packet.factory;

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
      Handler.handle(OpenRPSNPC.class).decorate(this::openRPSNPC).register(registry);
      Handler.handle(RPSMesoError.class).decorate(this::rpsMesoError).register(registry);
      Handler.handle(RPSSelection.class).decorate(this::rpsSelection).register(registry);
      Handler.handle(RPSMode.class).decorate(this::rpsMode).register(registry);
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