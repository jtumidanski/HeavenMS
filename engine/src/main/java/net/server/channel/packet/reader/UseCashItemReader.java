package net.server.channel.packet.reader;

import java.util.LinkedList;
import java.util.List;

import constants.items.UseableCashItems;
import net.server.PacketReader;
import net.server.channel.packet.AbstractUseCashItemPacket;
import net.server.channel.packet.UseApResetPacket;
import net.server.channel.packet.UseAvatarMegaphone;
import net.server.channel.packet.UseChalkboardPacket;
import net.server.channel.packet.UseCharacterCreatorPacket;
import net.server.channel.packet.UseCharacterEffectPacket;
import net.server.channel.packet.UseDueyPacket;
import net.server.channel.packet.UseExtendExpirationPacket;
import net.server.channel.packet.UseHammerPacket;
import net.server.channel.packet.UseIncubatorPacket;
import net.server.channel.packet.UseItemBagPacket;
import net.server.channel.packet.UseItemMegaphonePacket;
import net.server.channel.packet.UseItemTagPacket;
import net.server.channel.packet.UseJukeboxPacket;
import net.server.channel.packet.UseKitePacket;
import net.server.channel.packet.UseMapEffectPacket;
import net.server.channel.packet.UseMapleTvPacket;
import net.server.channel.packet.UseMegaphonePacket;
import net.server.channel.packet.UseMiuMiuPacket;
import net.server.channel.packet.UseNameChangePacket;
import net.server.channel.packet.UseNotePacket;
import net.server.channel.packet.UseOwlSearchPacket;
import net.server.channel.packet.UsePetConsumePacket;
import net.server.channel.packet.UsePetNameChangePacket;
import net.server.channel.packet.UseScissorsKarmaPacket;
import net.server.channel.packet.UseSealingLockPacket;
import net.server.channel.packet.UseSpResetPacket;
import net.server.channel.packet.UseSuperMegaphonePacket;
import net.server.channel.packet.UseTeleportRockPacket;
import net.server.channel.packet.UseTripleMegaphonePacket;
import net.server.channel.packet.UseUnhandledPacket;
import net.server.channel.packet.UseVegaSpellPacket;
import net.server.channel.packet.UseWorldChangePacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class UseCashItemReader implements PacketReader<AbstractUseCashItemPacket> {
   @Override
   public AbstractUseCashItemPacket read(SeekableLittleEndianAccessor accessor) {
      short position = accessor.readShort();
      int itemId = accessor.readInt();

      switch (UseableCashItems.Types.getByItemId(itemId)) {
         case TELEPORTERS:
            return readTeleportRock(accessor, position, itemId);
         case STAT_MODIFIERS:
            return readApOrSpResetPacket(accessor, position, itemId);
         case ITEM_AUGMENTERS:
            return readItemAugmenter(accessor, position, itemId);
         case BROADCASTERS:
            return readBroadcastTool(accessor, position, itemId);
         case KITES:
            return readKite(accessor, position, itemId);
         case NOTES:
            return readNote(accessor, position, itemId);
         case JUKE_BOXES:
            return new UseJukeboxPacket(position, itemId);
         case MAP_EFFECTS:
            return readMapEffect(accessor, position, itemId);
         case PET_NAME_CHANGE:
            return readPetNameChange(accessor, position, itemId);
         case ITEM_BAGS:
            return new UseItemBagPacket(position, itemId);
         case OWL_SEARCH:
            return readOwlSearch(accessor, position, itemId);
         case PET_CONSUME:
            return new UsePetConsumePacket(position, itemId);
         case CHARACTER_EFFECT:
            return new UseCharacterEffectPacket(position, itemId);
         case DUEY:
            return new UseDueyPacket(position, itemId);
         case CHALKBOARD:
            return new UseChalkboardPacket(position, itemId, accessor.readMapleAsciiString());
         case AVATAR_BROADCASTERS:
            return readAvatarMegaphone(accessor, position, itemId);
         case CHARACTER_MODIFIERS:
            return readNameOrWorldChange(accessor, position, itemId);
         case CHARACTER_CREATORS:
            return readCharacterCreator(accessor, position, itemId);
         case MIU_MIU:
            return new UseMiuMiuPacket(position, itemId);
         case EXPIRATION_EXTENDERS:
            return new UseExtendExpirationPacket(position, itemId);
         case SCISSORS_OF_KARMA:
            return readScissorsOfKarma(accessor, position, itemId);
         case HAMMER:
            return readHammer(accessor, position, itemId);
         case VEGA_SPELLS:
            return readVegaSpell(accessor, position, itemId);
         default:
            return new UseUnhandledPacket(position, itemId, accessor.toString());
      }
   }

   private AbstractUseCashItemPacket readHammer(SeekableLittleEndianAccessor accessor, short position, int itemId) {
      accessor.readInt();
      int itemSlot = accessor.readInt();
      accessor.readInt();
      return new UseHammerPacket(position, itemId, (short) itemSlot);
   }

   private AbstractUseCashItemPacket readVegaSpell(SeekableLittleEndianAccessor accessor, short position, int itemId) {
      return new UseVegaSpellPacket(position, itemId, accessor.readInt() != 1, (byte) accessor.readInt(), accessor.readInt() != 2, (byte) accessor.readInt());
   }

   private AbstractUseCashItemPacket readScissorsOfKarma(SeekableLittleEndianAccessor accessor, short position, int itemId) {
      return new UseScissorsKarmaPacket(position, itemId, (byte) accessor.readInt(), (short) accessor.readInt());
   }

   private AbstractUseCashItemPacket readCharacterCreator(SeekableLittleEndianAccessor accessor, short position, int itemId) {
      return new UseCharacterCreatorPacket(position, itemId, accessor.readMapleAsciiString(), accessor.readInt(),
            accessor.readInt(), accessor.readInt(), accessor.readInt(), accessor.readInt(), accessor.readInt(),
            accessor.readInt());
   }

   private AbstractUseCashItemPacket readNameOrWorldChange(SeekableLittleEndianAccessor accessor, short position, int itemId) {
      accessor.readByte();
      accessor.readInt();
      switch (UseableCashItems.CharacterModifiers.getByItemId(itemId)) {
         case NAME_CHANGE:
            return new UseNameChangePacket(position, itemId);
         case WORLD_CHANGE:
            return new UseWorldChangePacket(position, itemId);
      }
      return null;
   }

   private AbstractUseCashItemPacket readAvatarMegaphone(SeekableLittleEndianAccessor accessor, short position, int itemId) {
      String[] strLines = new String[4];
      for (int i = 0; i < 4; i++) {
         strLines[i] = accessor.readMapleAsciiString();
      }
      return new UseAvatarMegaphone(position, itemId, strLines, accessor.readByte() != 0);
   }

   private AbstractUseCashItemPacket readOwlSearch(SeekableLittleEndianAccessor accessor, short position, int itemId) {
      return new UseOwlSearchPacket(position, itemId, accessor.readInt());
   }

   private AbstractUseCashItemPacket readPetNameChange(SeekableLittleEndianAccessor accessor, short position, int itemId) {
      return new UsePetNameChangePacket(position, itemId, accessor.readMapleAsciiString());
   }

   private AbstractUseCashItemPacket readMapEffect(SeekableLittleEndianAccessor accessor, short position, int itemId) {
      return new UseMapEffectPacket(position, itemId, accessor.readMapleAsciiString());
   }

   private AbstractUseCashItemPacket readNote(SeekableLittleEndianAccessor accessor, short position, int itemId) {
      return new UseNotePacket(position, itemId, accessor.readMapleAsciiString(), accessor.readMapleAsciiString());
   }

   private AbstractUseCashItemPacket readKite(SeekableLittleEndianAccessor accessor, short position, int itemId) {
      return new UseKitePacket(position, itemId, accessor.readMapleAsciiString());
   }

   private AbstractUseCashItemPacket readBroadcastTool(SeekableLittleEndianAccessor accessor, short position, int itemId) {
      switch (UseableCashItems.BroadcasterCategories.getByItemId(itemId)) {
         case CHEAP_MEGAPHONE:
            System.out.println("Unhandled Item: " + itemId + "\n");
            return null;
         case MEGAPHONE:
            return new UseMegaphonePacket(position, itemId, accessor.readMapleAsciiString());
         case SUPER_MEGAPHONE:
            return new UseSuperMegaphonePacket(position, itemId, accessor.readMapleAsciiString(), accessor.readByte() != 0);
         case HEART_MEGAPHONE:
         case SKULL_MEGAPHONE:
            System.out.println("Unhandled Item: " + itemId + "\n");
            break;
         case MAPLE_TV:
            return readMapleTv(accessor, position, itemId);
         case ITEM_MEGAPHONE:
            return readItemMegaphone(accessor, position, itemId);
         case TRIPLE_MEGAPHONE:
            return readTripleMegaphone(accessor, position, itemId);
      }
      return null;
   }

   private AbstractUseCashItemPacket readItemAugmenter(SeekableLittleEndianAccessor accessor, short position, int itemId) {
      switch (UseableCashItems.ItemAugmenters.getByItemId(itemId)) {
         case ITEM_TAG:
            return readItemTag(accessor, position, itemId);
         case ITEM_GUARD:
         case ITEM_GUARD_7:
         case ITEM_GUARD_30:
         case ITEM_GUARD_90:
         case ITEM_GUARD_365:
            return readItemGuard(accessor, position, itemId);
         case INCUBATOR:
            return readIncubator(accessor, position, itemId);
      }
      return null;
   }

   private AbstractUseCashItemPacket readIncubator(SeekableLittleEndianAccessor accessor, short position, int itemId) {
      byte type = (byte) accessor.readInt();
      short equipSlot = (short) accessor.readInt();
      accessor.readInt(); // time stamp
      return new UseIncubatorPacket(position, itemId, type, equipSlot);
   }

   private AbstractUseCashItemPacket readItemGuard(SeekableLittleEndianAccessor accessor, short position, int itemId) {
      byte type = (byte) accessor.readInt();
      short equipSlot = (short) accessor.readInt();
      accessor.readInt(); // time stamp
      return new UseSealingLockPacket(position, itemId, type, equipSlot);
   }

   private AbstractUseCashItemPacket readItemTag(SeekableLittleEndianAccessor accessor, short position, int itemId) {
      int equipSlot = accessor.readShort();
      accessor.readInt(); // time stamp
      return new UseItemTagPacket(position, itemId, (short) equipSlot);
   }

   private AbstractUseCashItemPacket readMapleTv(SeekableLittleEndianAccessor accessor, short position, int itemId) {
      int tvType = itemId % 10;
      boolean megaMessenger = UseableCashItems.Broadcasters.isMapleTvMegaMessenger(itemId);
      boolean ear = false;
      String victim = "";
      if (tvType != 1) {
         if (tvType >= 3) {
            if (tvType == 3) {
               accessor.readByte();
            }
            ear = 1 == accessor.readByte();
         } else if (tvType != 2) {
            accessor.readByte();
         }
         if (tvType != 4) {
            victim = accessor.readMapleAsciiString();
         }
      }
      List<String> messages = new LinkedList<>();
      for (int i = 0; i < 5; i++) {
         String message = accessor.readMapleAsciiString();
         messages.add(message);
      }
      accessor.readInt();
      return new UseMapleTvPacket(position, itemId, megaMessenger, ear, victim, messages.toArray(String[]::new));
   }

   private AbstractUseCashItemPacket readItemMegaphone(SeekableLittleEndianAccessor accessor, short position, int itemId) {
      String msg = accessor.readMapleAsciiString();
      boolean whisper = accessor.readByte() == 1;
      boolean selected = accessor.readByte() == 1;
      byte inventoryType = (byte) accessor.readInt();
      short slot = (short) accessor.readInt();
      return new UseItemMegaphonePacket(position, itemId, msg, whisper, selected, inventoryType, slot);
   }

   private AbstractUseCashItemPacket readTripleMegaphone(SeekableLittleEndianAccessor accessor, short position, int itemId) {
      int lines = accessor.readByte();
      String[] msg2 = new String[lines];
      for (int i = 0; i < lines; i++) {
         msg2[i] = accessor.readMapleAsciiString();
      }
      boolean whisper = accessor.readByte() == 1;
      return new UseTripleMegaphonePacket(position, itemId, lines, msg2, whisper);
   }

   private AbstractUseCashItemPacket readApOrSpResetPacket(SeekableLittleEndianAccessor accessor, short position, int itemId) {
      if (UseableCashItems.CharacterStatModifiers.isAPReset(itemId)) {
         int to = accessor.readInt();
         int from = accessor.readInt();
         return new UseApResetPacket(position, itemId, to, from);
      } else {
         int to = accessor.readInt();
         int from = accessor.readInt();
         return new UseSpResetPacket(position, itemId, to, from);
      }
   }

   private AbstractUseCashItemPacket readTeleportRock(SeekableLittleEndianAccessor accessor, short position, int itemId) {
      boolean vip = accessor.readByte() == 1 && UseableCashItems.CharacterTeleporters.isVip(itemId);
      int mapId = -1;
      String name = "";
      if (!vip) {
         mapId = accessor.readInt();
      } else {
         name = accessor.readMapleAsciiString();
      }
      return new UseTeleportRockPacket(position, itemId, vip, mapId, name);
   }
}
