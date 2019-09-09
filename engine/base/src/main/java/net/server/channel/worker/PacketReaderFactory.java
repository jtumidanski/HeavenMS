package net.server.channel.worker;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.server.MaplePacket;
import net.server.PacketReader;
import net.server.channel.packet.reader.AcceptToSReader;
import net.server.channel.packet.reader.AfterLoginReader;
import net.server.channel.packet.reader.ChangeChannelReader;
import net.server.channel.packet.reader.ChangeMapReader;
import net.server.channel.packet.reader.CharacterListRequestReader;
import net.server.channel.packet.reader.CharacterSelectedReader;
import net.server.channel.packet.reader.CharacterSelectedWithPicReader;
import net.server.channel.packet.reader.CheckCharacterNameReader;
import net.server.channel.packet.reader.CreateCharacterReader;
import net.server.channel.packet.reader.DeleteCharacterReader;
import net.server.channel.packet.reader.GeneralChatReader;
import net.server.channel.packet.reader.GrenadeEffectReader;
import net.server.channel.packet.reader.InventoryMergeReader;
import net.server.channel.packet.reader.ItemMoveReader;
import net.server.channel.packet.reader.LoginPasswordReader;
import net.server.channel.packet.reader.MesoDropReader;
import net.server.channel.packet.reader.NPCMoreTalkReader;
import net.server.channel.packet.reader.NPCShopReader;
import net.server.channel.packet.reader.NPCTalkReader;
import net.server.channel.packet.reader.PlayerLoggedInReader;
import net.server.channel.packet.reader.QuestActionReader;
import net.server.channel.packet.reader.RegisterPicReader;
import net.server.channel.packet.reader.RegisterPinReader;
import net.server.channel.packet.reader.ServerStatusRequestReader;
import net.server.channel.packet.reader.SetGenderReader;
import net.server.channel.packet.reader.TransferNameReader;
import net.server.channel.packet.reader.TransferNameResultReader;
import net.server.channel.packet.reader.TransferWorldReader;
import net.server.channel.packet.reader.UseCashItemReader;
import net.server.channel.packet.reader.ViewAllCharactersRegisterPicReader;
import net.server.channel.packet.reader.ViewAllCharactersSelectedReader;
import net.server.channel.packet.reader.ViewAllCharactersSelectedWithPicReader;
import net.server.channel.packet.reader.WhisperReader;
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
      readers.put(CreateCharacterReader.class, new CreateCharacterReader());
      readers.put(DeleteCharacterReader.class, new DeleteCharacterReader());
      readers.put(ViewAllCharactersSelectedReader.class, new ViewAllCharactersSelectedReader());
      readers.put(RegisterPinReader.class, new RegisterPinReader());
      readers.put(RegisterPicReader.class, new RegisterPicReader());
      readers.put(CharacterSelectedWithPicReader.class, new CharacterSelectedWithPicReader());
      readers.put(SetGenderReader.class, new SetGenderReader());
      readers.put(ViewAllCharactersSelectedWithPicReader.class, new ViewAllCharactersSelectedWithPicReader());
      readers.put(ViewAllCharactersRegisterPicReader.class, new ViewAllCharactersRegisterPicReader());
      readers.put(ServerStatusRequestReader.class, new ServerStatusRequestReader());

      readers.put(TransferNameReader.class, new TransferNameReader());
      readers.put(TransferNameResultReader.class, new TransferNameResultReader());
      readers.put(TransferWorldReader.class, new TransferNameReader());
      readers.put(ChangeChannelReader.class, new ChangeChannelReader());
      readers.put(GeneralChatReader.class, new GeneralChatReader());
      readers.put(WhisperReader.class, new WhisperReader());
      readers.put(NPCTalkReader.class, new NPCTalkReader());
      readers.put(NPCMoreTalkReader.class, new NPCMoreTalkReader());
      readers.put(QuestActionReader.class, new QuestActionReader());
      readers.put(GrenadeEffectReader.class, new GrenadeEffectReader());
      readers.put(NPCShopReader.class, new NPCShopReader());
      readers.put(InventoryMergeReader.class, new InventoryMergeReader());
      readers.put(ItemMoveReader.class, new ItemMoveReader());
      readers.put(MesoDropReader.class, new MesoDropReader());
      readers.put(PlayerLoggedInReader.class, new PlayerLoggedInReader());
      readers.put(ChangeMapReader.class, new ChangeMapReader());
      readers.put(CheckCharacterNameReader.class, new CheckCharacterNameReader());
      readers.put(UseCashItemReader.class, new UseCashItemReader());

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
