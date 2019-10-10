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
      registry.setHandler(SendMTS.class, packet -> create(SendOpcode.MTS_OPERATION, this::sendMTS, packet));
      registry.setHandler(ShowMTSCash.class, packet -> create(SendOpcode.MTS_OPERATION2, this::showMTSCash, packet));
      registry.setHandler(MTSWantedListingOver.class, packet -> create(SendOpcode.MTS_OPERATION, this::wantedListingOver, packet));
      registry.setHandler(MTSConfirmSell.class, packet -> create(SendOpcode.MTS_OPERATION, this::confirmSell, packet));
      registry.setHandler(MTSConfirmBuy.class, packet -> create(SendOpcode.MTS_OPERATION, this::confirmBuy, packet));
      registry.setHandler(MTSFailBuy.class, packet -> create(SendOpcode.MTS_OPERATION, this::failBuy, packet));
      registry.setHandler(MTSConfirmTransfer.class, packet -> create(SendOpcode.MTS_OPERATION, this::confirmTransfer, packet));
      registry.setHandler(GetNotYetSoldMTSInventory.class, packet -> create(SendOpcode.MTS_OPERATION, this::notYetSoldInv, packet));
      registry.setHandler(MTSTransferInventory.class, packet -> create(SendOpcode.MTS_OPERATION, this::transferInventory, packet));
   }

   protected void sendMTS(MaplePacketLittleEndianWriter writer, SendMTS packet) {
      writer.write(0x15); //operation
      writer.writeInt(packet.pages() * 16); //testing, change to 10 if fails
      writer.writeInt(packet.items().size()); //number of items
      writer.writeInt(packet.tab());
      writer.writeInt(packet.theType());
      writer.writeInt(packet.page());
      writer.write(1);
      writer.write(1);
      for (MTSItemInfo item : packet.items()) {
         addItemInfo(writer, item.item(), true);
         writer.writeInt(item.id()); //id
         writer.writeInt(item.taxes()); //this + below = price
         writer.writeInt(item.price()); //price
         writer.writeInt(0);
         writer.writeLong(getTime(item.endingDate()));
         writer.writeMapleAsciiString(item.seller()); //account name (what was nexon thinking?)
         writer.writeMapleAsciiString(item.seller()); //char name
         for (int j = 0; j < 28; j++) {
            writer.write(0);
         }
      }
      writer.write(1);
   }

   protected void showMTSCash(MaplePacketLittleEndianWriter writer, ShowMTSCash packet) {
      writer.writeInt(packet.nxPrepaid());
      writer.writeInt(packet.maplePoint());
   }

   protected void wantedListingOver(MaplePacketLittleEndianWriter writer, MTSWantedListingOver packet) {
      writer.write(0x3D);
      writer.writeInt(packet.nx());
      writer.writeInt(packet.items());
   }

   protected void confirmSell(MaplePacketLittleEndianWriter writer, MTSConfirmSell packet) {
      writer.write(0x1D);
   }

   protected void confirmBuy(MaplePacketLittleEndianWriter writer, MTSConfirmBuy packet) {
      writer.write(0x33);
   }

   protected void failBuy(MaplePacketLittleEndianWriter writer, MTSFailBuy packet) {
      writer.write(0x34);
      writer.write(0x42);
   }

   protected void confirmTransfer(MaplePacketLittleEndianWriter writer, MTSConfirmTransfer packet) {
      writer.write(0x27);
      writer.writeInt(packet.quantity());
      writer.writeInt(packet.position());
   }

   protected void notYetSoldInv(MaplePacketLittleEndianWriter writer, GetNotYetSoldMTSInventory packet) {
      writer.write(0x23);
      writer.writeInt(packet.items().size());
      if (!packet.items().isEmpty()) {
         for (MTSItemInfo item : packet.items()) {
            addItemInfo(writer, item.item(), true);
            writer.writeInt(item.id()); //id
            writer.writeInt(item.taxes()); //this + below = price
            writer.writeInt(item.price()); //price
            writer.writeInt(0);
            writer.writeLong(getTime(item.endingDate()));
            writer.writeMapleAsciiString(item.seller()); //account name (what was nexon thinking?)
            writer.writeMapleAsciiString(item.seller()); //char name
            for (int i = 0; i < 28; i++) {
               writer.write(0);
            }
         }
      } else {
         writer.writeInt(0);
      }
   }

   protected void transferInventory(MaplePacketLittleEndianWriter writer, MTSTransferInventory packet) {
      writer.write(0x21);
      writer.writeInt(packet.items().size());
      if (!packet.items().isEmpty()) {
         for (MTSItemInfo item : packet.items()) {
            addItemInfo(writer, item.item(), true);
            writer.writeInt(item.id()); //id
            writer.writeInt(item.taxes()); //taxes
            writer.writeInt(item.price()); //price
            writer.writeInt(0);
            writer.writeLong(getTime(item.endingDate()));
            writer.writeMapleAsciiString(item.seller()); //account name (what was nexon thinking?)
            writer.writeMapleAsciiString(item.seller()); //char name
            for (int i = 0; i < 28; i++) {
               writer.write(0);
            }
         }
      }
      writer.write(0xD0 + packet.items().size());
      writer.write(new byte[]{-1, -1, -1, 0});
   }
}