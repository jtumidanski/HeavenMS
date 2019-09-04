package net.server.channel.worker;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.server.MaplePacket;
import net.server.PacketReader;
import net.server.channel.packet.reader.CharacterSelectedReader;
import tools.data.input.SeekableLittleEndianAccessor;

public class PacketReaderFactory {
   private static PacketReaderFactory ourInstance = new PacketReaderFactory();

   private static Map<Class<? extends PacketReader>, PacketReader> readers;

   public static PacketReaderFactory getInstance() {
      return ourInstance;
   }

   private PacketReaderFactory() {
      readers = new HashMap<>();
      readers.put(CharacterSelectedReader.class, new CharacterSelectedReader());
   }

   public <T extends MaplePacket> Optional<T> read(Class<? extends PacketReader<T>> readerClass, SeekableLittleEndianAccessor accessor) {
      if (readers.containsKey(readerClass)) {
         PacketReader<T> reader = readers.get(readerClass);
         return Optional.of(reader.read(accessor));
      } else {
         return Optional.empty();
      }
   }
}
