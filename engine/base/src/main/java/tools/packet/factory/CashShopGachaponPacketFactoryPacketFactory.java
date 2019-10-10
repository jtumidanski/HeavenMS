package tools.packet.factory;

import java.util.function.BiConsumer;
import java.util.function.Function;

import net.opcodes.SendOpcode;
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
      registry.setHandler(CashShopGachaponFailed.class, packet -> create(CashShopGachaponSubOp.FAILURE, this::onCashItemGachaponOpenFailed, packet));
      registry.setHandler(CashShopGachaponFailed.class, packet -> create(CashShopGachaponSubOp.SUCCESS, this::onCashGachaponOpenSuccess, packet));
   }

   protected <T extends PacketInput> byte[] create(CashShopGachaponSubOp subOp, BiConsumer<MaplePacketLittleEndianWriter, T> decorator, PacketInput packetInput, Integer size) {
      return create((Function<T, byte[]>) castInput -> {
         final MaplePacketLittleEndianWriter writer = newWriter(size);
         writer.writeShort(SendOpcode.CASHSHOP_CASH_ITEM_GACHAPON_RESULT.getValue());
         writer.write(subOp.getValue());
         decorator.accept(writer, castInput);
         return writer.getPacket();
      }, packetInput);
   }

   protected <T extends PacketInput> byte[] create(CashShopGachaponSubOp subOp, BiConsumer<MaplePacketLittleEndianWriter, T> decorator, PacketInput packetInput) {
      return create(subOp, decorator, packetInput, MaplePacketLittleEndianWriter.DEFAULT_SIZE);
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