package tools.packet.factory;

import net.opcodes.SendOpcode;
import server.MTSItemInfo;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.mtsoperation.GetNotYetSoldMTSInventory;
import tools.packet.mtsoperation.MTSConfirmBuy;
import tools.packet.mtsoperation.MTSConfirmSell;
import tools.packet.mtsoperation.MTSConfirmTransfer;
import tools.packet.mtsoperation.MTSFailBuy;
import tools.packet.mtsoperation.MTSTransferInventory;
import tools.packet.mtsoperation.MTSWantedListingOver;
import tools.packet.mtsoperation.SendMTS;
import tools.packet.mtsoperation.ShowMTSCash;

public class MTSOperationPacketFactory extends AbstractPacketFactory {
   private static MTSOperationPacketFactory instance;

   public static MTSOperationPacketFactory getInstance() {
      if (instance == null) {
         instance = new MTSOperationPacketFactory();
      }
      return instance;
   }

   private MTSOperationPacketFactory() {
      registry.setHandler(SendMTS.class, packet -> this.sendMTS((SendMTS) packet));
      registry.setHandler(ShowMTSCash.class, packet -> this.showMTSCash((ShowMTSCash) packet));
      registry.setHandler(MTSWantedListingOver.class, packet -> this.wantedListingOver((MTSWantedListingOver) packet));
      registry.setHandler(MTSConfirmSell.class, packet -> this.confirmSell((MTSConfirmSell) packet));
      registry.setHandler(MTSConfirmBuy.class, packet -> this.confirmBuy((MTSConfirmBuy) packet));
      registry.setHandler(MTSFailBuy.class, packet -> this.failBuy((MTSFailBuy) packet));
      registry.setHandler(MTSConfirmTransfer.class, packet -> this.confirmTransfer((MTSConfirmTransfer) packet));
      registry.setHandler(GetNotYetSoldMTSInventory.class, packet -> this.notYetSoldInv((GetNotYetSoldMTSInventory) packet));
      registry.setHandler(MTSTransferInventory.class, packet -> this.transferInventory((MTSTransferInventory) packet));
   }

   protected byte[] sendMTS(SendMTS packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MTS_OPERATION.getValue());
      mplew.write(0x15); //operation
      mplew.writeInt(packet.pages() * 16); //testing, change to 10 if fails
      mplew.writeInt(packet.items().size()); //number of items
      mplew.writeInt(packet.tab());
      mplew.writeInt(packet.theType());
      mplew.writeInt(packet.page());
      mplew.write(1);
      mplew.write(1);
      for (MTSItemInfo item : packet.items()) {
         addItemInfo(mplew, item.item(), true);
         mplew.writeInt(item.id()); //id
         mplew.writeInt(item.taxes()); //this + below = price
         mplew.writeInt(item.price()); //price
         mplew.writeInt(0);
         mplew.writeLong(getTime(item.endingDate()));
         mplew.writeMapleAsciiString(item.seller()); //account name (what was nexon thinking?)
         mplew.writeMapleAsciiString(item.seller()); //char name
         for (int j = 0; j < 28; j++) {
            mplew.write(0);
         }
      }
      mplew.write(1);
      return mplew.getPacket();
   }

   protected byte[] showMTSCash(ShowMTSCash packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MTS_OPERATION2.getValue());
      mplew.writeInt(packet.nxPrepaid());
      mplew.writeInt(packet.maplePoint());
      return mplew.getPacket();
   }

   protected byte[] wantedListingOver(MTSWantedListingOver packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MTS_OPERATION.getValue());
      mplew.write(0x3D);
      mplew.writeInt(packet.nx());
      mplew.writeInt(packet.items());
      return mplew.getPacket();
   }

   protected byte[] confirmSell(MTSConfirmSell packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MTS_OPERATION.getValue());
      mplew.write(0x1D);
      return mplew.getPacket();
   }

   protected byte[] confirmBuy(MTSConfirmBuy packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MTS_OPERATION.getValue());
      mplew.write(0x33);
      return mplew.getPacket();
   }

   protected byte[] failBuy(MTSFailBuy packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MTS_OPERATION.getValue());
      mplew.write(0x34);
      mplew.write(0x42);
      return mplew.getPacket();
   }

   protected byte[] confirmTransfer(MTSConfirmTransfer packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MTS_OPERATION.getValue());
      mplew.write(0x27);
      mplew.writeInt(packet.quantity());
      mplew.writeInt(packet.position());
      return mplew.getPacket();
   }

   protected byte[] notYetSoldInv(GetNotYetSoldMTSInventory packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MTS_OPERATION.getValue());
      mplew.write(0x23);
      mplew.writeInt(packet.items().size());
      if (!packet.items().isEmpty()) {
         for (MTSItemInfo item : packet.items()) {
            addItemInfo(mplew, item.item(), true);
            mplew.writeInt(item.id()); //id
            mplew.writeInt(item.taxes()); //this + below = price
            mplew.writeInt(item.price()); //price
            mplew.writeInt(0);
            mplew.writeLong(getTime(item.endingDate()));
            mplew.writeMapleAsciiString(item.seller()); //account name (what was nexon thinking?)
            mplew.writeMapleAsciiString(item.seller()); //char name
            for (int i = 0; i < 28; i++) {
               mplew.write(0);
            }
         }
      } else {
         mplew.writeInt(0);
      }
      return mplew.getPacket();
   }

   protected byte[] transferInventory(MTSTransferInventory packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MTS_OPERATION.getValue());
      mplew.write(0x21);
      mplew.writeInt(packet.items().size());
      if (!packet.items().isEmpty()) {
         for (MTSItemInfo item : packet.items()) {
            addItemInfo(mplew, item.item(), true);
            mplew.writeInt(item.id()); //id
            mplew.writeInt(item.taxes()); //taxes
            mplew.writeInt(item.price()); //price
            mplew.writeInt(0);
            mplew.writeLong(getTime(item.endingDate()));
            mplew.writeMapleAsciiString(item.seller()); //account name (what was nexon thinking?)
            mplew.writeMapleAsciiString(item.seller()); //char name
            for (int i = 0; i < 28; i++) {
               mplew.write(0);
            }
         }
      }
      mplew.write(0xD0 + packet.items().size());
      mplew.write(new byte[]{-1, -1, -1, 0});
      return mplew.getPacket();
   }
}