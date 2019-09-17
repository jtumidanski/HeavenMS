package net.server;

import net.PacketProcessor;
import net.opcodes.RecvOpcode;
import net.server.channel.handlers.AcceptFamilyHandler;
import net.server.channel.handlers.AdminChatHandler;
import net.server.channel.handlers.AdminCommandHandler;
import net.server.channel.handlers.AdminLogHandler;
import net.server.channel.handlers.AllianceOperationHandler;
import net.server.channel.handlers.AranComboHandler;
import net.server.channel.handlers.AutoAggroHandler;
import net.server.channel.handlers.AutoAssignHandler;
import net.server.channel.handlers.BBSOperationHandler;
import net.server.channel.handlers.BeholderHandler;
import net.server.channel.handlers.BuddyListModifyHandler;
import net.server.channel.handlers.CancelBuffHandler;
import net.server.channel.handlers.CancelChairHandler;
import net.server.channel.handlers.CancelDebuffHandler;
import net.server.channel.handlers.CancelItemEffectHandler;
import net.server.channel.handlers.CashOperationHandler;
import net.server.channel.handlers.CashShopSurpriseHandler;
import net.server.channel.handlers.ChangeChannelHandler;
import net.server.channel.handlers.ChangeMapHandler;
import net.server.channel.handlers.ChangeMapSpecialHandler;
import net.server.channel.handlers.CharInfoRequestHandler;
import net.server.channel.handlers.ClickGuideHandler;
import net.server.channel.handlers.CloseChalkboardHandler;
import net.server.channel.handlers.CloseRangeDamageHandler;
import net.server.channel.handlers.CoconutHandler;
import net.server.channel.handlers.CouponCodeHandler;
import net.server.channel.handlers.DamageSummonHandler;
import net.server.channel.handlers.DenyAllianceRequestHandler;
import net.server.channel.handlers.DenyGuildRequestHandler;
import net.server.channel.handlers.DenyPartyRequestHandler;
import net.server.channel.handlers.DistributeAPHandler;
import net.server.channel.handlers.DistributeSPHandler;
import net.server.channel.handlers.DoorHandler;
import net.server.channel.handlers.DueyHandler;
import net.server.channel.handlers.EnterCashShopHandler;
import net.server.channel.handlers.EnterMTSHandler;
import net.server.channel.handlers.FaceExpressionHandler;
import net.server.channel.handlers.FamilyAddHandler;
import net.server.channel.handlers.FamilyPreceptsHandler;
import net.server.channel.handlers.FamilySeparateHandler;
import net.server.channel.handlers.FamilySummonResponseHandler;
import net.server.channel.handlers.FamilyUseHandler;
import net.server.channel.handlers.FieldDamageMobHandler;
import net.server.channel.handlers.FredrickHandler;
import net.server.channel.handlers.GeneralChatHandler;
import net.server.channel.handlers.GiveFameHandler;
import net.server.channel.handlers.GrenadeEffectHandler;
import net.server.channel.handlers.GuildOperationHandler;
import net.server.channel.handlers.HealOvertimeHandler;
import net.server.channel.handlers.HiredMerchantRequest;
import net.server.channel.handlers.InnerPortalHandler;
import net.server.channel.handlers.InventoryMergeHandler;
import net.server.channel.handlers.InventorySortHandler;
import net.server.channel.handlers.ItemMoveHandler;
import net.server.channel.handlers.ItemPickupHandler;
import net.server.channel.handlers.ItemRewardHandler;
import net.server.channel.handlers.KeymapChangeHandler;
import net.server.channel.handlers.LeftKnockbackHandler;
import net.server.channel.handlers.MTSHandler;
import net.server.channel.handlers.MagicDamageHandler;
import net.server.channel.handlers.MakerSkillHandler;
import net.server.channel.handlers.MesoDropHandler;
import net.server.channel.handlers.MessengerHandler;
import net.server.channel.handlers.MobBanishPlayerHandler;
import net.server.channel.handlers.MobDamageMobFriendlyHandler;
import net.server.channel.handlers.MobDamageMobHandler;
import net.server.channel.handlers.MonsterBombHandler;
import net.server.channel.handlers.MonsterBookCoverHandler;
import net.server.channel.handlers.MonsterCarnivalHandler;
import net.server.channel.handlers.MoveDragonHandler;
import net.server.channel.handlers.MoveLifeHandler;
import net.server.channel.handlers.MovePetHandler;
import net.server.channel.handlers.MovePlayerHandler;
import net.server.channel.handlers.MoveSummonHandler;
import net.server.channel.handlers.MultiChatHandler;
import net.server.channel.handlers.NPCAnimationHandler;
import net.server.channel.handlers.NPCMoreTalkHandler;
import net.server.channel.handlers.NPCShopHandler;
import net.server.channel.handlers.NPCTalkHandler;
import net.server.channel.handlers.NewYearCardHandler;
import net.server.channel.handlers.NoteActionHandler;
import net.server.channel.handlers.OpenFamilyHandler;
import net.server.channel.handlers.OpenFamilyPedigreeHandler;
import net.server.channel.handlers.OwlWarpHandler;
import net.server.channel.handlers.PartyOperationHandler;
import net.server.channel.handlers.PartySearchRegisterHandler;
import net.server.channel.handlers.PartySearchStartHandler;
import net.server.channel.handlers.PartySearchUpdateHandler;
import net.server.channel.handlers.PetAutoPotHandler;
import net.server.channel.handlers.PetChatHandler;
import net.server.channel.handlers.PetCommandHandler;
import net.server.channel.handlers.PetExcludeItemsHandler;
import net.server.channel.handlers.PetFoodHandler;
import net.server.channel.handlers.PetLootHandler;
import net.server.channel.handlers.PlayerInteractionHandler;
import net.server.channel.handlers.PlayerLoggedinHandler;
import net.server.channel.handlers.PlayerMapTransitionHandler;
import net.server.channel.handlers.QuestActionHandler;
import net.server.channel.handlers.RPSActionHandler;
import net.server.channel.handlers.RaiseIncExpHandler;
import net.server.channel.handlers.RaiseUIStateHandler;
import net.server.channel.handlers.RangedAttackHandler;
import net.server.channel.handlers.ReactorHitHandler;
import net.server.channel.handlers.RemoteGachaponHandler;
import net.server.channel.handlers.RemoteStoreHandler;
import net.server.channel.handlers.ReportHandler;
import net.server.channel.handlers.RingActionHandler;
import net.server.channel.handlers.ScriptedItemHandler;
import net.server.channel.handlers.ScrollHandler;
import net.server.channel.handlers.SkillBookHandler;
import net.server.channel.handlers.SkillEffectHandler;
import net.server.channel.handlers.SkillMacroHandler;
import net.server.channel.handlers.SnowballHandler;
import net.server.channel.handlers.SpawnPetHandler;
import net.server.channel.handlers.SpecialMoveHandler;
import net.server.channel.handlers.SpouseChatHandler;
import net.server.channel.handlers.StorageHandler;
import net.server.channel.handlers.SummonDamageHandler;
import net.server.channel.handlers.TakeDamageHandler;
import net.server.channel.handlers.TouchMonsterDamageHandler;
import net.server.channel.handlers.TouchReactorHandler;
import net.server.channel.handlers.TouchingCashShopHandler;
import net.server.channel.handlers.TransferNameHandler;
import net.server.channel.handlers.TransferNameResultHandler;
import net.server.channel.handlers.TransferWorldHandler;
import net.server.channel.handlers.TrockAddMapHandler;
import net.server.channel.handlers.UseCashItemHandler;
import net.server.channel.handlers.UseCatchItemHandler;
import net.server.channel.handlers.UseChairHandler;
import net.server.channel.handlers.UseDeathItemHandler;
import net.server.channel.handlers.UseGachaExpHandler;
import net.server.channel.handlers.UseHammerHandler;
import net.server.channel.handlers.UseItemEffectHandler;
import net.server.channel.handlers.UseItemHandler;
import net.server.channel.handlers.UseMapleLifeHandler;
import net.server.channel.handlers.UseMountFoodHandler;
import net.server.channel.handlers.UseOwlOfMinervaHandler;
import net.server.channel.handlers.UseSolomonHandler;
import net.server.channel.handlers.UseSummonBagHandler;
import net.server.channel.handlers.UseWaterOfLifeHandler;
import net.server.channel.handlers.WeddingHandler;
import net.server.channel.handlers.WeddingTalkHandler;
import net.server.channel.handlers.WeddingTalkMoreHandler;
import net.server.channel.handlers.WhisperHandler;
import net.server.handlers.CustomPacketHandler;
import net.server.handlers.KeepAliveHandler;
import net.server.handlers.LoginRequiringNoOpHandler;
import net.server.handlers.login.AcceptToSHandler;
import net.server.handlers.login.AfterLoginHandler;
import net.server.handlers.login.CharSelectedWithPicHandler;
import net.server.handlers.login.CharacterListRequestHandler;
import net.server.handlers.login.CharacterSelectedHandler;
import net.server.handlers.login.CheckCharNameHandler;
import net.server.handlers.login.CreateCharHandler;
import net.server.handlers.login.DeleteCharHandler;
import net.server.handlers.login.GuestLoginHandler;
import net.server.handlers.login.LoginPasswordHandler;
import net.server.handlers.login.RegisterPicHandler;
import net.server.handlers.login.RegisterPinHandler;
import net.server.handlers.login.RelogRequestHandler;
import net.server.handlers.login.ServerListRequestHandler;
import net.server.handlers.login.ServerStatusRequestHandler;
import net.server.handlers.login.SetGenderHandler;
import net.server.handlers.login.ViewAllCharHandler;
import net.server.handlers.login.ViewAllCharRegisterPicHandler;
import net.server.handlers.login.ViewAllCharSelectedHandler;
import net.server.handlers.login.ViewAllCharSelectedWithPicHandler;

