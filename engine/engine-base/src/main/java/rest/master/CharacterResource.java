package rest.master;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import builder.ResultBuilder;
import builder.ResultObjectBuilder;
import client.MapleCharacter;
import client.inventory.MaplePet;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.processor.ItemProcessor;
import client.processor.PetProcessor;
import config.YamlConfig;
import constants.MapleInventoryType;
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
import rest.DataBody;
import rest.FreeSlotAttributes;
import rest.InputBody;
import rest.MessageAttributes;
import rest.MonsterBookAttributes;
import rest.NoopAttributes;
import rest.NpcAttributes;
import rest.PetAttributes;
import rest.QuestCompletionAttributes;
import rest.QuestFinishAttributes;
import rest.QuestTimeLimitAttributes;
import rest.QuestUpdateAttributes;
import rest.builders.CharacterAttributesBuilder;
import rest.builders.CharacterItemAttributesBuilder;
import rest.builders.CharacterSkillAttributesBuilder;
import rest.builders.CheckSpaceResponseAttributesBuilder;
import rest.builders.FreeSlotAttributesBuilder;
import rest.builders.MonsterBookAttributesBuilder;
import rest.builders.NpcAttributesBuilder;
import rest.builders.PetAttributesBuilder;
import server.MapleItemInformationProvider;
import server.life.MapleNPC;
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

   @GET
   @Path("/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getCharacter(@PathParam("id") Integer characterId) {
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return Response.status(Response.Status.NOT_FOUND).build();
      }

      ResultBuilder resultBuilder = new ResultBuilder();
      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId).ifPresent(character ->
            resultBuilder.addData(new ResultObjectBuilder(CharacterAttributes.class, characterId)
                  .setAttribute(new CharacterAttributesBuilder()
                        .setMapId(character.getMapId())
                        .setJobId(character.getJob().getId())
                        .setLevel(character.getLevel())
                        .setExperience(character.getExp())
                        .setMeso(character.getMeso())
                        .setGm(character.isGM())
                        .setX(character.position().x)
                        .setY(character.position().y)
                  )
            ));
      return resultBuilder.build();
   }

   @PATCH
   @Path("/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response updateCharacter(@PathParam("id") Integer characterId, InputBody<CharacterAttributes> inputBody) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return resultBuilder.build();
      }

      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId)
            .ifPresent(character -> {
               resultBuilder.setStatus(Response.Status.NO_CONTENT);

               Integer experienceGain = inputBody.getData().getAttributes().getExperience();
               if (experienceGain != null) {
                  if (!YamlConfig.config.server.USE_QUEST_RATE) {
                     character.gainExp(experienceGain * character.getExpRate(), true, true);
                  } else {
                     character.gainExp(experienceGain * character.getQuestExpRate(), true, true);
                  }
               }
               Integer fameGain = inputBody.getData().getAttributes().getFame();
               if (fameGain != null) {
                  character.gainFame(fameGain);
               }
               Integer mesoGain = inputBody.getData().getAttributes().getMeso();
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
            });
      return resultBuilder.build();
   }

   @GET
   @Path("/{id}/pets")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getCharactersPets(@PathParam("id") Integer characterId, @QueryParam("slot") Short slot) {
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return Response.status(Response.Status.NOT_FOUND).build();
      }

      Predicate<MaplePet> filter = questStatus -> true;
      if (slot != null) {
         filter = pet -> pet.position() == slot;
      }

      ResultBuilder resultBuilder = new ResultBuilder();
      Predicate<MaplePet> finalFilter = filter;
      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId).ifPresent(character ->
            Arrays.stream(character.getPets())
                  .filter(Objects::nonNull)
                  .filter(finalFilter)
                  .forEach(pet -> resultBuilder.addData(new ResultObjectBuilder(PetAttributes.class, pet.uniqueId())
                        .setAttribute(new PetAttributesBuilder()
                              .setSlot(pet.position())
                              .setFlag(pet.flag())
                              .setCloseness(pet.closeness()))))
      );
      return resultBuilder.build();
   }

   @PATCH
   @Path("/{id}/pets/{petId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response updatePet(@PathParam("id") Integer characterId, @PathParam("petId") Integer petId,
                             InputBody<PetAttributes> inputBody) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return resultBuilder.build();
      }

      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId)
            .ifPresent(character -> {
               short slot = character.getPetIndex(petId);
               if (slot < 0 || slot > 3) {
                  return;
               }
               resultBuilder.setStatus(Response.Status.NO_CONTENT);

               Short flag = inputBody.getData().getAttributes().getFlag();
               if (flag != null) {
                  character.updateAndGetPet(slot, pet -> {
                     pet.setFlag(ItemProcessor.getInstance().setFlag(pet.id(), flag));
                     return pet;
                  });
               }
               Integer petFlag = inputBody.getData().getAttributes().getPetFlag();
               if (petFlag != null) {
                  PetProcessor.getInstance().addPetFlag(character, (byte) slot, PetFlag.getById(petFlag));
               }
               Integer closeness = inputBody.getData().getAttributes().getCloseness();
               if (closeness != null) {
                  Integer fullness = inputBody.getData().getAttributes().getFullness();
                  PetProcessor.getInstance().gainClosenessFullness(character, (byte) slot, closeness, fullness);
               }
            });
      return resultBuilder.build();
   }

   @POST
   @Path("/{id}/buffs")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response gainBuff(@PathParam("id") Integer characterId, InputBody<NoopAttributes> inputBody) {
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return Response.status(Response.Status.NOT_FOUND).build();
      }

      int itemEffect = Integer.parseInt(inputBody.getData().getId());
      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId)
            .ifPresent(character -> MapleItemInformationProvider.getInstance().getItemEffect(itemEffect).applyTo(character));
      return Response.noContent().build();
   }

   @GET
   @Path("/{id}/buffs")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getBuff(@PathParam("id") Integer characterId,
                           @QueryParam("filter[sourceId]") Integer sourceId) {

      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return Response.status(Response.Status.NOT_FOUND).build();
      }

      Optional<MapleCharacter> character = Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId);
      if (character.isEmpty()) {
         return Response.status(Response.Status.NOT_FOUND).build();
      }

      if (sourceId != null) {
         boolean hasBuff = character.get().hasBuffFromSourceId(sourceId);
         if (hasBuff) {
            return Response.ok().build();
         } else {
            return Response.status(Response.Status.NOT_FOUND).build();
         }
      }
      return Response.status(Response.Status.NOT_FOUND).build();
   }

   @GET
   @Path("/{id}/skills/{skillId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getSkill(@PathParam("id") Integer characterId,
                            @PathParam("skillId") Integer skillId) {
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return Response.status(Response.Status.NOT_FOUND).build();
      }

      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);
      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId)
            .ifPresent(character -> {
               resultBuilder.setStatus(Response.Status.OK);
               resultBuilder.addData(new ResultObjectBuilder(CharacterSkillAttributes.class, skillId)
                     .setAttribute(new CharacterSkillAttributesBuilder()
                           .setLevel(character.getSkillLevel(skillId))
                           .setMasterLevel(character.getMasterLevel(skillId))
                     ));
            });
      return resultBuilder.build();
   }

   @POST
   @Path("/{id}/checkSpace")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response checkSpace(@PathParam("id") Integer characterId, InputBody<CheckSpaceAttributes> inputBody) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return resultBuilder.build();
      }

      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId)
            .ifPresent(character -> {
               resultBuilder.setStatus(Response.Status.OK);

               int result = MapleInventoryManipulator.checkSpaceProgressively(character.getClient(),
                     inputBody.getData().getAttributes().getItemId(),
                     inputBody.getData().getAttributes().getQuantity(),
                     inputBody.getData().getAttributes().getOwner(),
                     inputBody.getData().getAttributes().getUsedSlots(),
                     inputBody.getData().getAttributes().getUseProofInventory()
               );

               resultBuilder.addData(new ResultObjectBuilder(CheckSpaceResponseAttributes.class, 0)
                     .setAttribute(new CheckSpaceResponseAttributesBuilder().setResult(result))
               );
            });
      return resultBuilder.build();
   }

   @POST
   @Path("/{id}/items/canHold")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response canHoldAllAfterRemoving(@PathParam("id") Integer characterId,
                                           InputBody<CanHoldAllAttributes> inputBody) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return resultBuilder.build();
      }

      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId)
            .ifPresent(character -> {
               List<Integer> toAddItemIds = inputBody.getData().getAttributes()
                     .getAddable().stream()
                     .map(CanHoldItem::getItemId).collect(Collectors.toList());
               List<Integer> toAddQuantity = inputBody.getData().getAttributes()
                     .getAddable().stream()
                     .map(CanHoldItem::getQuantity).collect(Collectors.toList());
               List<Integer> toRemoveItemIds = inputBody.getData().getAttributes()
                     .getRemovables().stream()
                     .map(CanHoldItem::getItemId).collect(Collectors.toList());
               List<Integer> toRemoveQuantity = inputBody.getData().getAttributes()
                     .getRemovables().stream()
                     .map(CanHoldItem::getQuantity).collect(Collectors.toList());

               boolean canHold = character.getAbstractPlayerInteraction()
                     .canHoldAllAfterRemoving(toAddItemIds, toAddQuantity, toRemoveItemIds, toRemoveQuantity);
               if (canHold) {
                  resultBuilder.setStatus(Response.Status.NO_CONTENT);
                  resultBuilder.addData(new ResultObjectBuilder(NoopAttributes.class, 0));
               }
            });
      return resultBuilder.build();
   }

   @POST
   @Path("/{id}/items/{itemId}/canHold")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response canHold(@PathParam("id") Integer characterId, @PathParam("itemId") Integer itemId,
                           InputBody<CanHoldAttributes> inputBody) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return resultBuilder.build();
      }

      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId)
            .ifPresent(character -> {
               boolean canHold = character.canHold(itemId, inputBody.getData().getAttributes().getQuantity());
               if (canHold) {
                  resultBuilder.setStatus(Response.Status.OK);
                  resultBuilder.addData(new ResultObjectBuilder(NoopAttributes.class, 0));
               }
            });
      return resultBuilder.build();
   }

   @GET
   @Path("/{id}/inventories/{type}/freeSlots")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getInventoryItems(@PathParam("id") Integer characterId, @PathParam("type") String type,
                                     @QueryParam("filter[itemId]") Integer itemId,
                                     @QueryParam("filter[quantity]") Integer quantity) {
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return new ResultBuilder(Response.Status.NOT_FOUND).build();
      }

      MapleInventoryType inventoryType = MapleInventoryType.getByName(type);
      if (inventoryType == null) {
         return new ResultBuilder(Response.Status.NOT_FOUND).build();
      }

      if (itemId == null || quantity == null) {
         return new ResultBuilder(Response.Status.NOT_FOUND).build();
      }

      ResultBuilder resultBuilder = new ResultBuilder();
      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId)
            .ifPresent(character -> resultBuilder.addData(new ResultObjectBuilder(FreeSlotAttributes.class, type)
                  .setAttribute(new FreeSlotAttributesBuilder()
                        .setCount(character.getInventory(inventoryType).freeSlotCountById(itemId, quantity))
                  )));
      return resultBuilder.build();
   }

   @GET
   @Path("/{id}/inventories/{type}/items")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getInventoryItems(@PathParam("id") Integer characterId, @PathParam("type") String type,
                                     @QueryParam("filter[itemId]") Integer itemId) {
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return Response.status(Response.Status.NOT_FOUND).build();
      }

      MapleInventoryType inventoryType = MapleInventoryType.getByName(type);
      if (inventoryType == null) {
         return Response.status(Response.Status.NOT_FOUND).build();
      }

      ResultBuilder resultBuilder = new ResultBuilder();
      if (itemId != null) {
         Server.getInstance()
               .getWorld(worldId)
               .getPlayerStorage()
               .getCharacterById(characterId)
               .ifPresent(character ->
                     character.getInventory(inventoryType)
                           .listById(itemId)
                           .forEach(item -> resultBuilder
                                 .addData(new ResultObjectBuilder(CharacterItemAttributes.class, Integer.valueOf(item.position()))
                                       .setAttribute(new CharacterItemAttributesBuilder()
                                             .setItemId(item.id())
                                             .setQuantity(item.quantity())
                                       )
                                 )));
      }

      return resultBuilder.build();
   }

   @PATCH
   @Path("/{id}/inventories/{type}/items")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response addItem(@PathParam("id") Integer characterId, @PathParam("type") String type,
                           InputBody<CharacterItemAttributes> inputBody) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return resultBuilder.build();
      }

      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId)
            .ifPresent(character -> {
               resultBuilder.setStatus(Response.Status.OK);

               if (inputBody.getData().getAttributes().getQuantity() >= 0) {
                  MapleInventoryManipulator.addById(character.getClient(), inputBody.getData().getAttributes().getItemId(),
                        inputBody.getData().getAttributes().getQuantity(), inputBody.getData().getAttributes().getOwner(),
                        inputBody.getData().getAttributes().getPetId(), inputBody.getData().getAttributes().getExpiration());
                  PacketCreator.announce(character, new ShowItemGainInChat(inputBody.getData().getAttributes().getItemId(),
                        inputBody.getData().getAttributes().getQuantity()));
               } else {
                  MapleInventoryType inventoryType = MapleInventoryType.getByName(type);
                  MapleInventoryManipulator
                        .removeById(character.getClient(), inventoryType, inputBody.getData().getAttributes().getItemId(),
                              inputBody.getData().getAttributes().getQuantity(), true, false);
                  PacketCreator.announce(character, new ShowItemGainInChat(inputBody.getData().getAttributes().getItemId(),
                        (short) (inputBody.getData().getAttributes().getQuantity() * -1)));
               }
            });

      return resultBuilder.build();
   }

   @POST
   @Path("/{id}/message")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response sendMessage(@PathParam("id") Integer characterId, InputBody<MessageAttributes> inputBody) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return resultBuilder.build();
      }
      DataBody<MessageAttributes> data = inputBody.getData();
      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId)
            .ifPresent(character -> {
               resultBuilder.setStatus(Response.Status.OK);
               MessageBroadcaster.getInstance()
                     .sendServerNotice(character, ServerNoticeType.valueOf(data.getAttributes().getType()),
                           I18nMessage.from(data.getAttributes().getToken()).with(data.getAttributes().getReplacements()));
            });
      return resultBuilder.build();
   }

   @POST
   @Path("/{id}/quests/finishes")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response announceQuestFinish(@PathParam("id") Integer characterId, InputBody<QuestFinishAttributes> inputBody) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return resultBuilder.build();
      }
      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId)
            .ifPresent(character -> {
               resultBuilder.setStatus(Response.Status.OK);
               PacketCreator.announce(character, new QuestFinish(Short.parseShort(inputBody.getData().getId()),
                     inputBody.getData().getAttributes().getNpcId(), inputBody.getData().getAttributes().getNextQuestId()));
            });
      return resultBuilder.build();
   }

   @POST
   @Path("/{id}/quests/updates")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response announceQuestUpdate(@PathParam("id") Integer characterId, InputBody<QuestUpdateAttributes> inputBody) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return resultBuilder.build();
      }
      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId)
            .ifPresent(character -> {
               resultBuilder.setStatus(Response.Status.OK);
               character.announceUpdateQuest(inputBody.getData().getAttributes());
            });
      return resultBuilder.build();
   }

   @POST
   @Path("/{id}/quests/points")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response addQuestPoints(@PathParam("id") Integer characterId) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return resultBuilder.build();
      }
      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId)
            .ifPresent(character -> {
               resultBuilder.setStatus(Response.Status.OK);
               character.awardQuestPoint(YamlConfig.config.server.QUEST_POINT_PER_QUEST_COMPLETE);
            });
      return resultBuilder.build();
   }

   @POST
   @Path("/{id}/quests/completions")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response announceQuestComplete(@PathParam("id") Integer characterId, InputBody<QuestCompletionAttributes> inputBody) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return resultBuilder.build();
      }
      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId)
            .ifPresent(character -> {
               resultBuilder.setStatus(Response.Status.OK);
               PacketCreator.announce(character, new ShowSpecialEffect(9)); // Quest completion
               character.getMap().broadcastMessage(character, new ShowForeignEffect(characterId, 9)); //use 9 instead of 12 for both
            });
      return resultBuilder.build();
   }

   @POST
   @Path("/{id}/quests/{questId}/expire")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response showQuestExpiry(@PathParam("id") Integer characterId, @PathParam("questId") Integer questId) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return resultBuilder.build();
      }
      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId)
            .ifPresent(character -> {
               resultBuilder.setStatus(Response.Status.OK);
               PacketCreator.announce(character, new QuestExpire(questId.shortValue()));
            });
      return resultBuilder.build();
   }

   @POST
   @Path("/{id}/quests/{questId}/limit")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response addQuestTimeLimit(@PathParam("id") Integer characterId, @PathParam("questId") Integer questId,
                                     InputBody<QuestTimeLimitAttributes> inputBody) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return resultBuilder.build();
      }
      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId)
            .ifPresent(character -> {
               if (inputBody.getData().getId().equals("1")) {
                  character.questTimeLimit(questId, inputBody.getData().getAttributes().getLimit().intValue());
               } else if (inputBody.getData().getId().equals("2")) {
                  character.questTimeLimit2(questId, inputBody.getData().getAttributes().getLimit());
               }
            });
      return new ResultBuilder().build();
   }

   @DELETE
   @Path("/{id}/quests/{questId}/limit")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response removeQuestLimit(@PathParam("id") Integer characterId, @PathParam("questId") Integer questId) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return resultBuilder.build();
      }
      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId)
            .ifPresent(character -> PacketCreator.announce(character, new RemoveQuestTimeLimit(questId.shortValue())));
      return new ResultBuilder().build();
   }

   @GET
   @Path("/{id}/monsterBook")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getCharactersMonsterBook(@PathParam("id") Integer characterId) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return resultBuilder.build();
      }
      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId)
            .ifPresent(character -> resultBuilder.addData(new ResultObjectBuilder(MonsterBookAttributes.class, characterId)
                  .setAttribute(new MonsterBookAttributesBuilder()
                        .setLevel(character.getMonsterBook().getBookLevel())
                        .setCards(character.getMonsterBook().getCards())
                  )));
      return resultBuilder.build();
   }

   @GET
   @Path("/{id}/map/npcs/{npcId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getNpcInCharacterMap(@PathParam("id") Integer characterId, @PathParam("npcId") Integer npcId) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      if (worldId == -1) {
         return resultBuilder.build();
      }
      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId)
            .ifPresent(character -> {
               MapleNPC npc = character.getMap().getNPCById(npcId);
               if (npc == null) {
                  return;
               }
               resultBuilder.setStatus(Response.Status.OK);
               resultBuilder.addData(new ResultObjectBuilder(NpcAttributes.class, npcId)
                     .setAttribute(new NpcAttributesBuilder()
                           .setX(npc.position().x)
                           .setY(npc.position().y)
                     )
               );
            });
      return resultBuilder.build();
   }
}
