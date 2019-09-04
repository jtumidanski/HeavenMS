package net.server.channel.worker;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.server.MaplePacket;
import net.server.PacketReader;
import net.server.channel.packet.reader.AfterLoginReader;
import net.server.channel.packet.reader.CharacterListRequestReader;
import net.server.channel.packet.reader.CharacterSelectedReader;
import net.server.channel.packet.reader.AcceptToSReader;
import net.server.channel.packet.reader.CharacterSelectedWithPicReader;
import net.server.channel.packet.reader.CreateCharacterReader;
import net.server.channel.packet.reader.DeleteCharacterReader;
import net.server.channel.packet.reader.LoginPasswordReader;
import net.server.channel.packet.reader.RegisterPicReader;
import net.server.channel.packet.reader.RegisterPinReader;
import net.server.channel.packet.reader.SetGenderReader;
import net.server.channel.packet.reader.ViewAllCharactersRegisterPicReader;
import net.server.channel.packet.reader.ViewAllCharactersSelectedReader;
import net.server.channel.packet.reader.ViewAllCharactersSelectedWithPicReader;
import net.server.login.packet.SetGenderPacket;
import net.server.login.packet.ViewAllCharactersSelectedPacket;
import net.server.packet.reader.CustomReader;
import net.server.packet.reader.NoOpReader;
import tools.data.input.SeekableLittleEndianAccessor;

public class PacketReaderFactory {
   private static PacketReaderFactory ourInstance = new PacketReaderFactory();

   private static Map<Class<? extends PacketReader>, PacketReader> readers;

   public static PacketReaderFactory getInstance() {
      return ourInstance;
   }

   private PacketReaderFactory() {
      readers = new HashMap<>();
      readers.put(NoOpReader.class, new NoOpReader());
      readers.put(CustomReader.class, new CustomReader());

      readers.put(AcceptToSReader.class, new AcceptToSReader());
      readers.put(AfterLoginReader.class, new AfterLoginReader());
      readers.put(CharacterListRequestReader.class, new CharacterListRequestReader());
      readers.put(CharacterSelectedReader.class, new CharacterSelectedReader());
      readers.put(LoginPasswordReader.class, new LoginPasswordReader());
      readers.put(CreateCharacterReader.class, new CharacterListRequestReader());
      readers.put(DeleteCharacterReader.class, new DeleteCharacterReader());
      readers.put(ViewAllCharactersSelectedReader.class, new ViewAllCharactersSelectedReader());
      readers.put(RegisterPinReader.class, new RegisterPinReader());
      readers.put(RegisterPicReader.class, new RegisterPicReader());
      readers.put(CharacterSelectedWithPicReader.class, new CharacterSelectedWithPicReader());
      readers.put(SetGenderReader.class, new SetGenderReader());
      readers.put(ViewAllCharactersSelectedWithPicReader.class, new ViewAllCharactersSelectedWithPicReader());
      readers.put(ViewAllCharactersRegisterPicReader.class, new ViewAllCharactersRegisterPicReader());


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
