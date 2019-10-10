package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.DojoWarpUp;
import tools.packet.EnableReport;
import tools.packet.GetEnergy;
import tools.packet.SetNPCScriptable;

public class GenericPacketFactory extends AbstractPacketFactory {
   private static GenericPacketFactory instance;

   public static GenericPacketFactory getInstance() {
      if (instance == null) {
         instance = new GenericPacketFactory();
      }
      return instance;
   }

   private GenericPacketFactory() {
      registry.setHandler(EnableReport.class, packet -> create(SendOpcode.CLAIM_STATUS_CHANGED, this::enableReport, packet, 3));
      registry.setHandler(GetEnergy.class, packet -> create(SendOpcode.SESSION_VALUE, this::getEnergy, packet));
      registry.setHandler(DojoWarpUp.class, packet -> create(SendOpcode.DOJO_WARP_UP, this::dojoWarpUp, packet));
      registry.setHandler(SetNPCScriptable.class, packet -> create(SendOpcode.SET_NPC_SCRIPTABLE, this::setNPCScriptable, packet));
   }

   protected void enableReport(MaplePacketLittleEndianWriter writer, EnableReport packet) { // thanks to snow
      writer.write(1);
   }

   protected void getEnergy(MaplePacketLittleEndianWriter writer, GetEnergy packet) {
      writer.writeMapleAsciiString(packet.info());
      writer.writeMapleAsciiString(Integer.toString(packet.amount()));
   }

   protected void dojoWarpUp(MaplePacketLittleEndianWriter writer, DojoWarpUp packet) {
      writer.write(0);
      writer.write(6);
   }

   protected void setNPCScriptable(MaplePacketLittleEndianWriter writer, SetNPCScriptable packet) {  // thanks to GabrielSin
      writer.write(packet.descriptions().size());
      for (Pair<Integer, String> p : packet.descriptions()) {
         writer.writeInt(p.getLeft());
         writer.writeMapleAsciiString(p.getRight());
         writer.writeInt(0); // start time
         writer.writeInt(Integer.MAX_VALUE); // end time
      }
   }
}