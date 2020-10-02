package tools;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.factory.AddNewCharacterPacketFactory;
import tools.packet.factory.AfterLoginErrorPacketFactory;
import tools.packet.factory.AllianceOperationPacketFactory;
import tools.packet.factory.AriantPacketFactory;
import tools.packet.factory.AttackPacketFactory;
import tools.packet.factory.BuddyPacketFactory;
import tools.packet.factory.CancelBuffPacketFactory;
import tools.packet.factory.CashShopGachaponPacketFactoryPacketFactory;
import tools.packet.factory.CashShopOperationPacketFactory;
import tools.packet.factory.ChangeChannelPacketFactory;
import tools.packet.factory.CharacterListPacketFactory;
import tools.packet.factory.CharacterNameResponsePacketFactory;
import tools.packet.factory.CharacterPacketFactory;
import tools.packet.factory.DeleteCharacterPacketFactory;
import tools.packet.factory.EventPacketFactory;
import tools.packet.factory.FamilyPacketFactory;
import tools.packet.factory.FieldPacketFactory;
import tools.packet.factory.ForeignEffectPacketFactory;
import tools.packet.factory.FredrickPacketFactory;
import tools.packet.factory.GenericPacketFactory;
import tools.packet.factory.GiveBuffPacketFactory;
import tools.packet.factory.GuestLoginPacketFactory;
import tools.packet.factory.GuildBBSPacketFactory;
import tools.packet.factory.GuildPacketFactory;
import tools.packet.factory.InventoryPacketFactory;
import tools.packet.factory.ItemDropPacketFactory;
import tools.packet.factory.ItemEnhancePacketFactory;
import tools.packet.factory.ItemGainInChatPacketFactory;
import tools.packet.factory.LoginStatusPacketFactory;
import tools.packet.factory.MTSOperationPacketFactory;
import tools.packet.factory.MakerResultPacketFactory;
import tools.packet.factory.MessagePacketFactory;
import tools.packet.factory.MessengerPacketFactory;
import tools.packet.factory.MonsterBookPacketFactory;
import tools.packet.factory.MonsterCarnivalPacketFactory;
import tools.packet.factory.MonsterPacketFactory;
import tools.packet.factory.MovementPacketFactory;
import tools.packet.factory.NPCTalkPacketFactory;
import tools.packet.factory.NameChangePacketFactory;
import tools.packet.factory.NewYearCardPacketFactory;
import tools.packet.factory.OwlOfMinervaPacketFactory;
import tools.packet.factory.ParcelPacketFactory;
import tools.packet.factory.PartyPacketFactory;
import tools.packet.factory.PetPacketFactory;
import tools.packet.factory.PicPacketFactory;
import tools.packet.factory.PinPacketFactory;
import tools.packet.factory.PingPacketFactory;
import tools.packet.factory.PlayerInteractionPacketFactory;
import tools.packet.factory.PlayerNPCPacketFactory;
import tools.packet.factory.PyramidPacketFactory;
import tools.packet.factory.QuestPacketFactory;
import tools.packet.factory.RPSPacketFactory;
import tools.packet.factory.ReactorPacketFactory;
import tools.packet.factory.RecommendedWorldMessagePacketFactory;
import tools.packet.factory.RelogResponsePacketFactory;
import tools.packet.factory.RemovePacketFactory;
import tools.packet.factory.ReportPacketFactory;
import tools.packet.factory.SelectWorldPacketFactory;
import tools.packet.factory.ServerIPPacketFactory;
import tools.packet.factory.ServerListPacketFactory;
import tools.packet.factory.ServerStatusPacketFactory;
import tools.packet.factory.SetFieldPacketFactory;
import tools.packet.factory.ShopPacketFactory;
import tools.packet.factory.SpawnPacketFactory;
import tools.packet.factory.SpecialShopPacketFactory;
import tools.packet.factory.StatUpdatePacketFactory;
import tools.packet.factory.StatusInfoPacketFactory;
import tools.packet.factory.StoragePacketFactory;
import tools.packet.factory.TVPacketFactory;
import tools.packet.factory.UIPacketFactory;
import tools.packet.factory.UpdateCharacterBoxPacketFactory;
import tools.packet.factory.ViewAllCharactersPacketFactory;
import tools.packet.factory.WeddingPacketFactory;
import tools.packet.factory.WorldTransferPacketFactory;

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
         case SERVER_STATUS:
            return Optional.of(ServerStatusPacketFactory.getInstance());
         case CHECK_PIN_CODE:
         case UPDATE_PIN_CODE:
            return Optional.of(PinPacketFactory.getInstance());
         case VIEW_ALL_CHAR:
            return Optional.of(ViewAllCharactersPacketFactory.getInstance());
         case SELECT_CHARACTER_BY_VAC:
            return Optional.of(AfterLoginErrorPacketFactory.getInstance());
         case SERVER_LIST:
            return Optional.of(ServerListPacketFactory.getInstance());
         case CHARACTER_LIST:
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
         case CASH_SHOP_OPERATION:
         case QUERY_CASH_RESULT:
         case MAPLE_LIFE_RESULT:
            return Optional.of(CashShopOperationPacketFactory.getInstance());
         case CASH_SHOP_CASH_ITEM_GACHAPON_RESULT:
            return Optional.of(CashShopGachaponPacketFactoryPacketFactory.getInstance());
         case PLAYER_INTERACTION:
            return Optional.of(PlayerInteractionPacketFactory.getInstance());
         case PARCEL:
            return Optional.of(ParcelPacketFactory.getInstance());
         case ALLIANCE_OPERATION:
            return Optional.of(AllianceOperationPacketFactory.getInstance());
         case PARTY_OPERATION:
         case UPDATE_PARTY_MEMBER_HP:
            return Optional.of(PartyPacketFactory.getInstance());
         case STORAGE:
            return Optional.of(StoragePacketFactory.getInstance());
         case NPC_TALK:
            return Optional.of(NPCTalkPacketFactory.getInstance());
         case SHOW_FOREIGN_EFFECT:
         case SHOW_SCROLL_EFFECT:
         case SKILL_LEARN_ITEM_RESULT:
         case SHOW_COMBO:
         case PLAYER_HINT:
         case SKILL_EFFECT:
         case SHOW_ITEM_EFFECT:
         case TALK_GUIDE:
         case SHOW_CHAIR:
         case CANCEL_CHAIR:
         case CANCEL_SKILL_EFFECT:
         case SCRIPT_PROGRESS_MESSAGE:
         case BLOCKED_MAP:
            return Optional.of(ForeignEffectPacketFactory.getInstance());
         case UPDATE_QUEST_INFO:
         case QUEST_CLEAR:
            return Optional.of(QuestPacketFactory.getInstance());
         case MTS_OPERATION:
         case MTS_OPERATION2:
            return Optional.of(MTSOperationPacketFactory.getInstance());
         case SHOW_STATUS_INFO:
            return Optional.of(StatusInfoPacketFactory.getInstance());
         case SHOW_ITEM_GAIN_IN_CHAT:
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
         case FAMILY_PRIVILEGE_LIST:
         case FAMILY_RESULT:
         case FAMILY_INFO_RESULT:
         case FAMILY_CHART_RESULT:
         case FAMILY_JOIN_REQUEST:
         case FAMILY_SUMMON_REQUEST:
         case FAMILY_NOTIFY_LOGIN_OR_LOGOUT:
         case FAMILY_JOIN_REQUEST_RESULT:
         case FAMILY_JOIN_ACCEPTED:
         case FAMILY_REP_GAIN:
            return Optional.of(FamilyPacketFactory.getInstance());
         case SPAWN_PORTAL:
         case SPAWN_DOOR:
         case REMOVE_DOOR:
         case SPAWN_SPECIAL_MAP_OBJECT:
         case SPAWN_NPC:
         case SPAWN_NPC_REQUEST_CONTROLLER:
         case SPAWN_MONSTER_CONTROL:
         case SPAWN_MONSTER:
         case SPAWN_PLAYER:
         case SPAWN_KITE:
         case SPAWN_MIST:
         case SPAWN_PET:
         case SPAWN_HIRED_MERCHANT:
         case SPAWN_GUIDE:
         case SPAWN_DRAGON:
         case CANNOT_SPAWN_KITE:
            return Optional.of(SpawnPacketFactory.getInstance());
         case REMOVE_TV:
         case REMOVE_SPECIAL_MAP_OBJECT:
         case REMOVE_KITE:
         case REMOVE_PLAYER_FROM_MAP:
         case REMOVE_ITEM_FROM_MAP:
         case REMOVE_MIST:
         case REMOVE_NPC:
         case REMOVE_DRAGON:
            return Optional.of(RemovePacketFactory.getInstance());
         case FIELD_EFFECT:
         case FIELD_OBSTACLE_ON_OFF:
         case FIELD_OBSTACLE_ON_OFF_LIST:
         case FIELD_OBSTACLE_ALL_RESET:
         case BLOW_WEATHER:
         case SET_BACK_EFFECT:
         case FORCED_MAP_EQUIP:
         case FORCED_STAT_RESET:
         case FORCED_STAT_SET:
         case CONTI_MOVE:
         case CONTI_STATE:
            return Optional.of(FieldPacketFactory.getInstance());
         case SNOWBALL_STATE:
         case HIT_SNOWBALL:
         case SNOWBALL_MESSAGE:
         case COCONUT_SCORE:
         case COCONUT_HIT:
            return Optional.of(EventPacketFactory.getInstance());
         case SET_FIELD:
            return Optional.of(SetFieldPacketFactory.getInstance());
         case SET_ITC:
         case SET_CASH_SHOP:
            return Optional.of(SpecialShopPacketFactory.getInstance());
         case MOVE_MONSTER_RESPONSE:
         case MOVE_PLAYER:
         case MOVE_SUMMON:
         case MOVE_MONSTER:
         case MOVE_DRAGON:
            return Optional.of(MovementPacketFactory.getInstance());
         case MAKER_RESULT:
            return Optional.of(MakerResultPacketFactory.getInstance());
         case ENABLE_TV:
         case SEND_TV:
            return Optional.of(TVPacketFactory.getInstance());
         case SERVER_MESSAGE:
         case SET_AVATAR_MEGAPHONE:
         case CLEAR_AVATAR_MEGAPHONE:
         case CHAT_TEXT:
         case WHISPER:
         case FAME_RESPONSE:
         case MULTI_CHAT:
         case NOTIFY_LEVEL_UP:
         case NOTIFY_MARRIAGE:
         case NOTIFY_JOB_CHANGE:
         case SPOUSE_CHAT:
         case SET_WEEK_EVENT_MESSAGE:
            return Optional.of(MessagePacketFactory.getInstance());
         case BUDDY_LIST:
            return Optional.of(BuddyPacketFactory.getInstance());
         case MESSENGER:
            return Optional.of(MessengerPacketFactory.getInstance());
         case PET_CHAT:
         case PET_COMMAND:
         case PET_NAME_CHANGE:
            return Optional.of(PetPacketFactory.getInstance());
         case UPDATE_CHAR_LOOK:
         case CHAR_INFO:
         case FACIAL_EXPRESSION:
         case SET_GENDER:
         case AUTO_MP_POT:
         case AUTO_HP_POT:
         case UPDATE_SKILLS:
         case SUMMON_SKILL:
         case COOL_DOWN:
         case DAMAGE_PLAYER:
         case SET_TAMING_MOB_INFO:
         case LEFT_KNOCK_BACK:
            return Optional.of(CharacterPacketFactory.getInstance());
         case MARRIAGE_REQUEST:
         case WEDDING_PHOTO:
         case MARRIAGE_RESULT:
         case NOTIFY_MARRIED_PARTNER_MAP_TRANSFER:
         case WEDDING_CEREMONY_END:
         case WEDDING_PROGRESS:
         case WEDDING_GIFT_RESULT:
            return Optional.of(WeddingPacketFactory.getInstance());
         case FREDRICK_MESSAGE:
         case FREDRICK:
            return Optional.of(FredrickPacketFactory.getInstance());
         case SHOP_LINK_RESULT:
         case SHOP_SCANNER_RESULT:
            return Optional.of(OwlOfMinervaPacketFactory.getInstance());
         case UPDATE_CHAR_BOX:
         case CHALKBOARD:
            return Optional.of(UpdateCharacterBoxPacketFactory.getInstance());
         case NEW_YEAR_CARD_RES:
            return Optional.of(NewYearCardPacketFactory.getInstance());
         case GUILD_BBS_PACKET:
            return Optional.of(GuildBBSPacketFactory.getInstance());
         case REACTOR_SPAWN:
         case REACTOR_HIT:
         case REACTOR_DESTROY:
            return Optional.of(ReactorPacketFactory.getInstance());
         case CLOSE_RANGE_ATTACK:
         case RANGED_ATTACK:
         case MAGIC_ATTACK:
         case SUMMON_ATTACK:
         case THROW_GRENADE:
            return Optional.of(AttackPacketFactory.getInstance());
         case VEGA_SCROLL:
         case VICIOUS_HAMMER:
            return Optional.of(ItemEnhancePacketFactory.getInstance());
         case UPDATE_HIRED_MERCHANT:
         case OPEN_NPC_SHOP:
         case CONFIRM_SHOP_TRANSACTION:
         case ENTRUSTED_SHOP_CHECK_RESULT:
         case DESTROY_HIRED_MERCHANT:
            return Optional.of(ShopPacketFactory.getInstance());
         case GIVE_BUFF:
         case GIVE_FOREIGN_BUFF:
            return Optional.of(GiveBuffPacketFactory.getInstance());
         case CANCEL_BUFF:
         case CANCEL_FOREIGN_BUFF:
            return Optional.of(CancelBuffPacketFactory.getInstance());
         case PYRAMID_SCORE:
         case PYRAMID_GAUGE:
            return Optional.of(PyramidPacketFactory.getInstance());
         case KILL_MONSTER:
         case SHOW_MONSTER_HP:
         case APPLY_MONSTER_STATUS:
         case CANCEL_MONSTER_STATUS:
         case DAMAGE_MONSTER:
         case CATCH_MONSTER:
         case CATCH_MONSTER_WITH_ITEM:
         case BRIDLE_MOB_CATCH_FAIL:
         case DAMAGE_SUMMON:
            return Optional.of(MonsterPacketFactory.getInstance());
         case ARIANT_ARENA_SHOW_RESULT:
         case ARIANT_ARENA_USER_SCORE:
            return Optional.of(AriantPacketFactory.getInstance());
         case OPEN_UI:
         case LOCK_UI:
         case DISABLE_UI:
         case KEYMAP:
         case MACRO_SYS_DATA_INIT:
         case ADMIN_RESULT:
         case GATHER_ITEM_RESULT:
         case SORT_ITEM_RESULT:
         case CLOCK:
         case STOP_CLOCK:
         case BLOCKED_SERVER:
         case MEMO_RESULT:
         case OX_QUIZ:
         case MAP_TRANSFER_RESULT:
            return Optional.of(UIPacketFactory.getInstance());
         case SUE_CHARACTER_RESULT:
         case DATA_CRC_CHECK_FAILED:
            return Optional.of(ReportPacketFactory.getInstance());
         case RPS_GAME:
            return Optional.of(RPSPacketFactory.getInstance());
         case CASH_SHOP_CHECK_NAME_CHANGE_POSSIBLE_RESULT:
         case CASH_SHOP_CHECK_NAME_CHANGE:
         case CANCEL_NAME_CHANGE_RESULT:
            return Optional.of(NameChangePacketFactory.getInstance());
         case CASH_SHOP_CHECK_TRANSFER_WORLD_POSSIBLE_RESULT:
         case CANCEL_TRANSFER_WORLD_RESULT:
            return Optional.of(WorldTransferPacketFactory.getInstance());
         case IMITATED_NPC_DATA:
            return Optional.of(PlayerNPCPacketFactory.getInstance());
         case DROP_ITEM_FROM_MAP_OBJECT:
            return Optional.of(ItemDropPacketFactory.getInstance());
         case CLAIM_STATUS_CHANGED:
         case SESSION_VALUE:
         case DOJO_WARP_UP:
         case SET_NPC_SCRIPTABLE:
         case QUICK_SLOT_INIT:
            return Optional.of(GenericPacketFactory.getInstance());
      }
      LoggerUtil.printError(LoggerOriginator.PACKET_LOGS, "Trying to get an unhandled PacketFactory " + opcode.getValue());
      return Optional.empty();
   }

   /**
    * Sends a hello packet.
    *
    * @param mapleVersion The maple client version.
    * @param sendIv       the IV used by the server for sending
    * @param recvIv       the IV used by the server for receiving
    */
   public static byte[] getHello(short mapleVersion, byte[] sendIv, byte[] recvIv) {
      final MaplePacketLittleEndianWriter writer = new MaplePacketLittleEndianWriter(8);
      writer.writeShort(0x0E);
      writer.writeShort(mapleVersion);
      writer.writeShort(1);
      writer.write(49);
      writer.write(recvIv);
      writer.write(sendIv);
      writer.write(8);
      return writer.getPacket();
   }

   public static byte[] customPacket(byte[] packet) {
      final MaplePacketLittleEndianWriter writer = new MaplePacketLittleEndianWriter(packet.length);
      writer.write(packet);
      return writer.getPacket();
   }
}
