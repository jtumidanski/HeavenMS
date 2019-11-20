package tools.packet.factory;

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
      Handler.handle(EnableReport.class).decorate(this::enableReport).size(3).register(registry);
      Handler.handle(GetEnergy.class).decorate(this::getEnergy).register(registry);
      Handler.handle(DojoWarpUp.class).decorate(this::dojoWarpUp).register(registry);
      Handler.handle(SetNPCScriptable.class).decorate(this::setNPCScriptable).register(registry);
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