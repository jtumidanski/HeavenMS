package tools.packet.factory;

import java.util.List;

import client.MapleClient;
import net.opcodes.SendOpcode;
import server.CashShop;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.SetCashShop;
import tools.packet.SetITC;

public class SpecialShopPacketFactory extends AbstractPacketFactory {
   private static SpecialShopPacketFactory instance;

   public static SpecialShopPacketFactory getInstance() {
      if (instance == null) {
         instance = new SpecialShopPacketFactory();
      }
      return instance;
   }

   private SpecialShopPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof SetITC) {
         return create(this::setMTS, packetInput);
      } else if (packetInput instanceof SetCashShop) {
         return create(this::setCashShop, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] setMTS(SetITC packet) {
      return openCashShop(packet.getClient(), true);
   }

   protected byte[] setCashShop(SetCashShop packet) {
      return openCashShop(packet.getClient(), false);
   }

   protected byte[] openCashShop(MapleClient c, boolean mts) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(mts ? SendOpcode.SET_ITC.getValue() : SendOpcode.SET_CASH_SHOP.getValue());

      addCharacterInfo(mplew, c.getPlayer());

      if (!mts) {
         mplew.write(1);
      }

      mplew.writeMapleAsciiString(c.getAccountName());
      if (mts) {
         mplew.write(new byte[]{(byte) 0x88, 19, 0, 0, 7, 0, 0, 0, (byte) 0xF4, 1, 0, 0, (byte) 0x18, 0, 0, 0, (byte) 0xA8, 0, 0, 0, (byte) 0x70, (byte) 0xAA, (byte) 0xA7, (byte) 0xC5, (byte) 0x4E, (byte) 0xC1, (byte) 0xCA, 1});
      } else {
         mplew.writeInt(0);
         List<CashShop.SpecialCashItem> lsci = CashShop.CashItemFactory.getSpecialCashItems();
         mplew.writeShort(lsci.size());//Guess what
         for (CashShop.SpecialCashItem sci : lsci) {
            mplew.writeInt(sci.getSN());
            mplew.writeInt(sci.getModifier());
            mplew.write(sci.getInfo());
         }
         mplew.skip(121);

         List<List<Integer>> mostSellers = c.getWorldServer().getMostSellerCashItems();
         for (int i = 1; i <= 8; i++) {
            List<Integer> mostSellersTab = mostSellers.get(i);

            for (int j = 0; j < 2; j++) {
               for (Integer snid : mostSellersTab) {
                  mplew.writeInt(i);
                  mplew.writeInt(j);
                  mplew.writeInt(snid);
               }
            }
         }

         mplew.writeInt(0);
         mplew.writeShort(0);
         mplew.write(0);
         mplew.writeInt(75);
      }
      return mplew.getPacket();
   }

}