package rest.master;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ms.engine.rest.CharacterStatisticsAttributes;
import com.ms.engine.rest.InfoTextAttributes;
import com.ms.engine.rest.IntroAttributes;
import com.ms.engine.rest.MountAttributes;
import com.ms.engine.rest.TitleAttributes;
import com.ms.engine.rest.builders.CharacterStatisticsAttributesBuilder;
import com.ms.engine.rest.builders.MountAttributesBuilder;

import builder.ResultBuilder;
import builder.ResultObjectBuilder;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleMount;
import client.inventory.Item;
import client.inventory.MaplePet;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.processor.ItemProcessor;
import client.processor.PetProcessor;
import config.YamlConfig;
import constants.MapleInventoryType;
import constants.MapleJob;
import constants.PetFlag;
import database.DatabaseConnection;
import database.provider.CharacterProvider;
import net.server.Server;
import rest.CanHoldAllAttributes;
import rest.CanHoldAttributes;
import rest.CanHoldItem;
import rest.CharacterAttributes;
import rest.CharacterItemAttributes;
import rest.CharacterSkillAttributes;
import rest.CheckSpaceAttributes;
import rest.CheckSpaceResponseAttributes;
import rest.FamilyAttributes;
import rest.FreeSlotAttributes;
import rest.HintAttributes;
import rest.InfoPlayerInteractionAttributes;
import rest.InputBody;
import rest.MessageAttributes;
import rest.MonsterBookAttributes;
import rest.NoopAttributes;
import rest.NpcConversationAttributes;
import rest.NpcCoolDownAttributes;
import rest.PetAttributes;
import rest.QuestCompletionAttributes;
import rest.QuestFinishAttributes;
import rest.QuestTimeLimitAttributes;
import rest.QuestUpdateAttributes;
import rest.SoundAttributes;
import rest.builders.CharacterAttributesBuilder;
import rest.builders.CharacterItemAttributesBuilder;
import rest.builders.CharacterSkillAttributesBuilder;
import rest.builders.CheckSpaceResponseAttributesBuilder;
import rest.builders.FamilyAttributesBuilder;
import rest.builders.FreeSlotAttributesBuilder;
import rest.builders.MonsterBookAttributesBuilder;
import rest.builders.PetAttributesBuilder;
import scripting.event.EventManager;
import server.MapleItemInformationProvider;
import server.processor.NpcConversationProcessor;
import server.processor.PlayerInteractionProcessor;
import tools.I18nMessage;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.foreigneffect.ShowForeignEffect;
import tools.packet.quest.info.QuestExpire;
import tools.packet.quest.info.QuestFinish;
import tools.packet.quest.info.RemoveQuestTimeLimit;
import tools.packet.showitemgaininchat.ShowItemGainInChat;
import tools.packet.showitemgaininchat.ShowSpecialEffect;

@Path("characters")
public class CharacterResource {
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getCharacters() {

      ResultBuilder resultBuilder = new ResultBuilder();

      DatabaseConnection.getInstance().withConnection(entityManager ->
            CharacterProvider.getInstance().getAllCharacters(entityManager).forEach(character ->
                  resultBuilder.addData(new ResultObjectBuilder(CharacterAttributes.class, character.id())
                        .setAttribute(new CharacterAttributesBuilder()
                              .setAccountId(character.accountId())
                              .setName(character.name())
                        )
                  )));
      return resultBuilder.build();
   }