public class HandlerFactory {
   private static HandlerFactory ourInstance = new HandlerFactory();

   public static HandlerFactory getInstance() {
      return ourInstance;
   }

   private HandlerFactory() {
   }

   public void registerHandlers(PacketProcessor packetProcessor, int channel) {
      registerBaseHandlers(packetProcessor);
      if (channel < 0) {
         registerLoginHandlers(packetProcessor);
      } else {
         registerChannelHandlers(packetProcessor);
      }
   }

   private void registerBaseHandlers(PacketProcessor packetProcessor) {
      packetProcessor.registerHandler(RecvOpcode.PONG, new KeepAliveHandler());
      packetProcessor.registerHandler(RecvOpcode.CUSTOM_PACKET, new CustomPacketHandler());
   }

   private void registerLoginHandlers(PacketProcessor packetProcessor) {
      packetProcessor.registerHandler(RecvOpcode.ACCEPT_TOS, new AcceptToSHandler());
      packetProcessor.registerHandler(RecvOpcode.AFTER_LOGIN, new AfterLoginHandler());
      packetProcessor.registerHandler(RecvOpcode.SERVERLIST_REREQUEST, new ServerListRequestHandler());
      packetProcessor.registerHandler(RecvOpcode.CHARLIST_REQUEST, new CharacterListRequestHandler());
      packetProcessor.registerHandler(RecvOpcode.CHAR_SELECT, new CharacterSelectedHandler());
      packetProcessor.registerHandler(RecvOpcode.LOGIN_PASSWORD, new LoginPasswordHandler());
      packetProcessor.registerHandler(RecvOpcode.RELOG, new RelogRequestHandler());
      packetProcessor.registerHandler(RecvOpcode.SERVERLIST_REQUEST, new ServerListRequestHandler());
      packetProcessor.registerHandler(RecvOpcode.SERVERSTATUS_REQUEST, new ServerStatusRequestHandler());
      packetProcessor.registerHandler(RecvOpcode.CHECK_CHAR_NAME, new CheckCharNameHandler());
      packetProcessor.registerHandler(RecvOpcode.CREATE_CHAR, new CreateCharHandler());
      packetProcessor.registerHandler(RecvOpcode.DELETE_CHAR, new DeleteCharHandler());
      packetProcessor.registerHandler(RecvOpcode.VIEW_ALL_CHAR, new ViewAllCharHandler());
      packetProcessor.registerHandler(RecvOpcode.PICK_ALL_CHAR, new ViewAllCharSelectedHandler());
      packetProcessor.registerHandler(RecvOpcode.REGISTER_PIN, new RegisterPinHandler());
      packetProcessor.registerHandler(RecvOpcode.GUEST_LOGIN, new GuestLoginHandler());
      packetProcessor.registerHandler(RecvOpcode.REGISTER_PIC, new RegisterPicHandler());
      packetProcessor.registerHandler(RecvOpcode.CHAR_SELECT_WITH_PIC, new CharSelectedWithPicHandler());
      packetProcessor.registerHandler(RecvOpcode.SET_GENDER, new SetGenderHandler());
      packetProcessor.registerHandler(RecvOpcode.VIEW_ALL_WITH_PIC, new ViewAllCharSelectedWithPicHandler());
      packetProcessor.registerHandler(RecvOpcode.VIEW_ALL_PIC_REGISTER, new ViewAllCharRegisterPicHandler());
   }

