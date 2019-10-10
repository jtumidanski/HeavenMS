package tools.packet.factory;

import net.opcodes.SendOpcode;
import server.DueyPackage;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.parcel.DueyParcelNotification;
import tools.packet.parcel.DueyParcelReceived;
import tools.packet.parcel.RemoveDueyItem;
import tools.packet.parcel.SendDuey;

public class ParcelPacketFactory extends AbstractPacketFactory {
   private static ParcelPacketFactory instance;

   public static ParcelPacketFactory getInstance() {
      if (instance == null) {
         instance = new ParcelPacketFactory();
      }
      return instance;
   }

   private ParcelPacketFactory() {
      registry.setHandler(RemoveDueyItem.class, packet -> create(SendOpcode.PARCEL, this::removeItemFromDuey, packet));
      registry.setHandler(DueyParcelReceived.class, packet -> create(SendOpcode.PARCEL, this::sendDueyParcelReceived, packet));
      registry.setHandler(DueyParcelNotification.class, packet -> create(SendOpcode.PARCEL, this::sendDueyParcelNotification, packet));
      registry.setHandler(SendDuey.class, packet -> create(SendOpcode.PARCEL, this::sendDuey, packet));
   }

   protected void removeItemFromDuey(MaplePacketLittleEndianWriter writer, RemoveDueyItem packet) {
      writer.write(0x17);
      writer.writeInt(packet.packageId());
      writer.write(packet.remove() ? 3 : 4);
   }

   protected void sendDueyParcelReceived(MaplePacketLittleEndianWriter writer, DueyParcelReceived packet) {    // thanks inhyuk
      writer.write(0x19);
      writer.writeMapleAsciiString(packet.from());
      writer.writeBool(packet.quick());
   }

   protected void sendDueyParcelNotification(MaplePacketLittleEndianWriter writer, DueyParcelNotification packet) {
      writer.write(0x1B);
      writer.writeBool(packet.quick());  // 0 : package received, 1 : quick delivery package
   }

   protected void sendDuey(MaplePacketLittleEndianWriter writer, SendDuey packet) {
      writer.write(packet.operation().getValue());
      if (packet.operation().getValue() == 8) {
         writer.write(0);
         writer.write(packet.packages().get().size());
         for (DueyPackage dp : packet.packages().get()) {
            writer.writeInt(dp.packageId());
            writer.writeAsciiString(dp.sender());
            for (int i = dp.sender().length(); i < 13; i++) {
               writer.write(0);
            }

            writer.writeInt(dp.mesos());
            writer.writeLong(getTime(dp.sentTimeInMilliseconds()));

            String msg = dp.message();
            if (msg != null) {
               writer.writeInt(1);
               writer.writeAsciiString(msg);
               for (int i = msg.length(); i < 200; i++) {
                  writer.write(0);
               }
            } else {
               writer.writeInt(0);
               writer.skip(200);
            }

            writer.write(0);
            if (dp.item() != null) {
               writer.write(1);
               addItemInfo(writer, dp.item().get(), true);
            } else {
               writer.write(0);
            }
         }
         writer.write(0);
      }
   }
}