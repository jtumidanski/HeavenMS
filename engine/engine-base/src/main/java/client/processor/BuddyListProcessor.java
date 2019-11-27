package client.processor;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.core.UriBuilder;

import client.BuddyList;
import client.BuddyListEntry;
import client.BuddyListOperation;
import client.CharacterNameAndId;
import client.MapleCharacter;
import client.MapleClient;
import client.database.data.CharNameAndIdData;
import client.database.provider.CharacterProvider;
import config.YamlConfig;
import net.server.Server;
import net.server.channel.Channel;
import net.server.channel.CharacterIdChannelPair;
import net.server.world.World;
import rest.buddy.AddBuddy;
import rest.buddy.AddBuddyResponse;
import rest.buddy.AddBuddyResult;
import rest.buddy.AddCharacter;
import rest.buddy.Buddy;
import rest.buddy.Character;
import rest.buddy.GetBuddiesResponse;
import rest.buddy.UpdateBuddy;
import rest.buddy.UpdateCharacter;
import scala.Option;
import tools.DatabaseConnection;
import tools.FilePrinter;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.RestProvider;
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
    * @return a stream
    */
   protected Stream<Integer> getBuddies(int characterId) {
      URI path = UriBuilder.fromUri(URI.create("http://" + YamlConfig.config.server.BUDDY_MS_HOST + ":" + YamlConfig.config.server.BUDDY_MS_PORT + "/ms")).path("bos").path("buddies").queryParam("characterId", characterId).build();
      return RestProvider.getInstance().get(path, GetBuddiesResponse.class, response -> {
         return response.buddies().stream().map(Buddy::id);
      }, () -> FilePrinter.printError(FilePrinter.BUDDY_ORCHESTRATOR, "Failed to get buddies for character " + characterId)).orElse(Stream.empty());
   }

   /**
    * Gets a stream of the <code>MapleCharacter</code> objects representing the buddies of the supplied character.
    *
    * @param worldId     the world the character belongs to
    * @param characterId the id of the character
    * @return a stream
    */
   protected Stream<MapleCharacter> getBuddies(int worldId, int characterId) {
      return getBuddies(characterId)
            .map(buddyId -> Server.getInstance().getWorld(worldId).getPlayerStorage().getCharacterById(buddyId))
            .flatMap(Optional::stream);
   }

   /**
    * Retrieves the capacity of the buddy list for the given character.
    *
    * @param characterId the id of the character
    * @return an optional containing the capacity
    */
   public Integer getBuddyListCapacity(int characterId) {
      URI path = UriBuilder.fromUri(URI.create("http://" + YamlConfig.config.server.BUDDY_MS_HOST + ":" + YamlConfig.config.server.BUDDY_MS_PORT + "/ms")).path("bos").path("characters").path(Integer.toString(characterId)).build();
      return RestProvider.getInstance().get(path, Character.class,
            Character::capacity,
            () -> FilePrinter.printError(FilePrinter.BUDDY_ORCHESTRATOR, "Failed to get buddy list capacity for character " + characterId)).orElse(0);
   }

   /**
    * Loads the buddy list for the character supplied.
    *
    * @param characterId the id of the character
    * @param buddyList   the buddy list to populate
    */
   public void loadBuddies(int characterId, BuddyList buddyList) {
      URI path = UriBuilder.fromUri(URI.create("http://" + YamlConfig.config.server.BUDDY_MS_HOST + ":" + YamlConfig.config.server.BUDDY_MS_PORT + "/ms")).path("bos").path("buddies").queryParam("characterId", characterId).build();
      RestProvider.getInstance().get(path, GetBuddiesResponse.class,
            response -> {
               populateBuddyList(buddyList, response);
            },
            () -> FilePrinter.printError(FilePrinter.BUDDY_ORCHESTRATOR, "Failed to load buddies for character " + characterId));
   }

   protected void populateBuddyList(BuddyList buddyList, GetBuddiesResponse response) {
      DatabaseConnection.getInstance().withConnection(entityManager -> {
         response.buddies().parallelStream().forEach(buddy -> {
            String name = getCharacterNameFromDatabase(buddy.id());
            buddyList.put(new BuddyListEntry(name, buddy.group(), buddy.id(), -1, true));
         });

         response.pending().parallelStream().forEach(buddy -> {
            String name = getCharacterNameFromDatabase(buddy.id());
            buddyList.addRequest(new CharacterNameAndId(buddy.id(), name));
         });
      });
   }

   /**
    * Syncs the character being created, and initializes the buddy list object for the character.
    *
    * @param character the character being created
    */
   public void syncAndInitBuddyList(MapleCharacter character) {
      syncCharacter(character.getAccountID(), character.getId(), responseCode -> character.initBuddyList(getBuddyListCapacity(character.getId())));
   }

   /**
    * Syncs the character with the buddy orchestrator.
    *
    * @param accountId   the id of the account
    * @param characterId the id of the character
    */
   public void syncCharacter(int accountId, int characterId) {
      syncCharacter(accountId, characterId, RestProvider::doNothing);
   }

   /**
    * Syncs the character with the buddy orchestrator.
    *
    * @param accountId   the id of the account
    * @param characterId the id of the character
    * @param onSuccess   a callback for a successful sync
    */
   public void syncCharacter(int accountId, int characterId, Consumer<Integer> onSuccess) {
      URI path = UriBuilder.fromUri(URI.create("http://" + YamlConfig.config.server.BUDDY_MS_HOST + ":" + YamlConfig.config.server.BUDDY_MS_PORT + "/ms")).path("bos").path("characters").build();
      RestProvider.getInstance().post(path, new AddCharacter(characterId, accountId),
            onSuccess,
            responseCode -> FilePrinter.printError(FilePrinter.BUDDY_ORCHESTRATOR, "Failed to sync character " + characterId));
   }

   /**
    * Facilitates deleting the character for the buddy orchestrator.
    *
    * @param worldId     the world the character belongs to being deleted
    * @param characterId the id of the character to delete
    */
   public void deleteCharacter(int worldId, int characterId) {
      URI path = UriBuilder.fromUri(URI.create("http://" + YamlConfig.config.server.BUDDY_MS_HOST + ":" + YamlConfig.config.server.BUDDY_MS_PORT + "/ms")).path("bos").path("characters").path(Integer.toString(characterId)).build();
      RestProvider.getInstance().delete(path,
            responseCode -> getBuddies(worldId, characterId).forEach(buddy -> deleteBuddySuccess(buddy, characterId)),
            responseCode -> FilePrinter.printError(FilePrinter.BUDDY_ORCHESTRATOR, "Failed to delete character " + characterId));
   }

   /**
    * Facilitates deleting the buddies of a character.
    *
    * @param characterId the id of the character to delete buddies for
    */
   public void deleteBuddies(int characterId) {
      URI path = UriBuilder.fromUri(URI.create("http://" + YamlConfig.config.server.BUDDY_MS_HOST + ":" + YamlConfig.config.server.BUDDY_MS_PORT + "/ms")).path("bos").path("buddies").queryParam("characterId", characterId).build();
      RestProvider.getInstance().delete(path,
            RestProvider::doNothing,
            responseCode -> FilePrinter.printError(FilePrinter.BUDDY_ORCHESTRATOR, "Failed to delete buddies for character " + characterId));
      URI buddyPath = UriBuilder.fromUri(URI.create("http://" + YamlConfig.config.server.BUDDY_MS_HOST + ":" + YamlConfig.config.server.BUDDY_MS_PORT + "/ms")).path("bos").path("buddies").queryParam("buddyId", characterId).build();
      RestProvider.getInstance().delete(buddyPath,
            RestProvider::doNothing,
            responseCode -> FilePrinter.printError(FilePrinter.BUDDY_ORCHESTRATOR, "Failed to delete buddies for character " + characterId));
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

      BuddyList buddyList = character.getBuddylist();
      World world = character.getClient().getWorldServer();

      CharNameAndIdData otherCharMin;
      int channel;
      Optional<MapleCharacter> otherChar = character.getClient().getChannelServer().getPlayerStorage().getCharacterByName(addName);
      if (otherChar.isPresent()) {
         channel = character.getClient().getChannel();
         otherCharMin = new CharNameAndIdData(otherChar.get().getName(), otherChar.get().getId());
      } else {
         channel = world.find(addName);
         otherCharMin = DatabaseConnection.getInstance().withConnectionResult(connection -> CharacterProvider.getInstance().getCharacterInfoForName(connection, addName)).orElseThrow();
      }

      AddBuddy addBuddy = new AddBuddy(character.getId(), otherCharMin.id(), otherCharMin.name(), group);

      URI path = UriBuilder.fromUri(URI.create("http://" + YamlConfig.config.server.BUDDY_MS_HOST + ":" + YamlConfig.config.server.BUDDY_MS_PORT + "/ms")).path("bos").path("buddies").build();
      RestProvider.getInstance().post(path, addBuddy, AddBuddyResponse.class, (responseCode, result) -> {
         switch (result.errorCode()) {
            case TARGET_CHARACTER_DOES_NOT_EXIST:
               MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.POP_UP, "A character called \"" + addName + "\" does not exist");
               break;
            case FULL:
               MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.POP_UP, "Your buddylist is already full");
               break;
            case ALREADY_REQUESTED:
               MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.POP_UP, "You already have \"" + addName + "\" on your Buddylist");
               break;
            case BUDDY_FULL:
               MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.POP_UP, "\"" + addName + "\"'s Buddylist is full");
               break;
            case OK:
            case BUDDY_ALREADY_REQUESTED:
               int displayChannel = -1;
               if (result.errorCode().equals(AddBuddyResult.BUDDY_ALREADY_REQUESTED) && channel != -1) {
                  displayChannel = channel;
                  notifyRemoteChannel(character.getClient(), channel, otherCharMin.id(), BuddyListOperation.ADDED);
               } else {
                  otherChar.ifPresent(otherPlayer -> addBuddyRequest(otherPlayer, character.getId(), character.getName(), channel));
               }
               buddyList.put(new BuddyListEntry(otherCharMin.name(), group, otherCharMin.id(), displayChannel, true));
               PacketCreator.announce(character.getClient(), new UpdateBuddyList(buddyList.getBuddies()));
               break;
         }
      }, (responseCode) -> FilePrinter.printError(FilePrinter.BUDDY_ORCHESTRATOR, "Failed to add buddy " + addName + " for character " + character.getId()));

   }

   protected void addBuddyRequest(MapleCharacter character, int cidFrom, String nameFrom, int channelFrom) {
      character.getBuddylist().put(new BuddyListEntry(nameFrom, "Default Group", cidFrom, channelFrom, false));
      if (character.getBuddylist().hasPendingRequest()) {
         PacketCreator.announce(character, new RequestAddBuddy(cidFrom, character.getId(), nameFrom));
      } else {
         character.getBuddylist().addRequest(new CharacterNameAndId(cidFrom, nameFrom));
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
      if (!character.getBuddylist().isFull()) {
         int channel = character.getClient().getWorldServer().find(otherId);
         Optional<String> otherName = character.getClient().getChannelServer().getPlayerStorage().getCharacterById(otherId)
               .map(MapleCharacter::getName)
               .or(() -> Optional.ofNullable(getCharacterNameFromDatabase(otherId)));

         if (otherName.isPresent()) {
            RestProvider.getInstance().update(UriBuilder.fromUri(URI.create("http://" + YamlConfig.config.server.BUDDY_MS_HOST + ":" + YamlConfig.config.server.BUDDY_MS_PORT + "/ms")).path("bos").path("buddies").queryParam("characterId", character.getId()).queryParam("buddyId", otherId).build(), new UpdateBuddy(0, false),
                  RestProvider::doNothing,
                  responseCode -> FilePrinter.printError(FilePrinter.BUDDY_ORCHESTRATOR, "Unable to accept buddy " + otherId + " for character " + character.getId()));
            RestProvider.getInstance().update(UriBuilder.fromUri(URI.create("http://" + YamlConfig.config.server.BUDDY_MS_HOST + ":" + YamlConfig.config.server.BUDDY_MS_PORT + "/ms")).path("bos").path("buddies").queryParam("buddyId", character.getId()).queryParam("characterId", otherId).build(), new UpdateBuddy(0, false),
                  RestProvider::doNothing,
                  responseCode -> FilePrinter.printError(FilePrinter.BUDDY_ORCHESTRATOR, "Unable to accept buddy " + character.getId() + " for character " + otherId));

            character.getBuddylist().put(new BuddyListEntry(otherName.get(), "Default Group", otherId, channel, true));
            PacketCreator.announce(character.getClient(), new UpdateBuddyList(character.getBuddylist().getBuddies()));
            BuddyListProcessor.getInstance().notifyRemoteChannel(character.getClient(), channel, otherId, BuddyListOperation.ADDED);
         }
      }
      nextPendingRequest(character.getClient());
   }

   protected void nextPendingRequest(MapleClient c) {
      Option<CharacterNameAndId> pendingBuddyRequest = c.getPlayer().getBuddylist().pollPendingRequest();
      if (pendingBuddyRequest.isDefined()) {
         PacketCreator.announce(c, new RequestAddBuddy(pendingBuddyRequest.get().id(), c.getPlayer().getId(), pendingBuddyRequest.get().name()));
      }
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
      URI path = UriBuilder.fromUri(URI.create("http://" + YamlConfig.config.server.BUDDY_MS_HOST + ":" + YamlConfig.config.server.BUDDY_MS_PORT + "/ms")).path("bos").path("buddies").queryParam("characterId", character.getId()).queryParam("buddyId", otherId).build();
      RestProvider.getInstance().delete(path,
            returnCode -> deleteBuddySuccess(character, otherId),
            returnCode -> deleteBuddyFailure(character, otherId));
   }

   protected void deleteBuddyFailure(MapleCharacter character, int otherCharacterId) {
      FilePrinter.printError(FilePrinter.BUDDY_ORCHESTRATOR, "Failed to delete buddy " + otherCharacterId + " for character " + character.getId());
   }

   protected void deleteBuddySuccess(MapleCharacter character, int otherCharacterId) {
      BuddyList bl = character.getBuddylist();
      if (bl.containsVisible(otherCharacterId)) {
         notifyRemoteChannel(character.getClient(), character.getWorldServer().find(otherCharacterId), otherCharacterId, BuddyListOperation.DELETED);
      }

      bl.remove(otherCharacterId);
      PacketCreator.announce(character.getClient(), new UpdateBuddyList(character.getBuddylist().getBuddies()));
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
      URI path = UriBuilder.fromUri(URI.create("http://" + YamlConfig.config.server.BUDDY_MS_HOST + ":" + YamlConfig.config.server.BUDDY_MS_PORT + "/ms")).path("bos").path("characters").path(Integer.toString(character.getId())).build();
      RestProvider.getInstance().update(path, new UpdateCharacter(newCapacity),
            responseCode -> updateCapacitySuccess(character, newCapacity, onSuccess),
            responseCode -> updateCapacityFailure(character, onFailure));
   }

   protected void updateCapacityFailure(MapleCharacter character, Runnable onFailure) {
      FilePrinter.printError(FilePrinter.BUDDY_ORCHESTRATOR, "Failed to update capacity for character " + character.getId());
      onFailure.run();
   }

   protected void updateCapacitySuccess(MapleCharacter character, int capacity, Runnable onSuccess) {
      character.getBuddylist().capacity_$eq(capacity);
      PacketCreator.announce(character.getClient(), new UpdateBuddyCapacity(capacity));
      onSuccess.run();
   }

   /**
    * Facilitates notifying a characters buddies when they log in.
    *
    * @param character the character logging in
    */
   public void onLogin(MapleCharacter character) {
      BuddyList buddyList = character.getBuddylist();
      int[] buddyIds = buddyList.getBuddyIds();
      World world = character.getWorldServer();

      updateBuddies(world, character.getId(), character.getClient().getChannel(), buddyIds, false);
      multiBuddyFind(world, character.getId(), buddyIds).forEach(onlineBuddy -> updateChannelForBuddy(buddyList, onlineBuddy));
      PacketCreator.announce(character.getClient(), new UpdateBuddyList(buddyList.getBuddies()));
   }

   protected void updateChannelForBuddy(BuddyList buddyList, CharacterIdChannelPair onlineBuddy) {
      BuddyListEntry buddyListEntry = buddyList.get(onlineBuddy.getCharacterId());
      buddyListEntry.channel_$eq(onlineBuddy.getChannel());
      buddyList.put(buddyListEntry);
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
            .filter(character -> character.getBuddylist().containsVisible(charIdFrom))
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
      updateBuddies(character.getWorldServer(), character.getId(), character.getClient().getChannel(), character.getBuddylist().getBuddyIds(), true);
   }

   protected void updateBuddies(World world, int characterId, int channel, int[] buddies, boolean offline) {
      Arrays.stream(buddies)
            .mapToObj(buddyId -> world.getPlayerStorage().getCharacterById(buddyId))
            .flatMap(Optional::stream)
            .filter(character -> {
               BuddyListEntry buddyListEntry = character.getBuddylist().get(characterId);
               return buddyListEntry != null && buddyListEntry.visible();
            })
            .forEach(character -> {
               BuddyListEntry buddyListEntry = character.getBuddylist().get(characterId);
               int mcChannel;
               if (offline) {
                  buddyListEntry.channel_$eq((byte) -1);
                  mcChannel = -1;
               } else {
                  buddyListEntry.channel_$eq(channel);
                  mcChannel = (byte) (channel - 1);
               }
               character.getBuddylist().put(buddyListEntry);
               PacketCreator.announce(character, new UpdateBuddyChannel(buddyListEntry.characterId(), mcChannel));
            });
   }

   protected void buddyChanged(World world, int cid, int cidFrom, String name, int channel, BuddyListOperation operation) {
      world.getPlayerStorage().getCharacterById(cid).ifPresent(addChar -> {
         BuddyList buddylist = addChar.getBuddylist();
         switch (operation) {
            case ADDED:
               if (buddylist.contains(cidFrom)) {
                  buddylist.put(new BuddyListEntry(name, "Default Group", cidFrom, channel, true));
                  PacketCreator.announce(addChar, new UpdateBuddyChannel(cidFrom, (byte) (channel - 1)));
               }
               break;
            case DELETED:
               if (buddylist.contains(cidFrom)) {
                  buddylist.put(new BuddyListEntry(name, "Default Group", cidFrom, (byte) -1, buddylist.get(cidFrom).visible()));
                  PacketCreator.announce(addChar, new UpdateBuddyChannel(cidFrom, (byte) -1));
               }
               break;
         }
      });
   }
}
