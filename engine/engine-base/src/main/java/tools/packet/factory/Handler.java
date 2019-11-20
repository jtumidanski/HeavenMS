package tools.packet.factory;

import java.util.function.BiConsumer;
import java.util.function.Function;

import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;

public class Handler<T extends PacketInput> {
   private Class<T> type;

   private BiConsumer<MaplePacketLittleEndianWriter, T> decorator;

   private int size = MaplePacketLittleEndianWriter.DEFAULT_SIZE;

   protected Handler(Class<T> type) {
      this.type = type;
   }

   public Handler<T> decorate(BiConsumer<MaplePacketLittleEndianWriter, T> decorator) {
      this.decorator = decorator;
      return this;
   }

   public Handler<T> size(int size) {
      this.size = size;
      return this;
   }

   public void register(HandlerRegistry registry) {
      registry.setHandler(type, packet -> create(decorator, packet));
   }

   protected byte[] create(BiConsumer<MaplePacketLittleEndianWriter, T> decorator, PacketInput packetInput) {
      return create(decorator, packetInput, size);
   }

   protected byte[] create(BiConsumer<MaplePacketLittleEndianWriter, T> decorator, PacketInput packetInput, Integer size) {
      return create(castInput -> {
         final MaplePacketLittleEndianWriter writer = newWriter(size);
         writer.writeShort(castInput.opcode().getValue());
         if (decorator != null) {
            decorator.accept(writer, castInput);
         }
         return writer.getPacket();
      }, packetInput);
   }

   protected MaplePacketLittleEndianWriter newWriter(int size) {
      return new MaplePacketLittleEndianWriter(size);
   }

   protected byte[] create(Function<T, byte[]> creator, PacketInput packetInput) {
      return creator.apply((T) packetInput);
   }

   public static <T extends PacketInput> Handler<T> handle(Class<T> type) {
      return new Handler<>(type);
   }
}
