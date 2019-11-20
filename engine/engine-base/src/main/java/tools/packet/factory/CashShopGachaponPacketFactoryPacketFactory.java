package tools.packet.factory;

import java.util.function.BiConsumer;

import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.cashshop.CashShopGachaponSubOp;
import tools.packet.cashshop.gachapon.CashShopGachaponFailed;
import tools.packet.cashshop.gachapon.CashShopGachaponSuccess;

public class CashShopGachaponPacketFactoryPacketFactory extends AbstractCashShopPacketFactory {
   private static CashShopGachaponPacketFactoryPacketFactory instance;

   public static CashShopGachaponPacketFactoryPacketFactory getInstance() {
      if (instance == null) {
         instance = new CashShopGachaponPacketFactoryPacketFactory();
      }
      return instance;
   }

   private CashShopGachaponPacketFactoryPacketFactory() {
      Handler.handle(CashShopGachaponFailed.class)
            .decorate((writer, packet) -> decorate(writer, packet, CashShopGachaponSubOp.FAILURE, this::onCashItemGachaponOpenFailed))
            .register(registry);
      Handler.handle(CashShopGachaponSuccess.class)
            .decorate((writer, packet) -> decorate(writer, packet, CashShopGachaponSubOp.SUCCESS, this::onCashGachaponOpenSuccess))
            .decorate(this::onCashGachaponOpenSuccess)
            .register(registry);
   }

   protected <T extends PacketInput> void decorate(MaplePacketLittleEndianWriter writer, T packet, CashShopGachaponSubOp subOp, BiConsumer<MaplePacketLittleEndianWriter, T> decorator) {
      writer.write(subOp.getValue());
      decorator.accept(writer, packet);
   }

   // Cash Shop Surprise packets found thanks to Arnah (Vertisy)
   protected void onCashItemGachaponOpenFailed(MaplePacketLittleEndianWriter writer, CashShopGachaponFailed packet) {
   }

   protected void onCashGachaponOpenSuccess(MaplePacketLittleEndianWriter writer, CashShopGachaponSuccess packet) {
      writer.writeLong(packet.sn());// sn of the box used
      writer.writeInt(packet.remainingBoxes());
      addCashItemInformation(writer, packet.item(), packet.accountId());
      writer.writeInt(packet.itemId());// the itemid of the liSN?
      writer.write(packet.selectedItemCount());// the total count now? o.O
      writer.writeBool(packet.jackpot());// "CashGachaponJackpot"
   }
}