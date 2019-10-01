package tools;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;
import tools.packet.factory.AddNewCharacterPacketFactory;
import tools.packet.factory.AfterLoginErrorPacketFactory;
import tools.packet.factory.AllianceOperationPacketFactory;
import tools.packet.factory.CashShopGachaponPacketFactoryPacketFactory;
import tools.packet.factory.CashShopOperationPacketFactory;
import tools.packet.factory.ChangeChannelPacketFactory;
import tools.packet.factory.CharacterListPacketFactory;
import tools.packet.factory.CharacterNameResponsePacketFactory;
import tools.packet.factory.DeleteCharacterPacketFactory;
import tools.packet.factory.ForeignEffectPacketFactory;
import tools.packet.factory.GuestLoginPacketFactory;
import tools.packet.factory.GuildPacketFactory;
import tools.packet.factory.InventoryPacketFactory;
import tools.packet.factory.ItemGainInChatPacketFactory;
import tools.packet.factory.LoginStatusPacketFactory;
import tools.packet.factory.MTSOperationPacketFactory;
import tools.packet.factory.MonsterBookPacketFactory;
import tools.packet.factory.MonsterCarnivalPacketFactory;
import tools.packet.factory.NPCTalkPacketFactory;
import tools.packet.factory.ParcelPacketFactory;
import tools.packet.factory.PartyOperationPacketFactory;
import tools.packet.factory.PicPacketFactory;
import tools.packet.factory.PinPacketFactory;
import tools.packet.factory.PingPacketFactory;
import tools.packet.factory.PlayerInteractionPacketFactory;
import tools.packet.factory.QuestInfoPacketFactory;
import tools.packet.factory.RecommendedWorldMessagePacketFactory;
import tools.packet.factory.RelogResponsePacketFactory;
import tools.packet.factory.SelectWorldPacketFactory;
import tools.packet.factory.ServerIPPacketFactory;
import tools.packet.factory.ServerListPacketFactory;
import tools.packet.factory.ServerStatusPacketFactory;
import tools.packet.factory.StatUpdatePacketFactory;
import tools.packet.factory.StatusInfoPacketFactory;
import tools.packet.factory.StoragePacketFactory;
import tools.packet.factory.ViewAllCharactersPacketFactory;

public class PacketCreator {
   private PacketCreator() {
   }

   public static void announce(MapleCharacter mapleCharacter, PacketInput input) {
      announce(mapleCharacter.getClient(), input);
   }

   public static void announce(MapleClient client, PacketInput input) {
      get(input.opcode()).ifPresent(factory -> factory.announce(client, input));
   }

   public static byte[] create(PacketInput input) {
      Optional<PacketFactory> factory = get(input.opcode());
      if (factory.isEmpty()) {
         return new byte[0];
      }
      return factory.get().create(input);
   }

   public static Optional<PacketFactory> get(SendOpcode opcode) {
      switch (opcode) {
         case LOGIN_STATUS:
            return Optional.of(LoginStatusPacketFactory.getInstance());
         case GUEST_ID_LOGIN:
            return Optional.of(GuestLoginPacketFactory.getInstance());
         case SERVERSTATUS:
            return Optional.of(ServerStatusPacketFactory.getInstance());
         case CHECK_PINCODE:
         case UPDATE_PINCODE:
            return Optional.of(PinPacketFactory.getInstance());
         case VIEW_ALL_CHAR:
            return Optional.of(ViewAllCharactersPacketFactory.getInstance());
         case SELECT_CHARACTER_BY_VAC:
            return Optional.of(AfterLoginErrorPacketFactory.getInstance());
         case SERVERLIST:
            return Optional.of(ServerListPacketFactory.getInstance());
         case CHARLIST:
            return Optional.of(CharacterListPacketFactory.getInstance());
         case SERVER_IP:
            return Optional.of(ServerIPPacketFactory.getInstance());
         case CHAR_NAME_RESPONSE:
            return Optional.of(CharacterNameResponsePacketFactory.getInstance());
         case ADD_NEW_CHAR_ENTRY:
            return Optional.of(AddNewCharacterPacketFactory.getInstance());
         case DELETE_CHAR_RESPONSE:
            return Optional.of(DeleteCharacterPacketFactory.getInstance());
         case CHANGE_CHANNEL:
            return Optional.of(ChangeChannelPacketFactory.getInstance());
         case PING:
            return Optional.of(PingPacketFactory.getInstance());
         case RELOG_RESPONSE:
            return Optional.of(RelogResponsePacketFactory.getInstance());
         case LAST_CONNECTED_WORLD:
            return Optional.of(SelectWorldPacketFactory.getInstance());
         case RECOMMENDED_WORLD_MESSAGE:
            return Optional.of(RecommendedWorldMessagePacketFactory.getInstance());
         case CHECK_SPW_RESULT:
            return Optional.of(PicPacketFactory.getInstance());
         case INVENTORY_OPERATION:
         case INVENTORY_GROW:
            return Optional.of(InventoryPacketFactory.getInstance());
         case STAT_CHANGED:
            return Optional.of(StatUpdatePacketFactory.getInstance());
         case CASHSHOP_OPERATION:
            return Optional.of(CashShopOperationPacketFactory.getInstance());
         case CASHSHOP_CASH_ITEM_GACHAPON_RESULT:
            return Optional.of(CashShopGachaponPacketFactoryPacketFactory.getInstance());
         case PLAYER_INTERACTION:
            return Optional.of(PlayerInteractionPacketFactory.getInstance());
         case PARCEL:
            return Optional.of(ParcelPacketFactory.getInstance());
         case ALLIANCE_OPERATION:
            return Optional.of(AllianceOperationPacketFactory.getInstance());
         case PARTY_OPERATION:
            return Optional.of(PartyOperationPacketFactory.getInstance());
         case STORAGE:
            return Optional.of(StoragePacketFactory.getInstance());
         case NPC_TALK:
            return Optional.of(NPCTalkPacketFactory.getInstance());
         case SHOW_FOREIGN_EFFECT:
            return Optional.of(ForeignEffectPacketFactory.getInstance());
         case UPDATE_QUEST_INFO:
            return Optional.of(QuestInfoPacketFactory.getInstance());
         case MTS_OPERATION:
         case MTS_OPERATION2:
            return Optional.of(MTSOperationPacketFactory.getInstance());
         case SHOW_STATUS_INFO:
            return Optional.of(StatusInfoPacketFactory.getInstance());
         case SHOW_ITEM_GAIN_INCHAT:
            return Optional.of(ItemGainInChatPacketFactory.getInstance());
         case GUILD_OPERATION:
         case GUILD_NAME_CHANGED:
         case GUILD_MARK_CHANGED:
            return Optional.of(GuildPacketFactory.getInstance());
         case MONSTER_CARNIVAL_START:
         case MONSTER_CARNIVAL_DIED:
         case MONSTER_CARNIVAL_SUMMON:
         case MONSTER_CARNIVAL_MESSAGE:
         case MONSTER_CARNIVAL_OBTAINED_CP:
         case MONSTER_CARNIVAL_PARTY_CP:
            return Optional.of(MonsterCarnivalPacketFactory.getInstance());
         case MONSTER_BOOK_SET_CARD:
         case MONSTER_BOOK_SET_COVER:
            return Optional.of(MonsterBookPacketFactory.getInstance());
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to get an unhandled PacketFactory " + opcode.getValue());
      return Optional.empty();
   }
}
