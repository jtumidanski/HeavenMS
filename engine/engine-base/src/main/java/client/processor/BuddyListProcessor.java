package client.processor;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import client.BuddyList;
import client.BuddyListEntry;
import client.BuddyListOperation;
import client.CharacterNameAndId;
import client.MapleCharacter;
import client.MapleClient;
import client.database.data.CharNameAndIdData;
import database.DatabaseConnection;
import database.provider.CharacterProvider;
import net.server.Server;
import net.server.channel.Channel;
import net.server.channel.CharacterIdChannelPair;
import net.server.world.World;
import rest.RestService;
import rest.UriBuilder;
import rest.buddy.AddBuddy;
import rest.buddy.AddBuddyResponse;
import rest.buddy.AddBuddyResult$;
import rest.buddy.AddCharacter;
import rest.buddy.Buddy;
import rest.buddy.Character;
import rest.buddy.GetBuddiesResponse;
import rest.buddy.UpdateBuddy;
import rest.buddy.UpdateCharacter;
import tools.FilePrinter;
import tools.I18nMessage;
import tools.LambdaNoOp;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.ServerNoticeType;
import tools.packet.buddy.RequestAddBuddy;
import tools.packet.buddy.UpdateBuddyCapacity;
import tools.packet.buddy.UpdateBuddyChannel;
import tools.packet.buddy.UpdateBuddyList;

public class BuddyListProcessor {
   private static BuddyListProcessor ourInstance = new BuddyListProcessor();

   public static BuddyListProcessor getInstance() {
      return ourInstance;
   }

   private BuddyListProcessor() {
   }

   /**
    * Gets a stream of the ids of the buddies of the supplied character.
    *
    * @param characterId the id of the character
    */
   protected void getBuddies(int characterId, Consumer<Stream<Integer>> consumer) {
      UriBuilder.service(RestService.BUDDY).path("characters").path(characterId).path("buddies").getRestClient(GetBuddiesResponse.class)
            .success((responseCode, response) -> consumer.accept(response.buddies().stream().map(Buddy::id)))
            .failure(responseCode -> FilePrinter.printError(FilePrinter.BUDDY_ORCHESTRATOR, "Failed to get buddies for character " + characterId))
            .get();
   }

   /**
    * Gets a stream of the <code>MapleCharacter</code> objects representing the buddies of the supplied character.
    *
    * @param worldId     the world the character belongs to
    * @param characterId the id of the character
    */
   protected void getBuddies(int worldId, int characterId, Consumer<Stream<MapleCharacter>> consumer) {
      getBuddies(characterId, buddyIdStream -> consumer.accept(buddyIdStream.map(id -> Server.getInstance().getWorld(worldId).getPlayerStorage().getCharacterById(id)).flatMap(Optional::stream)));
   }

   /**
    * Retrieves the capacity of the buddy list for the given character.
    *
    * @param characterId the id of the character
    */
   public void getBuddyListCapacity(int characterId, Consumer<Integer> consumer) {
      UriBuilder.service(RestService.BUDDY).path("characters").path(characterId).getRestClient(Character.class)
            .success((responseCode, character) -> consumer.accept(character.capacity()))
            .failure(responseCode -> FilePrinter.printError(FilePrinter.BUDDY_ORCHESTRATOR, "Failed to get buddy list capacity for character " + characterId))
            .get();
   }

   /**
    * Loads the buddy list for the character supplied.
    *
    * @param character the character
    */
   public void loadBuddies(MapleCharacter character) {
      BuddyList result;
      UriBuilder.service(RestService.BUDDY).path("characters").path(character.getId()).path("buddies").getRestClient(GetBuddiesResponse.class)
            .success((responseCode, response) -> character.modifyBuddyList(buddyList -> populateBuddyList(buddyList, response)))
            .failure(responseCode -> FilePrinter.printError(FilePrinter.BUDDY_ORCHESTRATOR, "Failed to load buddies for character " + character.getId()))
            .get();
   }