   protected Response forCharacter(int characterId, BiConsumer<MapleCharacter, ResultBuilder> consumer) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);

      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return resultBuilder.build();
      }

      Server.getInstance().getWorld(worldId).getPlayerStorage()
            .getCharacterById(characterId).ifPresent(character -> consumer.accept(character, resultBuilder));
      return resultBuilder.build();
   }

   @GET
   @Path("/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getCharacter(@PathParam("id") Integer characterId) {
      return forCharacter(characterId, this::handleGetCharacter);
   }

   protected void handleGetCharacter(MapleCharacter character, ResultBuilder resultBuilder) {
      resultBuilder.addData(new ResultObjectBuilder(CharacterAttributes.class, character.getId())
            .setAttribute(new CharacterAttributesBuilder()
                  .setMapId(character.getMapId())
                  .setJobId(character.getJob().getId())
                  .setJobStyle(character.getJobStyle().ordinal())
                  .setLevel(character.getLevel())
                  .setExperience(character.getExp())
                  .setMeso(character.getMeso())
                  .setGm(character.isGM())
                  .setGender(character.getGender())
                  .setX(character.position().x)
                  .setY(character.position().y)
                  .setRemainingSp(character.getRemainingSp())
            )
      );
   }

   @PATCH
   @Path("/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response updateCharacter(@PathParam("id") Integer characterId, InputBody<CharacterAttributes> inputBody) {
      return forCharacter(characterId, (character, resultBuilder) -> handleUpdateCharacter(inputBody, character, resultBuilder));
   }

   protected void handleUpdateCharacter(InputBody<CharacterAttributes> inputBody, MapleCharacter character,
                                        ResultBuilder resultBuilder) {
      resultBuilder.setStatus(Response.Status.NO_CONTENT);
      Integer experienceGain = inputBody.attribute(CharacterAttributes::getExperience);
      if (experienceGain != null) {
         if (!YamlConfig.config.server.USE_QUEST_RATE) {
            character.gainExp(experienceGain * character.getExpRate(), true, true);
         } else {
            character.gainExp(experienceGain * character.getQuestExpRate(), true, true);
         }
      }
      Integer fameGain = inputBody.attribute(CharacterAttributes::getFame);
      if (fameGain != null) {
         character.gainFame(fameGain);
      }
      Integer mesoGain = inputBody.attribute(CharacterAttributes::getMeso);
      if (mesoGain != null) {
         if (mesoGain < 0) {
            character.gainMeso(mesoGain, true, false, true);
         } else {
            if (!YamlConfig.config.server.USE_QUEST_RATE) {
               character.gainMeso(mesoGain * character.getMesoRate(), true, false, true);
            } else {
               character.gainMeso(mesoGain * character.getQuestMesoRate(), true, false, true);
            }
         }
      }
      Integer jobId = inputBody.attribute(CharacterAttributes::getJobId);
      if (jobId != null) {
         character.changeJob(MapleJob.getById(jobId));
      }
   }

   @GET
   @Path("/{id}/statistics")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getCharacterStatistics(@PathParam("id") Integer characterId) {
      return forCharacter(characterId, this::handleGetCharacterStatistics);
   }

   protected void handleGetCharacterStatistics(MapleCharacter character, ResultBuilder resultBuilder) {
      resultBuilder.addData(new ResultObjectBuilder(CharacterStatisticsAttributes.class, character.getId())
            .setAttribute(new CharacterStatisticsAttributesBuilder()
                  .setStrength(character.getStr())
                  .setDexterity(character.getDex())
                  .setLuck(character.getLuk())
                  .setIntelligence(character.getHp())
                  .setHp(character.getHp())
            )
      );
   }

   @PATCH
   @Path("/{id}/statistics")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response updateCharacterStatistics(@PathParam("id") Integer characterId,
                                             InputBody<CharacterStatisticsAttributes> inputBody) {
      return forCharacter(characterId,
            (character, resultBuilder) -> handleUpdateCharacterStatistics(inputBody, character, resultBuilder));
   }

   protected void handleUpdateCharacterStatistics(InputBody<CharacterStatisticsAttributes> inputBody, MapleCharacter character,
                                                  ResultBuilder resultBuilder) {
      resultBuilder.setStatus(Response.Status.NO_CONTENT);

      Integer hp = inputBody.attribute(CharacterStatisticsAttributes::getHp);
      if (hp != null) {
         character.updateHp(hp);
      }
   }

   @DELETE
   @Path("/{id}/statistics")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response resetStatistics(@PathParam("id") Integer characterId) {
      return forCharacter(characterId, this::handleResetCharacterStatistics);
   }

   protected void handleResetCharacterStatistics(MapleCharacter character, ResultBuilder resultBuilder) {
      resultBuilder.setStatus(Response.Status.NO_CONTENT);
      character.resetStats();
   }

   @GET
   @Path("/{id}/pets")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getCharactersPets(@PathParam("id") Integer characterId, @QueryParam("slot") Short slot) {
      return forCharacter(characterId, (character, resultBuilder) -> handleGetCharacterPets(character, resultBuilder, slot));
   }

   protected void handleGetCharacterPets(MapleCharacter character, ResultBuilder resultBuilder, Short slot) {
      Predicate<MaplePet> filter = questStatus -> true;
      if (slot != null) {
         filter = pet -> pet.position() == slot;
      }
      Arrays.stream(character.getPets())
            .filter(Objects::nonNull)
            .filter(filter)
            .forEach(pet -> resultBuilder.addData(new ResultObjectBuilder(PetAttributes.class, pet.uniqueId())
                  .setAttribute(new PetAttributesBuilder()
                        .setSlot(pet.position())
                        .setFlag(pet.flag())
                        .setCloseness(pet.closeness()))));
   }

   @PATCH
   @Path("/{id}/pets/{petId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response updatePet(@PathParam("id") Integer characterId, @PathParam("petId") Integer petId,
                             InputBody<PetAttributes> inputBody) {
      return forCharacter(characterId, (character, resultBuilder) -> handleUpdatePet(character, petId, inputBody, resultBuilder));
   }

   protected void handleUpdatePet(MapleCharacter character, Integer petId, InputBody<PetAttributes> inputBody,
                                  ResultBuilder resultBuilder) {
      short slot = character.getPetIndex(petId);
      if (slot < 0 || slot > 3) {
         return;
      }
      resultBuilder.setStatus(Response.Status.NO_CONTENT);
      Short flag = inputBody.attribute(PetAttributes::getFlag);
      if (flag != null) {
         character.updateAndGetPet(slot, pet -> {
            pet.setFlag(ItemProcessor.getInstance().setFlag(pet.id(), flag));
            return pet;
         });
      }
      Integer petFlag = inputBody.attribute(PetAttributes::getPetFlag);
      if (petFlag != null) {
         PetProcessor.getInstance().addPetFlag(character, (byte) slot, PetFlag.getById(petFlag));
      }
      Integer closeness = inputBody.attribute(PetAttributes::getCloseness);
      if (closeness != null) {
         Integer fullness = inputBody.attribute(PetAttributes::getFullness);
         PetProcessor.getInstance().gainClosenessFullness(character, (byte) slot, closeness, fullness);
      }
   }

   @POST
   @Path("/{id}/buffs")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response gainBuff(@PathParam("id") Integer characterId, InputBody<NoopAttributes> inputBody) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         int itemEffect = inputBody.idAsInt();
         MapleItemInformationProvider.getInstance().getItemEffect(itemEffect).applyTo(character);
      });
   }

   @GET
   @Path("/{id}/buffs")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getBuff(@PathParam("id") Integer characterId,
                           @QueryParam("filter[sourceId]") Integer sourceId) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         if (sourceId != null) {
            boolean hasBuff = character.hasBuffFromSourceId(sourceId);
            if (hasBuff) {
               resultBuilder.setStatus(Response.Status.OK);
            }
         }
      });
   }

   @GET
   @Path("/{id}/buffSources/{statId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getBuffSource(@PathParam("id") Integer characterId,
                                 @PathParam("statId") String statId) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         MapleBuffStat stat = MapleBuffStat.valueOf(statId);
         int sourceId = character.getBuffSource(stat);
         resultBuilder.setStatus(Response.Status.OK);
         resultBuilder.addData(new ResultObjectBuilder(NoopAttributes.class, sourceId));
      });
   }

   @GET
   @Path("/{id}/skills/{skillId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getSkill(@PathParam("id") Integer characterId,
                            @PathParam("skillId") Integer skillId) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         resultBuilder.setStatus(Response.Status.OK);
         resultBuilder.addData(new ResultObjectBuilder(CharacterSkillAttributes.class, skillId)
               .setAttribute(new CharacterSkillAttributesBuilder()
                     .setLevel(character.getSkillLevel(skillId))
                     .setMasterLevel(character.getMasterLevel(skillId))
                     .setExpiration(character.getSkillExpiration(skillId))
               ));
      });
   }

   @POST
   @Path("/{id}/skills")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getSkill(@PathParam("id") Integer characterId, InputBody<CharacterSkillAttributes> inputBody) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         resultBuilder.setStatus(Response.Status.NO_CONTENT);
         character.getAbstractPlayerInteraction().teachSkill(
               inputBody.idAsInt(),
               inputBody.attribute(CharacterSkillAttributes::getLevel).byteValue(),
               inputBody.attribute(CharacterSkillAttributes::getMasterLevel).byteValue(),
               inputBody.attribute(CharacterSkillAttributes::getExpiration)
         );
      });
   }

   @POST
   @Path("/{id}/checkSpace")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response checkSpace(@PathParam("id") Integer characterId, InputBody<CheckSpaceAttributes> inputBody) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         resultBuilder.setStatus(Response.Status.OK);
         int result = MapleInventoryManipulator.checkSpaceProgressively(character.getClient(),
               inputBody.attribute(CheckSpaceAttributes::getItemId),
               inputBody.attribute(CheckSpaceAttributes::getQuantity),
               inputBody.attribute(CheckSpaceAttributes::getOwner),
               inputBody.attribute(CheckSpaceAttributes::getUsedSlots),
               inputBody.attribute(CheckSpaceAttributes::getUseProofInventory)
         );
         resultBuilder.addData(new ResultObjectBuilder(CheckSpaceResponseAttributes.class, 0)
               .setAttribute(new CheckSpaceResponseAttributesBuilder().setResult(result))
         );
      });
   }

   @POST
   @Path("/{id}/items/canHold")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response canHoldAllAfterRemoving(@PathParam("id") Integer characterId,
                                           InputBody<CanHoldAllAttributes> inputBody) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         List<Integer> toAddItemIds = inputBody.attribute(CanHoldAllAttributes::getAddable).stream()
               .map(CanHoldItem::getItemId).collect(Collectors.toList());
         List<Integer> toAddQuantity = inputBody.attribute(CanHoldAllAttributes::getAddable).stream()
               .map(CanHoldItem::getQuantity).collect(Collectors.toList());
         List<Integer> toRemoveItemIds = inputBody.attribute(CanHoldAllAttributes::getRemovables).stream()
               .map(CanHoldItem::getItemId).collect(Collectors.toList());
         List<Integer> toRemoveQuantity = inputBody.attribute(CanHoldAllAttributes::getRemovables).stream()
               .map(CanHoldItem::getQuantity).collect(Collectors.toList());

         boolean canHold = character.getAbstractPlayerInteraction()
               .canHoldAllAfterRemoving(toAddItemIds, toAddQuantity, toRemoveItemIds, toRemoveQuantity);
         if (canHold) {
            resultBuilder.setStatus(Response.Status.NO_CONTENT);
            resultBuilder.addData(new ResultObjectBuilder(NoopAttributes.class, 0));
         }
      });
   }

   @POST
   @Path("/{id}/items/{itemId}/canHold")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response canHold(@PathParam("id") Integer characterId, @PathParam("itemId") Integer itemId,
                           InputBody<CanHoldAttributes> inputBody) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         boolean canHold = character.canHold(itemId, inputBody.attribute(CanHoldAttributes::getQuantity));
         if (canHold) {
            resultBuilder.setStatus(Response.Status.NO_CONTENT);
            resultBuilder.addData(new ResultObjectBuilder(NoopAttributes.class, 0));
         }
      });
   }

   @GET
   @Path("/{id}/inventories/{type}/freeSlots")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getInventoryItems(@PathParam("id") Integer characterId, @PathParam("type") String type,
                                     @QueryParam("filter[itemId]") Integer itemId,
                                     @QueryParam("filter[quantity]") Integer quantity) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         MapleInventoryType inventoryType = MapleInventoryType.getByName(type);
         if (inventoryType == null) {
            return;
         }
         if (itemId == null && quantity == null) {
            resultBuilder.addData(new ResultObjectBuilder(FreeSlotAttributes.class, type)
                  .setAttribute(new FreeSlotAttributesBuilder()
                        .setCount((int) character.getInventory(inventoryType).getNumFreeSlot())
                  ));
            return;
         }

         if (itemId == null || quantity == null) {
            return;
         }

         resultBuilder.addData(new ResultObjectBuilder(FreeSlotAttributes.class, type)
               .setAttribute(new FreeSlotAttributesBuilder()
                     .setCount(character.getInventory(inventoryType).freeSlotCountById(itemId, quantity))
               ));
      });
   }

   @GET
   @Path("/{id}/inventories/{type}/items")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getInventoryItems(@PathParam("id") Integer characterId, @PathParam("type") String type,
                                     @QueryParam("filter[itemId]") Integer itemId) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         MapleInventoryType inventoryType = MapleInventoryType.getByName(type);
         if (inventoryType == null) {
            return;
         }
         resultBuilder.setStatus(Response.Status.OK);
         character.getInventory(inventoryType)
               .listById(itemId)
               .forEach(item -> resultBuilder
                     .addData(new ResultObjectBuilder(CharacterItemAttributes.class, Integer.valueOf(item.position()))
                           .setAttribute(new CharacterItemAttributesBuilder()
                                 .setItemId(item.id())
                                 .setQuantity(item.quantity())
                           )
                     ));
      });
   }

   @PATCH
   @Path("/{id}/inventories/{type}/items")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response addItem(@PathParam("id") Integer characterId, @PathParam("type") String type,
                           @DefaultValue("true") @QueryParam("showMessage") Boolean showMessage,
                           InputBody<CharacterItemAttributes> inputBody) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         resultBuilder.setStatus(Response.Status.OK);

         if (inputBody.attribute(CharacterItemAttributes::getQuantity) >= 0) {
            MapleInventoryManipulator.addById(character.getClient(),
                  inputBody.attribute(CharacterItemAttributes::getItemId),
                  inputBody.attribute(CharacterItemAttributes::getQuantity),
                  inputBody.attribute(CharacterItemAttributes::getOwner),
                  inputBody.attribute(CharacterItemAttributes::getPetId),
                  inputBody.attribute(CharacterItemAttributes::getExpiration));
            if (showMessage) {
               PacketCreator.announce(character, new ShowItemGainInChat(
                     inputBody.attribute(CharacterItemAttributes::getItemId),
                     inputBody.attribute(CharacterItemAttributes::getQuantity)));
            }
         } else {
            MapleInventoryType inventoryType = MapleInventoryType.getByName(type);
            MapleInventoryManipulator
                  .removeById(character.getClient(), inventoryType, inputBody.attribute(CharacterItemAttributes::getItemId),
                        inputBody.attribute(CharacterItemAttributes::getQuantity), true, false);
            if (showMessage) {
               PacketCreator.announce(character, new ShowItemGainInChat(
                     inputBody.attribute(CharacterItemAttributes::getItemId),
                     (short) (inputBody.attribute(CharacterItemAttributes::getQuantity) * -1)));
            }
         }
      });
   }

   @DELETE
   @Path("/{id}/inventories/{type}/items")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response removeFromSlot(@PathParam("id") Integer characterId, @PathParam("type") String type,
                                  @QueryParam("slot") Short slot) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         resultBuilder.setStatus(Response.Status.OK);
         MapleInventoryType typeEnum = MapleInventoryType.getByName(type);
         Item tempItem = character.getInventory(typeEnum).getItem(slot);
         MapleInventoryManipulator.removeFromSlot(character.getClient(), typeEnum, slot, tempItem.quantity(), false, false);
      });
   }

   @DELETE
   @Path("/{id}/inventories/{type}/items/{itemId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response removeById(@PathParam("id") Integer characterId, @PathParam("type") String type,
                              @PathParam("itemId") Integer itemId) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         resultBuilder.setStatus(Response.Status.OK);
         character.getAbstractPlayerInteraction().removeAll(itemId);
      });
   }

   @POST
   @Path("/{id}/message")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response sendMessage(@PathParam("id") Integer characterId, InputBody<MessageAttributes> inputBody) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         resultBuilder.setStatus(Response.Status.OK);
         MessageBroadcaster.getInstance()
               .sendServerNotice(character, ServerNoticeType.valueOf(inputBody.attribute(MessageAttributes::getType)),
                     I18nMessage.from(inputBody.attribute(MessageAttributes::getToken))
                           .with(inputBody.attribute(MessageAttributes::getReplacements)));
      });
   }

   @POST
   @Path("/{id}/quests/finishes")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response announceQuestFinish(@PathParam("id") Integer characterId, InputBody<QuestFinishAttributes> inputBody) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         resultBuilder.setStatus(Response.Status.OK);
         PacketCreator.announce(character, new QuestFinish(Short.parseShort(inputBody.id()),
               inputBody.attribute(QuestFinishAttributes::getNpcId),
               inputBody.attribute(QuestFinishAttributes::getNextQuestId)));
      });
   }

   @POST
   @Path("/{id}/quests/updates")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response announceQuestUpdate(@PathParam("id") Integer characterId, InputBody<QuestUpdateAttributes> inputBody) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         resultBuilder.setStatus(Response.Status.OK);
         character.announceUpdateQuest(inputBody.attributes());
      });
   }

   @POST
   @Path("/{id}/quests/points")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response addQuestPoints(@PathParam("id") Integer characterId) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         resultBuilder.setStatus(Response.Status.OK);
         character.awardQuestPoint(YamlConfig.config.server.QUEST_POINT_PER_QUEST_COMPLETE);
      });
   }

   @POST
   @Path("/{id}/quests/completions")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response announceQuestComplete(@PathParam("id") Integer characterId, InputBody<QuestCompletionAttributes> inputBody) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         resultBuilder.setStatus(Response.Status.OK);
         PacketCreator.announce(character, new ShowSpecialEffect(9)); // Quest completion
         character.getMap().broadcastMessage(character, new ShowForeignEffect(characterId, 9)); //use 9 instead of 12 for both
      });
   }

   @POST
   @Path("/{id}/quests/{questId}/expire")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response showQuestExpiry(@PathParam("id") Integer characterId, @PathParam("questId") Integer questId) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         resultBuilder.setStatus(Response.Status.OK);
         PacketCreator.announce(character, new QuestExpire(questId.shortValue()));
      });
   }

   @POST
   @Path("/{id}/quests/{questId}/limit")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response addQuestTimeLimit(@PathParam("id") Integer characterId, @PathParam("questId") Integer questId,
                                     InputBody<QuestTimeLimitAttributes> inputBody) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         if (inputBody.idAsInt() == 1) {
            character.questTimeLimit(questId, inputBody.attribute(QuestTimeLimitAttributes::getLimit).intValue());
         } else if (inputBody.idAsInt() == 2) {
            character.questTimeLimit2(questId, inputBody.attribute(QuestTimeLimitAttributes::getLimit));
         }
      });
   }

   @DELETE
   @Path("/{id}/quests/{questId}/limit")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response removeQuestLimit(@PathParam("id") Integer characterId, @PathParam("questId") Integer questId) {
      return forCharacter(characterId, (character, resultBuilder) -> handleRemoveQuestLimit(character, questId));
   }

   protected void handleRemoveQuestLimit(MapleCharacter character, Integer questId) {
      PacketCreator.announce(character, new RemoveQuestTimeLimit(questId.shortValue()));
   }

   @GET
   @Path("/{id}/monsterBook")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getCharactersMonsterBook(@PathParam("id") Integer characterId) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         resultBuilder.setStatus(Response.Status.OK);
         resultBuilder.addData(new ResultObjectBuilder(MonsterBookAttributes.class, characterId)
               .setAttribute(new MonsterBookAttributesBuilder()
                     .setLevel(character.getMonsterBook().getBookLevel())
                     .setCards(character.getMonsterBook().getCards())
               ));
      });
   }

   @GET
   @Path("/{id}/npcClick")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getNpcClick(@PathParam("id") Integer characterId) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         boolean can = character.getClient().canClickNPC();
         if (can) {
            resultBuilder.setStatus(Response.Status.OK);
         }
      });
   }

   @PATCH
   @Path("/{id}/npcClick")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response setNpcClick(@PathParam("id") Integer characterId) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         character.getClient().setClickedNPC();
         resultBuilder.setStatus(Response.Status.NO_CONTENT);
         resultBuilder.addData(new ResultObjectBuilder(NoopAttributes.class, characterId));
      });
   }

   @POST
   @Path("/{id}/flushDelayedUpdateQuests")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response flushDelayedUpdateQuests(@PathParam("id") Integer characterId) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         character.flushDelayedUpdateQuests();
         resultBuilder.setStatus(Response.Status.NO_CONTENT);
         resultBuilder.addData(new ResultObjectBuilder(NoopAttributes.class, characterId));
      });
   }

   @PATCH
   @Path("/{id}/npcCoolDown")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response setNpcCoolDown(@PathParam("id") Integer characterId, InputBody<NpcCoolDownAttributes> inputBody) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         character.setNpcCoolDown(inputBody.attribute(NpcCoolDownAttributes::getCoolDown));
         resultBuilder.setStatus(Response.Status.NO_CONTENT);
         resultBuilder.addData(new ResultObjectBuilder(NoopAttributes.class, characterId));
      });
   }

   @POST
   @Path("/{id}/npcs/{npcId}/conversations")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createNpcConversation(@PathParam("id") Integer characterId, @PathParam("npcId") Integer npcId,
                                         InputBody<NpcConversationAttributes> inputBody) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         I18nMessage message = I18nMessage.from(inputBody.attribute(NpcConversationAttributes::getToken));
         List<String> arguments = inputBody.attribute(NpcConversationAttributes::getArguments);
         message = message.with(arguments.toArray());

         switch (inputBody.attribute(NpcConversationAttributes::getType)) {
            case NEXT -> NpcConversationProcessor.getInstance()
                  .sendNext(character, npcId, message, inputBody.attribute(NpcConversationAttributes::getSpeaker));
            case PREVIOUS -> NpcConversationProcessor.getInstance()
                  .sendPrev(character, npcId, message, inputBody.attribute(NpcConversationAttributes::getSpeaker));
            case NEXT_PREVIOUS -> NpcConversationProcessor.getInstance()
                  .sendNextPrev(character, npcId, message, inputBody.attribute(NpcConversationAttributes::getSpeaker));
            case ACCEPT_DECLINE -> NpcConversationProcessor.getInstance()
                  .sendAcceptDecline(character, npcId, message, inputBody.attribute(NpcConversationAttributes::getSpeaker));
            case SIMPLE -> NpcConversationProcessor.getInstance()
                  .sendSimple(character, npcId, message, inputBody.attribute(NpcConversationAttributes::getSpeaker));
            case OK -> NpcConversationProcessor.getInstance()
                  .sendOk(character, npcId, message, inputBody.attribute(NpcConversationAttributes::getSpeaker));
            case YES_NO -> NpcConversationProcessor.getInstance()
                  .sendYesNo(character, npcId, message, inputBody.attribute(NpcConversationAttributes::getSpeaker));
            case GET_NUMBER -> NpcConversationProcessor.getInstance().sendGetNumber(character, npcId, message,
                  inputBody.attribute(NpcConversationAttributes::getDef),
                  inputBody.attribute(NpcConversationAttributes::getMin),
                  inputBody.attribute(NpcConversationAttributes::getMax));
         }
         resultBuilder.setStatus(Response.Status.NO_CONTENT);
         resultBuilder.addData(new ResultObjectBuilder(NoopAttributes.class, characterId));
      });
   }

   @POST
   @Path("/{id}/interactions/info")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createNpcConversation(@PathParam("id") Integer characterId,
                                         InputBody<InfoPlayerInteractionAttributes> inputBody) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         PlayerInteractionProcessor.getInstance()
               .showInfo(character, inputBody.attribute(InfoPlayerInteractionAttributes::getPath));
         resultBuilder.setStatus(Response.Status.NO_CONTENT);
         resultBuilder.addData(new ResultObjectBuilder(NoopAttributes.class, characterId));
      });
   }

   @POST
   @Path("/{id}/interactions/hints")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createHint(@PathParam("id") Integer characterId,
                              InputBody<HintAttributes> inputBody) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         PlayerInteractionProcessor.getInstance()
               .showHint(character, inputBody.attribute(HintAttributes::getMessage),
                     inputBody.attribute(HintAttributes::getWidth),
                     inputBody.attribute(HintAttributes::getHeight));
         resultBuilder.setStatus(Response.Status.NO_CONTENT);
         resultBuilder.addData(new ResultObjectBuilder(NoopAttributes.class, characterId));
      });
   }

   @POST
   @Path("/{id}/interactions/sounds")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createSound(@PathParam("id") Integer characterId,
                               InputBody<SoundAttributes> inputBody) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         PlayerInteractionProcessor.getInstance().playSound(character, inputBody.attribute(SoundAttributes::getPath));
         resultBuilder.setStatus(Response.Status.NO_CONTENT);
         resultBuilder.addData(new ResultObjectBuilder(NoopAttributes.class, characterId));
      });
   }

   @POST
   @Path("/{id}/interactions/guides/{guideId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createSound(@PathParam("id") Integer characterId, @PathParam("guideId") Integer guideId) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         PlayerInteractionProcessor.getInstance().guideHint(character, guideId);
         resultBuilder.setStatus(Response.Status.NO_CONTENT);
         resultBuilder.addData(new ResultObjectBuilder(NoopAttributes.class, characterId));
      });
   }

   @GET
   @Path("/{id}/family")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getFamilyInformation(@PathParam("id") Integer characterId) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         resultBuilder.setStatus(Response.Status.NO_CONTENT);
         resultBuilder.addData(new ResultObjectBuilder(FamilyAttributes.class, characterId)
               .setAttribute(new FamilyAttributesBuilder()
                     .setJuniorCount(character.getFamilyEntry().getJuniorCount())
               )
         );
      });
   }

   @GET
   @Path("/{id}/mount")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getMountInformation(@PathParam("id") Integer characterId) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         MapleMount mount = character.getMount();
         if (mount != null) {
            resultBuilder.setStatus(Response.Status.OK);
            resultBuilder.addData(new ResultObjectBuilder(MountAttributes.class, mount.id())
                  .setAttribute(new MountAttributesBuilder()
                        .setLevel(mount.level())
                  )
            );
         }
      });
   }

   @POST
   @Path("/{id}/titles")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response earnTitle(@PathParam("id") Integer characterId, InputBody<TitleAttributes> inputBody) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         String message = "<" + inputBody.attribute(TitleAttributes::getName) + "> has been awarded.";
         PlayerInteractionProcessor.getInstance().earnTitle(character, message);
         resultBuilder.setStatus(Response.Status.NO_CONTENT);
      });
   }

   @POST
   @Path("/{id}/intros")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response showIntro(@PathParam("id") Integer characterId, InputBody<IntroAttributes> inputBody) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         PlayerInteractionProcessor.getInstance().showIntro(character, inputBody.attribute(IntroAttributes::getPath));
         resultBuilder.setStatus(Response.Status.NO_CONTENT);
      });
   }

   @POST
   @Path("/{id}/infoTexts")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response showInfoText(@PathParam("id") Integer characterId, InputBody<InfoTextAttributes> inputBody) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         PlayerInteractionProcessor.getInstance().showInfoText(character, inputBody.attribute(InfoTextAttributes::getText));
         resultBuilder.setStatus(Response.Status.NO_CONTENT);
      });
   }

   @POST
   @Path("/{id}/events/{eventId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response startEvent(@PathParam("id") Integer characterId, @PathParam("eventId") String eventId) {
      return forCharacter(characterId, (character, resultBuilder) -> {
         EventManager eventManager = character.getClient().getEventManager(eventId);
         if (eventManager != null) {
            boolean result = eventManager.startInstance(character);
            if (result) {
               resultBuilder.setStatus(Response.Status.NO_CONTENT);
            }
         }
      });
   }
}