   private void registerChannelHandlers(PacketProcessor packetProcessor) {
      packetProcessor.registerHandler(RecvOpcode.NAME_TRANSFER, new TransferNameHandler());
      packetProcessor.registerHandler(RecvOpcode.CHECK_CHAR_NAME, new TransferNameResultHandler());
      packetProcessor.registerHandler(RecvOpcode.WORLD_TRANSFER, new TransferWorldHandler());
      packetProcessor.registerHandler(RecvOpcode.CHANGE_CHANNEL, new ChangeChannelHandler());
      packetProcessor.registerHandler(RecvOpcode.STRANGE_DATA, new LoginRequiringNoOpHandler());
      packetProcessor.registerHandler(RecvOpcode.GENERAL_CHAT, new GeneralChatHandler());
      packetProcessor.registerHandler(RecvOpcode.WHISPER, new WhisperHandler());
      packetProcessor.registerHandler(RecvOpcode.NPC_TALK, new NPCTalkHandler());
      packetProcessor.registerHandler(RecvOpcode.NPC_TALK_MORE, new NPCMoreTalkHandler());
      packetProcessor.registerHandler(RecvOpcode.QUEST_ACTION, new QuestActionHandler());
      packetProcessor.registerHandler(RecvOpcode.GRENADE_EFFECT, new GrenadeEffectHandler());
      packetProcessor.registerHandler(RecvOpcode.NPC_SHOP, new NPCShopHandler());
      packetProcessor.registerHandler(RecvOpcode.ITEM_SORT, new InventoryMergeHandler());
      packetProcessor.registerHandler(RecvOpcode.ITEM_MOVE, new ItemMoveHandler());
      packetProcessor.registerHandler(RecvOpcode.MESO_DROP, new MesoDropHandler());
      packetProcessor.registerHandler(RecvOpcode.PLAYER_LOGGEDIN, new PlayerLoggedinHandler());
      packetProcessor.registerHandler(RecvOpcode.CHANGE_MAP, new ChangeMapHandler());
      packetProcessor.registerHandler(RecvOpcode.USE_CASH_ITEM, new UseCashItemHandler());
      packetProcessor.registerHandler(RecvOpcode.USE_ITEM, new UseItemHandler());
      packetProcessor.registerHandler(RecvOpcode.USE_RETURN_SCROLL, new UseItemHandler());
      packetProcessor.registerHandler(RecvOpcode.USE_UPGRADE_SCROLL, new ScrollHandler());
      packetProcessor.registerHandler(RecvOpcode.USE_SUMMON_BAG, new UseSummonBagHandler());
      packetProcessor.registerHandler(RecvOpcode.FACE_EXPRESSION, new FaceExpressionHandler());
      packetProcessor.registerHandler(RecvOpcode.HEAL_OVER_TIME, new HealOvertimeHandler());
      packetProcessor.registerHandler(RecvOpcode.ITEM_PICKUP, new ItemPickupHandler());
      packetProcessor.registerHandler(RecvOpcode.CHAR_INFO_REQUEST, new CharInfoRequestHandler());
      packetProcessor.registerHandler(RecvOpcode.USE_INNER_PORTAL, new InnerPortalHandler());
      packetProcessor.registerHandler(RecvOpcode.CANCEL_BUFF, new CancelBuffHandler());
      packetProcessor.registerHandler(RecvOpcode.CANCEL_ITEM_EFFECT, new CancelItemEffectHandler());
      packetProcessor.registerHandler(RecvOpcode.PLAYER_INTERACTION, new PlayerInteractionHandler());
      packetProcessor.registerHandler(RecvOpcode.RPS_ACTION, new RPSActionHandler());
      packetProcessor.registerHandler(RecvOpcode.DISTRIBUTE_AP, new DistributeAPHandler());
      packetProcessor.registerHandler(RecvOpcode.DISTRIBUTE_SP, new DistributeSPHandler());
      packetProcessor.registerHandler(RecvOpcode.CHANGE_KEYMAP, new KeymapChangeHandler());
      packetProcessor.registerHandler(RecvOpcode.CHANGE_MAP_SPECIAL, new ChangeMapSpecialHandler());
      packetProcessor.registerHandler(RecvOpcode.STORAGE, new StorageHandler());
      packetProcessor.registerHandler(RecvOpcode.GIVE_FAME, new GiveFameHandler());
      packetProcessor.registerHandler(RecvOpcode.PARTY_OPERATION, new PartyOperationHandler());
      packetProcessor.registerHandler(RecvOpcode.DENY_PARTY_REQUEST, new DenyPartyRequestHandler());
      packetProcessor.registerHandler(RecvOpcode.MULTI_CHAT, new MultiChatHandler());
      packetProcessor.registerHandler(RecvOpcode.USE_DOOR, new DoorHandler());
      packetProcessor.registerHandler(RecvOpcode.ENTER_MTS, new EnterMTSHandler());
      packetProcessor.registerHandler(RecvOpcode.ENTER_CASHSHOP, new EnterCashShopHandler());
      packetProcessor.registerHandler(RecvOpcode.DAMAGE_SUMMON, new DamageSummonHandler());
      packetProcessor.registerHandler(RecvOpcode.BUDDYLIST_MODIFY, new BuddyListModifyHandler());
      packetProcessor.registerHandler(RecvOpcode.USE_ITEMEFFECT, new UseItemEffectHandler());
      packetProcessor.registerHandler(RecvOpcode.USE_CHAIR, new UseChairHandler());
      packetProcessor.registerHandler(RecvOpcode.CANCEL_CHAIR, new CancelChairHandler());
      packetProcessor.registerHandler(RecvOpcode.DAMAGE_REACTOR, new ReactorHitHandler());
      packetProcessor.registerHandler(RecvOpcode.GUILD_OPERATION, new GuildOperationHandler());
      packetProcessor.registerHandler(RecvOpcode.DENY_GUILD_REQUEST, new DenyGuildRequestHandler());
      packetProcessor.registerHandler(RecvOpcode.BBS_OPERATION, new BBSOperationHandler());
      packetProcessor.registerHandler(RecvOpcode.SKILL_EFFECT, new SkillEffectHandler());
      packetProcessor.registerHandler(RecvOpcode.MESSENGER, new MessengerHandler());
      packetProcessor.registerHandler(RecvOpcode.CHECK_CASH, new TouchingCashShopHandler());
      packetProcessor.registerHandler(RecvOpcode.SPAWN_PET, new SpawnPetHandler());
      packetProcessor.registerHandler(RecvOpcode.NPC_ACTION, new NPCAnimationHandler());
      packetProcessor.registerHandler(RecvOpcode.CASHSHOP_OPERATION, new CashOperationHandler());
      packetProcessor.registerHandler(RecvOpcode.COUPON_CODE, new CouponCodeHandler());
      packetProcessor.registerHandler(RecvOpcode.PET_CHAT, new PetChatHandler());
      packetProcessor.registerHandler(RecvOpcode.PET_COMMAND, new PetCommandHandler());
      packetProcessor.registerHandler(RecvOpcode.PET_FOOD, new PetFoodHandler());
      packetProcessor.registerHandler(RecvOpcode.PET_LOOT, new PetLootHandler());
      packetProcessor.registerHandler(RecvOpcode.AUTO_AGGRO, new AutoAggroHandler());
      packetProcessor.registerHandler(RecvOpcode.MONSTER_BOMB, new MonsterBombHandler());
      packetProcessor.registerHandler(RecvOpcode.CANCEL_DEBUFF, new CancelDebuffHandler());
      packetProcessor.registerHandler(RecvOpcode.USE_SKILL_BOOK, new SkillBookHandler());
      packetProcessor.registerHandler(RecvOpcode.SKILL_MACRO, new SkillMacroHandler());
      packetProcessor.registerHandler(RecvOpcode.NOTE_ACTION, new NoteActionHandler());
      packetProcessor.registerHandler(RecvOpcode.CLOSE_CHALKBOARD, new CloseChalkboardHandler());
      packetProcessor.registerHandler(RecvOpcode.USE_MOUNT_FOOD, new UseMountFoodHandler());
      packetProcessor.registerHandler(RecvOpcode.MTS_OPERATION, new MTSHandler());
      packetProcessor.registerHandler(RecvOpcode.RING_ACTION, new RingActionHandler());
      packetProcessor.registerHandler(RecvOpcode.SPOUSE_CHAT, new SpouseChatHandler());
      packetProcessor.registerHandler(RecvOpcode.PET_AUTO_POT, new PetAutoPotHandler());
      packetProcessor.registerHandler(RecvOpcode.PET_EXCLUDE_ITEMS, new PetExcludeItemsHandler());
      packetProcessor.registerHandler(RecvOpcode.OWL_ACTION, new UseOwlOfMinervaHandler());
      packetProcessor.registerHandler(RecvOpcode.OWL_WARP, new OwlWarpHandler());
      packetProcessor.registerHandler(RecvOpcode.TROCK_ADD_MAP, new TrockAddMapHandler());
      packetProcessor.registerHandler(RecvOpcode.HIRED_MERCHANT_REQUEST, new HiredMerchantRequest());
      packetProcessor.registerHandler(RecvOpcode.MOB_BANISH_PLAYER, new MobBanishPlayerHandler());
      packetProcessor.registerHandler(RecvOpcode.MOB_DAMAGE_MOB, new MobDamageMobHandler());
      packetProcessor.registerHandler(RecvOpcode.REPORT, new ReportHandler());
      packetProcessor.registerHandler(RecvOpcode.MONSTER_BOOK_COVER, new MonsterBookCoverHandler());
      packetProcessor.registerHandler(RecvOpcode.OPEN_FAMILY_PEDIGREE, new OpenFamilyPedigreeHandler());
      packetProcessor.registerHandler(RecvOpcode.OPEN_FAMILY, new OpenFamilyHandler());
      packetProcessor.registerHandler(RecvOpcode.ADD_FAMILY, new FamilyAddHandler());
      packetProcessor.registerHandler(RecvOpcode.SEPARATE_FAMILY_BY_SENIOR, new FamilySeparateHandler());
      packetProcessor.registerHandler(RecvOpcode.SEPARATE_FAMILY_BY_JUNIOR, new FamilySeparateHandler());
      packetProcessor.registerHandler(RecvOpcode.CHANGE_FAMILY_MESSAGE, new FamilyPreceptsHandler());
      packetProcessor.registerHandler(RecvOpcode.FAMILY_SUMMON_RESPONSE, new FamilySummonResponseHandler());
      packetProcessor.registerHandler(RecvOpcode.USE_HAMMER, new UseHammerHandler());
      packetProcessor.registerHandler(RecvOpcode.SCRIPTED_ITEM, new ScriptedItemHandler());
      packetProcessor.registerHandler(RecvOpcode.TOUCHING_REACTOR, new TouchReactorHandler());
      packetProcessor.registerHandler(RecvOpcode.BEHOLDER, new BeholderHandler());
      packetProcessor.registerHandler(RecvOpcode.ADMIN_LOG, new AdminLogHandler());
      packetProcessor.registerHandler(RecvOpcode.DENY_ALLIANCE_REQUEST, new DenyAllianceRequestHandler());
      packetProcessor.registerHandler(RecvOpcode.USE_SOLOMON_ITEM, new UseSolomonHandler());
      packetProcessor.registerHandler(RecvOpcode.USE_GACHA_EXP, new UseGachaExpHandler());
      packetProcessor.registerHandler(RecvOpcode.NEW_YEAR_CARD_REQUEST, new NewYearCardHandler());
      packetProcessor.registerHandler(RecvOpcode.CASHSHOP_SURPRISE, new CashShopSurpriseHandler());
      packetProcessor.registerHandler(RecvOpcode.USE_ITEM_REWARD, new ItemRewardHandler());
      packetProcessor.registerHandler(RecvOpcode.USE_REMOTE, new RemoteGachaponHandler());
      packetProcessor.registerHandler(RecvOpcode.ACCEPT_FAMILY, new AcceptFamilyHandler());
      packetProcessor.registerHandler(RecvOpcode.DUEY_ACTION, new DueyHandler());
      packetProcessor.registerHandler(RecvOpcode.USE_DEATHITEM, new UseDeathItemHandler());
      packetProcessor.registerHandler(RecvOpcode.PLAYER_MAP_TRANSFER, new PlayerMapTransitionHandler());
      packetProcessor.registerHandler(RecvOpcode.USE_MAPLELIFE, new UseMapleLifeHandler());
      packetProcessor.registerHandler(RecvOpcode.USE_CATCH_ITEM, new UseCatchItemHandler());
      packetProcessor.registerHandler(RecvOpcode.FIELD_DAMAGE_MOB, new FieldDamageMobHandler());
      packetProcessor.registerHandler(RecvOpcode.MOB_DAMAGE_MOB_FRIENDLY, new MobDamageMobFriendlyHandler());
      packetProcessor.registerHandler(RecvOpcode.PARTY_SEARCH_REGISTER, new PartySearchRegisterHandler());
      packetProcessor.registerHandler(RecvOpcode.PARTY_SEARCH_START, new PartySearchStartHandler());
      packetProcessor.registerHandler(RecvOpcode.PARTY_SEARCH_UPDATE, new PartySearchUpdateHandler());
      packetProcessor.registerHandler(RecvOpcode.LEFT_KNOCKBACK, new LeftKnockbackHandler());
      packetProcessor.registerHandler(RecvOpcode.SNOWBALL, new SnowballHandler());
      packetProcessor.registerHandler(RecvOpcode.COCONUT, new CoconutHandler());
      packetProcessor.registerHandler(RecvOpcode.ARAN_COMBO_COUNTER, new AranComboHandler());
      packetProcessor.registerHandler(RecvOpcode.CLICK_GUIDE, new ClickGuideHandler());
      packetProcessor.registerHandler(RecvOpcode.FREDRICK_ACTION, new FredrickHandler());
      packetProcessor.registerHandler(RecvOpcode.REMOTE_STORE, new RemoteStoreHandler());
      packetProcessor.registerHandler(RecvOpcode.WEDDING_ACTION, new WeddingHandler());
      packetProcessor.registerHandler(RecvOpcode.WEDDING_TALK, new WeddingTalkHandler());
      packetProcessor.registerHandler(RecvOpcode.WEDDING_TALK_MORE, new WeddingTalkMoreHandler());
      packetProcessor.registerHandler(RecvOpcode.WATER_OF_LIFE, new UseWaterOfLifeHandler());
      packetProcessor.registerHandler(RecvOpcode.OPEN_ITEMUI, new RaiseUIStateHandler());
      packetProcessor.registerHandler(RecvOpcode.USE_ITEMUI, new RaiseIncExpHandler());
      packetProcessor.registerHandler(RecvOpcode.CLOSE_RANGE_ATTACK, new CloseRangeDamageHandler());
      packetProcessor.registerHandler(RecvOpcode.RANGED_ATTACK, new RangedAttackHandler());
      packetProcessor.registerHandler(RecvOpcode.MAGIC_ATTACK, new MagicDamageHandler());
      packetProcessor.registerHandler(RecvOpcode.SUMMON_ATTACK, new SummonDamageHandler());
      packetProcessor.registerHandler(RecvOpcode.TOUCH_MONSTER_ATTACK, new TouchMonsterDamageHandler());
      packetProcessor.registerHandler(RecvOpcode.USE_FAMILY, new FamilyUseHandler());
      packetProcessor.registerHandler(RecvOpcode.ITEM_SORT2, new InventorySortHandler());
      packetProcessor.registerHandler(RecvOpcode.MONSTER_CARNIVAL, new MonsterCarnivalHandler());
      packetProcessor.registerHandler(RecvOpcode.TAKE_DAMAGE, new TakeDamageHandler());
      packetProcessor.registerHandler(RecvOpcode.AUTO_DISTRIBUTE_AP, new AutoAssignHandler());
      packetProcessor.registerHandler(RecvOpcode.ADMIN_CHAT, new AdminChatHandler());
      packetProcessor.registerHandler(RecvOpcode.MOVE_PET, new MovePetHandler());
      packetProcessor.registerHandler(RecvOpcode.ALLIANCE_OPERATION, new AllianceOperationHandler());
      packetProcessor.registerHandler(RecvOpcode.MAKER_SKILL, new MakerSkillHandler());
      packetProcessor.registerHandler(RecvOpcode.ADMIN_COMMAND, new AdminCommandHandler());
      packetProcessor.registerHandler(RecvOpcode.MOVE_LIFE, new MoveLifeHandler());
      packetProcessor.registerHandler(RecvOpcode.MOVE_PLAYER, new MovePlayerHandler());
      packetProcessor.registerHandler(RecvOpcode.MOVE_SUMMON, new MoveSummonHandler());
      packetProcessor.registerHandler(RecvOpcode.MOVE_DRAGON, new MoveDragonHandler());
      packetProcessor.registerHandler(RecvOpcode.SPECIAL_MOVE, new SpecialMoveHandler());
   }
}