   protected BuddyList populateBuddyList(BuddyList buddyList, GetBuddiesResponse response) {
      Map<Integer, BuddyListEntry> entries = new HashMap<>();
      Deque<CharacterNameAndId> requests = new ArrayDeque<>();

      DatabaseConnection.getInstance().withConnection(entityManager -> {
         entries.putAll(
               response.buddies().parallelStream()
                     .map(buddy -> {
                        String name = getCharacterNameFromDatabase(buddy.id());
                        return new BuddyListEntry(name, buddy.group(), buddy.id(), -1, true);
                     })
                     .collect(Collectors.toMap(BuddyListEntry::characterId, buddyListEntry -> buddyListEntry)));
         requests.addAll(
               response.pending().parallelStream()
                     .map(buddy -> {
                        String name = getCharacterNameFromDatabase(buddy.id());
                        return new CharacterNameAndId(buddy.id(), name);
                     })
                     .collect(Collectors.toUnmodifiableList()));
      });

      return new BuddyList(buddyList.capacity(), entries, requests);
   }

   /**
    * Syncs the character being created, and initializes the buddy list object for the character.
    *
    * @param character the character being created
    */
   public void syncAndInitBuddyList(MapleCharacter character) {
      syncCharacter(character.getAccountID(), character.getId(),
            responseCode -> getBuddyListCapacity(character.getId(), character::initBuddyList),
            () -> character.initBuddyList(0));
   }

   /**
    * Syncs the character with the buddy orchestrator.
    *
    * @param accountId   the id of the account
    * @param characterId the id of the character
    */
   public void syncCharacter(int accountId, int characterId) {
      syncCharacter(accountId, characterId, LambdaNoOp::doNothing, () -> FilePrinter.printError(FilePrinter.BUDDY_ORCHESTRATOR, "Failed to sync character " + characterId));
   }

   /**
    * Syncs the character with the buddy orchestrator.
    *
    * @param accountId   the id of the account
    * @param characterId the id of the character
    * @param onSuccess   a callback for a successful sync
    */
   public void syncCharacter(int accountId, int characterId, Consumer<Integer> onSuccess, Runnable onFailure) {
      UriBuilder.service(RestService.BUDDY).path("characters").getRestClient()
            .success((responseCode, result) -> onSuccess.accept(responseCode))
            .failure((responseCode) -> onFailure.run())
            .create(new AddCharacter(characterId, accountId));
   }

   /**
    * Facilitates deleting the character for the buddy orchestrator.
    *
    * @param worldId     the world the character belongs to being deleted
    * @param characterId the id of the character to delete
    */
   public void deleteCharacter(int worldId, int characterId) {
      UriBuilder.service(RestService.BUDDY).path("characters").path(characterId).getRestClient()
            .success((responseCode, result) -> getBuddies(worldId, characterId, stream -> stream.forEach(buddy -> deleteBuddySuccess(buddy, characterId))))
            .failure(responseCode -> FilePrinter.printError(FilePrinter.BUDDY_ORCHESTRATOR, "Failed to delete character " + characterId))
            .delete();
   }

   /**
    * Facilitates deleting the buddies of a character.
    *
    * @param characterId the id of the character to delete buddies for
    */
   public void deleteBuddies(int characterId) {
      UriBuilder.service(RestService.BUDDY).path("characters").path(characterId).path("buddies").getRestClient()
            .failure(responseCode -> FilePrinter.printError(FilePrinter.BUDDY_ORCHESTRATOR, "Failed to delete buddies for character " + characterId))
            .delete();
   }

   /**
    * Facilitates adding a buddy.
    *
    * @param character the character performing the operation
    * @param addName   the name of the character to add
    * @param group     the buddy group the character belongs to
    */
   public void addBuddy(MapleCharacter character, String addName, String group) {
      if (group.length() > 16 || addName.length() < 4 || addName.length() > 13) {
         return; //hax.
      }

      World world = character.getClient().getWorldServer();

      CharNameAndIdData otherCharMin;
      int channel;
      Optional<MapleCharacter> otherChar = character.getClient().getChannelServer().getPlayerStorage().getCharacterByName(addName);
      if (otherChar.isPresent()) {
         channel = character.getClient().getChannel();
         otherCharMin = new CharNameAndIdData(otherChar.get().getName(), otherChar.get().getId());
      } else {
         channel = world.find(addName);
         otherCharMin = DatabaseConnection.getInstance().withConnectionResult(connection -> CharacterProvider.getInstance().getCharacterInfoForName(connection, addName)).orElse(null);
      }

      if (otherCharMin == null) {
         MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.POP_UP, I18nMessage.from("BUDDY_SERVICE_PLAYER_NOT_FOUND").with(addName));
         return;
      }

