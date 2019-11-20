package tools.packet.factory;

import java.util.List;

import client.MapleClient;
import server.CashShop;
import tools.data.output.MaplePacketLittleEndianWriter;
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
      Handler.handle(SetITC.class).decorate(this::setMTS).register(registry);
      Handler.handle(SetCashShop.class).decorate(this::setCashShop).register(registry);
   }

   protected void setMTS(MaplePacketLittleEndianWriter writer, SetITC packet) {
      openCashShop(writer, packet.getClient(), true);
   }

   protected void setCashShop(MaplePacketLittleEndianWriter writer, SetCashShop packet) {
      openCashShop(writer, packet.getClient(), false);
   }

   protected void openCashShop(MaplePacketLittleEndianWriter writer, MapleClient c, boolean mts) {
      addCharacterInfo(writer, c.getPlayer());

      if (!mts) {
         writer.write(1);
      }

      writer.writeMapleAsciiString(c.getAccountName());
      if (mts) {
         writer.write(new byte[]{(byte) 0x88, 19, 0, 0, 7, 0, 0, 0, (byte) 0xF4, 1, 0, 0, (byte) 0x18, 0, 0, 0, (byte) 0xA8, 0, 0, 0, (byte) 0x70, (byte) 0xAA, (byte) 0xA7, (byte) 0xC5, (byte) 0x4E, (byte) 0xC1, (byte) 0xCA, 1});
      } else {
         writer.writeInt(0);
         List<CashShop.SpecialCashItem> lsci = CashShop.CashItemFactory.getSpecialCashItems();
         writer.writeShort(lsci.size());//Guess what
         for (CashShop.SpecialCashItem sci : lsci) {
            writer.writeInt(sci.getSN());
            writer.writeInt(sci.getModifier());
            writer.write(sci.getInfo());
         }
         writer.skip(121);

         List<List<Integer>> mostSellers = c.getWorldServer().getMostSellerCashItems();
         for (int i = 1; i <= 8; i++) {
            List<Integer> mostSellersTab = mostSellers.get(i);

            for (int j = 0; j < 2; j++) {
               for (Integer snid : mostSellersTab) {
                  writer.writeInt(i);
                  writer.writeInt(j);
                  writer.writeInt(snid);
               }
            }
         }

         writer.writeInt(0);
         writer.writeShort(0);
         writer.write(0);
         writer.writeInt(75);
      }
   }

}