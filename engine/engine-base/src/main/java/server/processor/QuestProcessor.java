package server.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import builder.InputObjectBuilder;
import builder.ResultObjectBuilder;
import client.MapleCharacter;
import rest.CharacterQuestAttributes;
import rest.CharacterQuestProgressAttributes;
import rest.DataBody;
import rest.DataContainer;
import rest.InfoExAttributes;
import rest.InfoNumberAttributes;
import rest.NoopAttributes;
import rest.QuestAttributes;
import rest.QuestCompleteAttributes;
import rest.QuestItemAttributes;
import rest.QuestProgressAttributes;
import rest.QuestStartAttributes;
import rest.RestService;
import rest.UriBuilder;
import rest.builders.CharacterQuestAttributesBuilder;
import rest.builders.QuestCompleteAttributesBuilder;
import rest.builders.QuestProgressAttributesBuilder;
import rest.builders.QuestStartAttributesBuilder;
import scripting.quest.QuestScriptManager;
import tools.I18nMessage;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class QuestProcessor {
   private static final Object lock = new Object();

   private static volatile QuestProcessor instance;

   public static QuestProcessor getInstance() {
      QuestProcessor result = instance;
      if (result == null) {
         synchronized (lock) {
            result = instance;
            if (result == null) {
               result = new QuestProcessor();
               instance = result;
            }
         }
      }
      return result;
   }

   private QuestProcessor() {
   }

   //TODO transform this to local model
   public CharacterQuestAttributes getQuest(MapleCharacter character, int questId) {
      return UriBuilder.service(RestService.QUEST)
            .path("characters").path(character.getId()).path("quests").path(questId)
            .getRestClient(CharacterQuestAttributes.class)
            .getWithResponse()
            .result()
            .map(container -> container.getData().getAttributes())
            .orElseThrow();
   }

   //TODO transform this to local model
   public List<CharacterQuestAttributes> getQuests(MapleCharacter character) {
      return UriBuilder.service(RestService.QUEST)
            .path("characters").path(character.getId()).path("quests")
            .getRestClient(CharacterQuestAttributes.class)
            .getWithResponse()
            .result()
            .map(container -> container.getDataAsList().stream().map(DataBody::getAttributes).collect(Collectors.toList()))
            .orElse(new ArrayList<>());
   }

   public List<CharacterQuestAttributes> getStartedQuests(MapleCharacter character) {
      return getQuestsByStatus(character, "STARTED");
   }

   public List<CharacterQuestAttributes> getCompletedQuests(MapleCharacter character) {
      return getQuestsByStatus(character, "COMPLETED");
   }

   protected List<CharacterQuestAttributes> getQuestsByStatus(MapleCharacter character, String status) {
      return UriBuilder.service(RestService.QUEST)
            .path("characters").path(character.getId()).path("quests").queryParam("status", status)
            .getRestClient(CharacterQuestAttributes.class)
            .getWithResponse()
            .result()
            .map(container -> container.getDataAsList().stream().map(DataBody::getAttributes).collect(Collectors.toList()))
            .orElse(new ArrayList<>());
   }

   public short getInfoNumber(int questId, String status) {
      return UriBuilder.service(RestService.QUEST)
            .path("quests").path(questId).path("infoNumber").queryParam("status", status)
            .getRestClient(InfoNumberAttributes.class)
            .getWithResponse()
            .result()
            .map(container -> container.getData().getAttributes().getInfoNumber())
            .orElseThrow();
   }

   public void raiseQuestMobCount(MapleCharacter character, int mobId) {
      UriBuilder.service(RestService.QUEST).path("characters").path(character.getId()).path("mobs").path(mobId)
            .getRestClient()
            .create();
   }

   public void setQuestProgress(MapleCharacter character, int questId, int infoNumber, String progress) {
      String questStatus = getStatus(character, questId).orElseThrow();
      short questInfoNumber = getInfoNumber(questId, questStatus);
      int key = infoNumber;
      short requestId = (short) questId;
      if (questInfoNumber == infoNumber && infoNumber > 0) {
         key = 0;
         requestId = questInfoNumber;
      }

      UriBuilder.service(RestService.QUEST)
            .path("characters").path(character.getId()).path("quests").path((int) requestId).path("progress").path(key)
            .getRestClient()
            .update(new InputObjectBuilder().setData(new ResultObjectBuilder(QuestProgressAttributes.class, questId)
                  .setAttribute(new QuestProgressAttributesBuilder().setProgress(progress)).build()));
   }

   public void startQuest(int characterId, int questId, int npcId, int x, int y) {
      UriBuilder.service(RestService.QUEST).path("characters").path(characterId).path("quests")
            .getRestClient()
            .create(new InputObjectBuilder().setData(new ResultObjectBuilder(QuestStartAttributes.class, questId)
                  .setAttribute(new QuestStartAttributesBuilder()
                        .setNpcId(npcId)
                        .setPacketX(x)
                        .setPacketY(y)
                  )
                  .build()));
   }

   public void startScriptedQuest(MapleCharacter character, int questId, int npcId, int x, int y) {
      UriBuilder.service(RestService.QUEST)
            .path("characters").path(character.getId()).path("quests").queryParam("scripted", true)
            .getRestClient()
            .success((code, body) -> {
               boolean hasScriptRequirement = UriBuilder.service(RestService.QUEST).path("quests").path(questId)
                     .getRestClient(QuestAttributes.class)
                     .getWithResponse()
                     .result()
                     .map(container -> container.getData().getAttributes().getHasScriptRequirement())
                     .orElse(false);
               QuestScriptManager.getInstance().start(character.getClient(), (short) questId, npcId, hasScriptRequirement);
            })
            .create(new InputObjectBuilder().setData(new ResultObjectBuilder(QuestStartAttributes.class, questId)
                  .setAttribute(new QuestStartAttributesBuilder()
                        .setNpcId(npcId)
                        .setPacketX(x)
                        .setPacketY(y)
                  )
                  .build()
            ));
   }

   public void completeQuest(int characterId, int questId, int npcId, int selection, int x, int y) {
      UriBuilder.service(RestService.QUEST)
            .path("characters").path(characterId).path("quests").path(questId).path("complete")
            .getRestClient()
            .create(new InputObjectBuilder().setData(new ResultObjectBuilder(QuestCompleteAttributes.class, questId)
                  .setAttribute(new QuestCompleteAttributesBuilder()
                        .setNpcId(npcId)
                        .setSelection(selection)
                        .setPacketX(x)
                        .setPacketY(y)
                  )
                  .build()));
   }

   public void completeScriptedQuest(MapleCharacter character, int questId, int npcId, int x, int y) {
      UriBuilder.service(RestService.QUEST)
            .path("characters").path(character.getId()).path("quests").path(questId).path("complete").queryParam("scripted", true)
            .getRestClient()
            .success((code, body) -> {
               boolean hasScriptRequirement =
                     UriBuilder.service(RestService.QUEST).path("quests").path(questId).queryParam("checkEnd", true)
                           .getRestClient(QuestAttributes.class)
                           .getWithResponse()
                           .result()
                           .map(container -> container.getData().getAttributes().getHasScriptRequirement())
                           .orElse(false);
               QuestScriptManager.getInstance().end(character.getClient(), (short) questId, npcId, hasScriptRequirement);
            })
            .create(new InputObjectBuilder().setData(new ResultObjectBuilder(QuestCompleteAttributes.class, "")
                  .setAttribute(new QuestCompleteAttributesBuilder()
                        .setNpcId(npcId)
                        .setPacketX(x)
                        .setPacketY(y)
                  )
                  .build()));
   }

   public void reset(MapleCharacter character, int questId) {
      UriBuilder.service(RestService.QUEST)
            .path("characters").path(character.getId()).path("quests").path(questId)
            .getRestClient()
            .success((code, data) ->
                  MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.PINK_TEXT,
                        I18nMessage.from("QUEST_RESET_COMMAND_SUCCESS").with(questId)))
            .failure((code) ->
                  MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.PINK_TEXT,
                        I18nMessage.from("QUEST_RESET_COMMAND_FAILURE").with(questId)))
            .delete();
   }

   public boolean forfeit(MapleCharacter character, int questId) {
      UriBuilder.service(RestService.QUEST)
            .path("characters").path(character.getId()).path("quests").path(questId).queryParam("forfeit", true)
            .getRestClient()
            .delete();
      return true;
   }

   public void forceStart(MapleCharacter character, int questId, int npc) {
      UriBuilder.service(RestService.QUEST).path("characters").path(character.getId()).path("quests").queryParam("force", true)
            .getRestClient()
            .create(new InputObjectBuilder().setData(new ResultObjectBuilder(QuestStartAttributes.class, questId)
                  .setAttribute(new QuestStartAttributesBuilder()
                        .setNpcId(npc)
                  )
                  .build()));
   }

   public boolean forceComplete(MapleCharacter character, int questId, int npc) {
      UriBuilder.service(RestService.QUEST)
            .path("characters").path(character.getId()).path("quests").path(questId).path("complete").queryParam("force", true)
            .getRestClient()
            .create(new InputObjectBuilder().setData(new ResultObjectBuilder(QuestCompleteAttributes.class, "")
                  .setAttribute(new QuestCompleteAttributesBuilder()
                        .setNpcId(npc)
                  )
                  .build()));
      return true;
   }

   public void expireQuest(MapleCharacter character, int questId) {
      UriBuilder.service(RestService.QUEST).path("characters").path(character.getId()).path("quests").path(questId).path("expire")
            .getRestClient()
            .create();
   }

   public void restoreLostItem(MapleCharacter character, int questId, int itemId) {
      UriBuilder.service(RestService.QUEST)
            .path("characters").path(character.getId()).path("quests").path(questId).path("items").path(itemId)
            .getRestClient()
            .create();
   }

   public boolean isComplete(MapleCharacter character, int questId) {
      return isStatus(character, questId, "COMPLETED");
   }

   public boolean isNotStarted(MapleCharacter character, int questId) {
      return isStatus(character, questId, "NOT_STARTED");
   }

   public boolean isStarted(MapleCharacter character, int questId) {
      return isStatus(character, questId, "STARTED");
   }

   public boolean isStatus(MapleCharacter character, int questId, String status) {
      return getStatus(character, questId).map(result -> result.equalsIgnoreCase(status)).orElse(false);
   }

   public Optional<String> getStatus(MapleCharacter character, int questId) {
      return UriBuilder.service(RestService.QUEST).path("characters").path(character.getId()).path("quests").path(questId)
            .getRestClient(CharacterQuestAttributes.class)
            .getWithResponse()
            .result()
            .map(container -> container.getData().getAttributes().getStatus());
   }

   public boolean hasMedalMap(MapleCharacter character, int questId, int mapId) {
      return UriBuilder.service(RestService.QUEST)
            .path("characters").path(character.getId()).path("quests").path(questId).path("medalMaps").path(mapId)
            .getRestClient(NoopAttributes.class)
            .getWithResponse()
            .result()
            .isPresent();
   }

   public void addMedalMap(MapleCharacter character, int questId, int mapId) {
      UriBuilder.service(RestService.QUEST)
            .path("characters").path(character.getId()).path("quests").path(questId).path("medalMaps")
            .getRestClient()
            .create(new InputObjectBuilder().setData(new ResultObjectBuilder(NoopAttributes.class, mapId).build()));
   }

   public int getMedalProgress(MapleCharacter character, int questId) {
      return UriBuilder.service(RestService.QUEST)
            .path("characters").path(character.getId()).path("quests").path(questId).path("medalMaps")
            .getRestClient(NoopAttributes.class)
            .getWithResponse()
            .result()
            .map(container -> container.getDataAsList().size())
            .orElse(0);
   }

   public short getInstanceFromInfoNumber(int infoNumber) {
      return UriBuilder.service(RestService.QUEST).path("quests").queryParam("infoNumber", infoNumber)
            .getRestClient(QuestAttributes.class)
            .getWithResponse()
            .result()
            .map(container -> Short.parseShort(container.getData().getId()))
            .orElseThrow();
   }

   public String getProgress(MapleCharacter character, int questId) {
      return UriBuilder.service(RestService.QUEST)
            .path("characters").path(character.getId()).path("quests").path(questId).path("progress")
            .getRestClient(CharacterQuestProgressAttributes.class)
            .getWithResponse()
            .result()
            .map(container -> container.getDataAsList()
                  .stream()
                  .map(data -> data.getAttributes().getProgress())
                  .collect(Collectors.toList()))
            .map(progress -> String.join("", progress))
            .orElse("");
   }

   public String getProgress(MapleCharacter character, int questId, int infoNumber) {
      return UriBuilder.service(RestService.QUEST)
            .path("characters").path(character.getId()).path("quests").path(questId).path("progress").path(infoNumber)
            .getRestClient(CharacterQuestProgressAttributes.class)
            .getWithResponse()
            .result()
            .map(container -> container.getData().getAttributes().getProgress())
            .orElseThrow();
   }

   public void resetProgress(MapleCharacter character, int questId) {
      UriBuilder.service(RestService.QUEST)
            .path("characters").path(character.getId()).path("quests").path(questId).path("progress")
            .getRestClient()
            .delete();
   }

   public void resetSpecificProgress(MapleCharacter character, int questId, int infoNumber) {
      UriBuilder.service(RestService.QUEST)
            .path("characters").path(character.getId()).path("quests").path(questId).path("progress").path(infoNumber)
            .getRestClient()
            .delete();
   }

   public void setCustomData(MapleCharacter character, int questId, String customData) {
      UriBuilder.service(RestService.QUEST)
            .path("characters").path(character.getId()).path("quests").path(questId)
            .getRestClient()
            .update(new InputObjectBuilder().setData(new ResultObjectBuilder(CharacterQuestAttributes.class, questId)
                  .setAttribute(new CharacterQuestAttributesBuilder().setCustomData(customData))
                  .build()));
   }

   public String getCustomDataOrDefault(MapleCharacter character, int questId, String defaultValue) {
      String customData = UriBuilder.service(RestService.QUEST)
            .path("characters").path(character.getId()).path("quests").path(questId)
            .getRestClient(CharacterQuestAttributes.class)
            .getWithResponse()
            .result()
            .map(container -> container.getData().getAttributes().getCustomData())
            .orElse(null);
      if (customData == null) {
         setCustomData(character, questId, defaultValue);
         customData = UriBuilder.service(RestService.QUEST)
               .path("characters").path(character.getId()).path("quests").path(questId)
               .getRestClient(CharacterQuestAttributes.class)
               .getWithResponse()
               .result()
               .map(container -> container.getData().getAttributes().getCustomData())
               .orElse(null);
      }
      return customData;
   }

   public Integer getQuestStartItemAmountNeeded(int questId, int itemId) {
      return UriBuilder.service(RestService.QUEST)
            .path("quests").path(questId).path("items").path(itemId)
            .getRestClient(QuestItemAttributes.class)
            .getWithResponse()
            .result()
            .map(container -> container.getData().getAttributes().getStartItemAmountNeeded())
            .orElse(Integer.MIN_VALUE);
   }

   public Integer getQuestCompleteItemAmountNeeded(int questId, int itemId) {
      return UriBuilder.service(RestService.QUEST)
            .path("quests").path(questId).path("items").path(itemId)
            .getRestClient(QuestItemAttributes.class)
            .getWithResponse()
            .result()
            .map(container -> container.getData().getAttributes().getCompleteItemAmountNeeded())
            .orElse(Integer.MAX_VALUE);
   }

   public int getNpcRequirement(int questId, boolean checkEnd) {
      return UriBuilder.service(RestService.QUEST).path("quests").path(questId).queryParam("checkEnd", checkEnd)
            .getRestClient(QuestAttributes.class)
            .getWithResponse()
            .result()
            .map(container -> container.getData().getAttributes().getNpcRequirement())
            .orElse(-1);
   }

   public Optional<QuestItemAttributes> getQuestItemAttributes(int questId, int itemId) {
      return UriBuilder.service(RestService.QUEST)
            .path("quests").path(questId).path("items").path(itemId)
            .getRestClient(QuestItemAttributes.class)
            .getWithResponse()
            .result()
            .map(container -> container.getData().getAttributes());
   }

   public int getQuestMedalId(int questId) {
      return UriBuilder.service(RestService.QUEST).path("quests").path(questId)
            .getRestClient(QuestAttributes.class)
            .getWithResponse()
            .result()
            .map(container -> container.getData().getAttributes().getMedalId())
            .orElse(-1);
   }

   public String getInfoEx(MapleCharacter character, short questId, int index) {
      String questStatus = getQuest(character, questId).getStatus();
      return UriBuilder.service(RestService.QUEST)
            .path("quests").path((int) questId).queryParam("status", questStatus).queryParam("index", index)
            .getRestClient(InfoExAttributes.class)
            .getWithResponse()
            .result()
            .map(container -> container.getData().getAttributes().getInfoEx())
            .orElse("");
   }

   public List<DataBody<QuestAttributes>> getMatchedQuests(String search) {
      return UriBuilder.service(RestService.QUEST)
            .path("quests").queryParam("filter[search]", search)
            .getRestClient(QuestAttributes.class)
            .getWithResponse()
            .result()
            .map(DataContainer::getDataAsList)
            .orElse(new ArrayList<>());
   }
}
