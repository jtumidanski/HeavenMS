package tools.packet.factory;

import java.util.function.BiConsumer;
import java.util.function.Function;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.cashshop.CashShopGachaponSubOp;
import tools.packet.cashshop.gachapon.CashShopGachaponFailed;
import tools.packet.cashshop.gachapon.CashShopGachaponSuccess;
import tools.packet.PacketInput;

public class CashShopGachaponPacketFactoryPacketFactory extends AbstractCashShopPacketFactory {
   private static CashShopGachaponPacketFactoryPacketFactory instance;

   public static CashShopGachaponPacketFactoryPacketFactory getInstance() {
      if (instance == null) {
         instance = new CashShopGachaponPacketFactoryPacketFactory();
      }
      return instance;
   }

   private CashShopGachaponPacketFactoryPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof CashShopGachaponFailed) {
         return create(CashShopGachaponSubOp.FAILURE, this::onCashItemGachaponOpenFailed, packetInput);
      } else if (packetInput instanceof CashShopGachaponSuccess) {
         return create(CashShopGachaponSubOp.SUCCESS, this::onCashGachaponOpenSuccess, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
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