      UriBuilder.service(RestService.BUDDY).path("characters").path(character.getId()).path("buddies").getRestClient(AddBuddyResponse.class)
            .success((responseCode, result) -> {
               if (result.errorCode() == AddBuddyResult$.MODULE$.TARGET_CHARACTER_DOES_NOT_EXIST()) {
                  MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.POP_UP, I18nMessage.from("BUDDY_SERVICE_PLAYER_NOT_FOUND").with(addName));
               } else if (result.errorCode() == AddBuddyResult$.MODULE$.FULL()) {
                  MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.POP_UP, I18nMessage.from("BUDDY_SERVICE_BUDDY_LIST_FULL"));
               } else if (result.errorCode() == AddBuddyResult$.MODULE$.ALREADY_REQUESTED()) {
                  MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.POP_UP, I18nMessage.from("BUDDY_SERVICE_ALREADY_REQUESTED").with(addName));
               } else if (result.errorCode() == AddBuddyResult$.MODULE$.BUDDY_FULL()) {
                  MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.POP_UP, I18nMessage.from("BUDDY_SERVICE_BUDDIES_LIST_IS_FULL").with(addName));
               } else if (result.errorCode() == AddBuddyResult$.MODULE$.OK() || result.errorCode() == AddBuddyResult$.MODULE$.BUDDY_ALREADY_REQUESTED()) {

                  int displayChannel;
                  if (result.errorCode().equals(AddBuddyResult$.MODULE$.BUDDY_ALREADY_REQUESTED()) && channel != -1) {
                     displayChannel = channel;
                     notifyRemoteChannel(character.getClient(), channel, otherCharMin.id(), BuddyListOperation.ADDED);
                  } else {
                     displayChannel = -1;
                     otherChar.ifPresent(otherPlayer -> addBuddyRequest(otherPlayer, character.getId(), character.getName(), channel));
                  }
                  character.modifyBuddyList(buddyList -> buddyList.put(new BuddyListEntry(otherCharMin.name(), group, otherCharMin.id(), displayChannel, true)));
                  PacketCreator.announce(character.getClient(), new UpdateBuddyList(character.getBuddyList().buddies().values()));
               }
            })
            .failure(responseCode -> operationFailure(character, "Failed to add buddy " + addName + " for character " + character.getId()))
            .create(new AddBuddy(otherCharMin.id(), otherCharMin.name(), group));

   }

   protected void addBuddyRequest(MapleCharacter character, int cidFrom, String nameFrom, int channelFrom) {
      character.modifyBuddyList(buddyList -> buddyList.put(new BuddyListEntry(nameFrom, "Default Group", cidFrom, channelFrom, false)));
      if (character.getBuddyList().hasPendingRequest()) {
         PacketCreator.announce(character, new RequestAddBuddy(cidFrom, character.getId(), nameFrom));
      } else {
         character.modifyBuddyList(buddyList -> buddyList.addRequest(new CharacterNameAndId(cidFrom, nameFrom)));
      }
   }

   protected void notifyRemoteChannel(MapleClient c, int remoteChannel, int otherCid, BuddyListOperation operation) {
      MapleCharacter player = c.getPlayer();
      if (remoteChannel != -1) {
         buddyChanged(c.getWorldServer(), otherCid, player.getId(), player.getName(), c.getChannel(), operation);
      }
   }

   /**
    * Facilitates accepting a buddy request.
    *
    * @param character the character performing the operation
    * @param otherId   the id of the other character
    */
   public void accept(MapleCharacter character, int otherId) {
      if (!character.getBuddyList().isFull()) {
         int channel = character.getClient().getWorldServer().find(otherId);
         Optional<String> otherName = character.getClient().getChannelServer().getPlayerStorage().getCharacterById(otherId)
               .map(MapleCharacter::getName)
               .or(() -> Optional.ofNullable(getCharacterNameFromDatabase(otherId)));

         if (otherName.isPresent()) {
            UriBuilder.service(RestService.BUDDY).path("characters").path(character.getId()).path("buddies").path(otherId).getRestClient()
                  .failure(responseCode -> operationFailure(character, "Unable to accept buddy " + otherId + " for character " + character.getId()))
                  .update(new UpdateBuddy(0, false));
            UriBuilder.service(RestService.BUDDY).path("characters").path(otherId).path("buddies").path(character.getId()).getRestClient()
                  .failure(responseCode -> operationFailure(character, "Unable to accept buddy " + character.getId() + " for character " + otherId))
                  .update(new UpdateBuddy(0, false));

            character.modifyBuddyList(buddyList -> buddyList.put(new BuddyListEntry(otherName.get(), "Default Group", otherId, channel, true)));
            PacketCreator.announce(character.getClient(), new UpdateBuddyList(character.getBuddyList().buddies().values()));
            BuddyListProcessor.getInstance().notifyRemoteChannel(character.getClient(), channel, otherId, BuddyListOperation.ADDED);
         }
      }
      nextPendingRequest(character.getClient());
   }

   protected void nextPendingRequest(MapleClient client) {
      client.getPlayer().modifyBuddyList(buddyList -> {
         Optional<Pair<BuddyList, CharacterNameAndId>> result = buddyList.pollPendingRequest();
         if (result.isPresent()) {
            PacketCreator.announce(client, new RequestAddBuddy(result.get().getRight().id(), client.getPlayer().getId(), result.get().getRight().name()));
            return result.get().getLeft();
         } else {
            return buddyList;
         }
      });
   }

   protected String getCharacterNameFromDatabase(int characterId) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> CharacterProvider.getInstance().getNameForId(connection, characterId)).orElse(null);
   }

   /**
    * Facilitates deleting a buddy.
    *
    * @param character the character performing the operation
    * @param otherId   the id of the other character
    */
   public void deleteBuddy(MapleCharacter character, int otherId) {
      UriBuilder.service(RestService.BUDDY).path("characters").path(character.getId()).path("buddies").path(otherId).getRestClient()
            .success((responseCode, result) -> deleteBuddySuccess(character, otherId))
            .failure(responseCode -> deleteBuddyFailure(character, otherId))
            .delete();
   }

   protected void deleteBuddyFailure(MapleCharacter character, int otherCharacterId) {
      operationFailure(character, "Failed to delete buddy " + otherCharacterId + " for character " + character.getId());
   }

   protected void deleteBuddySuccess(MapleCharacter character, int otherCharacterId) {
      BuddyList bl = character.getBuddyList();
      if (bl.containsVisible(otherCharacterId)) {
         notifyRemoteChannel(character.getClient(), character.getWorldServer().find(otherCharacterId), otherCharacterId, BuddyListOperation.DELETED);
      }
      character.modifyBuddyList(buddyList -> buddyList.remove(otherCharacterId));
      PacketCreator.announce(character.getClient(), new UpdateBuddyList(character.getBuddyList().buddies().values()));
      nextPendingRequest(character.getClient());
   }

   /**
    * Facilitates updating the capacity of a characters buddy list.
    *
    * @param character   the character performing the operation
    * @param newCapacity the new capacity of the buddy list
    * @param onSuccess   a runnable if the update is successful
    * @param onFailure   a runnable if the update is not successful
    */
   public void updateCapacity(MapleCharacter character, int newCapacity, Runnable onSuccess, Runnable onFailure) {
      UriBuilder.service(RestService.BUDDY).path("characters").path(character.getId()).getRestClient()
            .success((responseCode, result) -> updateCapacitySuccess(character, newCapacity, onSuccess))
            .failure(responseCode -> updateCapacityFailure(character, onFailure))
            .update(new UpdateCharacter(newCapacity));
   }

   protected void updateCapacityFailure(MapleCharacter character, Runnable onFailure) {
      operationFailure(character, "Failed to update capacity for character " + character.getId());
      onFailure.run();
   }

   //TODO - handle logging locale
   protected void operationFailure(MapleCharacter character, String debugMessage) {
      FilePrinter.printError(FilePrinter.BUDDY_ORCHESTRATOR, debugMessage);
      MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.PINK_TEXT, I18nMessage.from("BUDDY_SERVICE_OFFLINE"));
   }

   protected void updateCapacitySuccess(MapleCharacter character, int capacity, Runnable onSuccess) {
      character.modifyBuddyList(buddyList -> buddyList.updateCapacity(capacity));
      PacketCreator.announce(character.getClient(), new UpdateBuddyCapacity(capacity));
      onSuccess.run();
   }

   /**
    * Facilitates notifying a characters buddies when they log in.
    *
    * @param character the character logging in
    */
   public void onLogin(MapleCharacter character) {
      BuddyList buddyList = character.getBuddyList();
      int[] buddyIds = buddyList.buddyIds();
      World world = character.getWorldServer();

      updateBuddies(world, character.getId(), character.getClient().getChannel(), buddyIds, false);
      buddyList = multiBuddyFind(world, character.getId(), buddyIds)
            .reduce(buddyList, this::updateChannelForBuddy, (a, b) -> b);
      PacketCreator.announce(character.getClient(), new UpdateBuddyList(buddyList.buddies().values()));
   }

   protected BuddyList updateChannelForBuddy(BuddyList buddyList, CharacterIdChannelPair onlineBuddy) {
      BuddyListEntry buddyListEntry = buddyList.get(onlineBuddy.getCharacterId())
            .updateChannel(onlineBuddy.getChannel());
      return buddyList.put(buddyListEntry);
   }

   protected Stream<CharacterIdChannelPair> multiBuddyFind(World world, int charIdFrom, int[] characterIds) {
      return world.getChannels().parallelStream()
            .map(channel -> Arrays.stream(multiBuddyFind(channel, charIdFrom, characterIds))
                  .mapToObj(characterId -> new CharacterIdChannelPair(characterId, channel.getId()))
                  .collect(Collectors.toList()))
            .flatMap(Collection::stream);
   }

   protected int[] multiBuddyFind(Channel channel, int charIdFrom, int[] characterIds) {
      List<Integer> ret = Arrays.stream(characterIds)
            .mapToObj(id -> channel.getPlayerStorage().getCharacterById(id))
            .flatMap(Optional::stream)
            .filter(character -> character.getBuddyList().containsVisible(charIdFrom))
            .map(MapleCharacter::getId)
            .collect(Collectors.toList());

      int[] retArr = new int[ret.size()];
      int pos = 0;
      for (Integer i : ret) {
         retArr[pos++] = i;
      }
      return retArr;
   }

   /**
    * Facilitates notifying a characters buddies when they log out.
    *
    * @param character the character logging out
    */
   public void onLogoff(MapleCharacter character) {
      updateBuddies(character.getWorldServer(), character.getId(), character.getClient().getChannel(), character.getBuddyList().buddyIds(), true);
   }

   protected void updateBuddies(World world, int characterId, int channel, int[] buddies, boolean offline) {
      Arrays.stream(buddies)
            .mapToObj(buddyId -> world.getPlayerStorage().getCharacterById(buddyId))
            .flatMap(Optional::stream)
            .filter(character -> {
               BuddyListEntry buddyListEntry = character.getBuddyList().get(characterId);
               return buddyListEntry != null && buddyListEntry.visible();
            })
            .forEach(character -> {
               BuddyListEntry initialEntry = character.getBuddyList().get(characterId);
               BuddyListEntry buddyListEntry;
               int mcChannel;
               if (offline) {
                  buddyListEntry = initialEntry.updateChannel((byte) -1);
                  mcChannel = -1;
               } else {
                  buddyListEntry = initialEntry.updateChannel(channel);
                  mcChannel = (byte) (channel - 1);
               }
               character.modifyBuddyList(buddyList -> buddyList.put(buddyListEntry));
               PacketCreator.announce(character, new UpdateBuddyChannel(buddyListEntry.characterId(), mcChannel));
            });
   }

   protected void buddyChanged(World world, int cid, int cidFrom, String name, int channel, BuddyListOperation operation) {
      world.getPlayerStorage().getCharacterById(cid).ifPresent(addChar -> {
         BuddyList buddylist = addChar.getBuddyList();
         switch (operation) {
            case ADDED:
               if (buddylist.contains(cidFrom)) {
                  addChar.modifyBuddyList(buddyList -> buddyList.put(new BuddyListEntry(name, "Default Group", cidFrom, channel, true)));
                  PacketCreator.announce(addChar, new UpdateBuddyChannel(cidFrom, (byte) (channel - 1)));
               }
               break;
            case DELETED:
               if (buddylist.contains(cidFrom)) {
                  addChar.modifyBuddyList(buddyList -> buddyList.put(new BuddyListEntry(name, "Default Group", cidFrom, (byte) -1, buddylist.get(cidFrom).visible())));
                  PacketCreator.announce(addChar, new UpdateBuddyChannel(cidFrom, (byte) -1));
               }
               break;
         }
      });
   }
}
