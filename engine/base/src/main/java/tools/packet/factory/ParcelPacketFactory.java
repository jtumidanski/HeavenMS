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
      registry.setHandler(RemoveDueyItem.class, packet -> this.removeItemFromDuey((RemoveDueyItem) packet));
      registry.setHandler(DueyParcelReceived.class, packet -> this.sendDueyParcelReceived((DueyParcelReceived) packet));
      registry.setHandler(DueyParcelNotification.class, packet -> this.sendDueyParcelNotification((DueyParcelNotification) packet));
      registry.setHandler(SendDuey.class, packet -> this.sendDuey((SendDuey) packet));
   }

   protected byte[] removeItemFromDuey(RemoveDueyItem packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PARCEL.getValue());
      mplew.write(0x17);
      mplew.writeInt(packet.packageId());
      mplew.write(packet.remove() ? 3 : 4);
      return mplew.getPacket();
   }

   protected byte[] sendDueyParcelReceived(DueyParcelReceived packet) {    // thanks inhyuk
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PARCEL.getValue());
      mplew.write(0x19);
      mplew.writeMapleAsciiString(packet.from());
      mplew.writeBool(packet.quick());
      return mplew.getPacket();
   }

   protected byte[] sendDueyParcelNotification(DueyParcelNotification packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PARCEL.getValue());
      mplew.write(0x1B);
      mplew.writeBool(packet.quick());  // 0 : package received, 1 : quick delivery package
      return mplew.getPacket();
   }

   protected byte[] sendDuey(SendDuey packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PARCEL.getValue());
      mplew.write(packet.operation().getValue());
      if (packet.operation().getValue() == 8) {
         mplew.write(0);
         mplew.write(packet.packages().get().size());
         for (DueyPackage dp : packet.packages().get()) {
            mplew.writeInt(dp.packageId());
            mplew.writeAsciiString(dp.sender());
            for (int i = dp.sender().length(); i < 13; i++) {
               mplew.write(0);
            }

            mplew.writeInt(dp.mesos());
            mplew.writeLong(getTime(dp.sentTimeInMilliseconds()));

            String msg = dp.message();
            if (msg != null) {
               mplew.writeInt(1);
               mplew.writeAsciiString(msg);
               for (int i = msg.length(); i < 200; i++) {
                  mplew.write(0);
               }
            } else {
               mplew.writeInt(0);
               mplew.skip(200);
            }

            mplew.write(0);
            if (dp.item() != null) {
               mplew.write(1);
               addItemInfo(mplew, dp.item().get(), true);
            } else {
               mplew.write(0);
            }
         }
         mplew.write(0);
      }

      return mplew.getPacket();
   }
}