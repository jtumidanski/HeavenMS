package tools;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import client.MapleCharacter;
import client.MapleFamily;
import client.MapleFamilyEntry;
import net.server.world.World;
import server.maps.MapleMap;
import server.maps.MapleMiniGame;
import server.maps.MaplePlayerShop;
import server.processor.maps.MapleMapProcessor;
import tools.packet.PacketInput;

public class MasterBroadcaster {
   private static MasterBroadcaster instance;

   public static MasterBroadcaster getInstance() {
      if (instance == null) {
         instance = new MasterBroadcaster();
      }
      return instance;
   }

   private MasterBroadcaster() {
   }

   /**
    * Sends a packet to everyone in the map. Including the source.
    *
    * @param map           the map
    * @param packetCreator the packet to send
    */
   public void sendToAllInMap(MapleMap map, Function<MapleCharacter, byte[]> packetCreator) {
      sendToAllInMap(map, null, packetCreator);
   }

   public void sendToAllInMap(MapleMap map, PacketInput packetInput) {
      sendToAllInMap(map, null, character -> PacketCreator.create(packetInput));
   }

   /**
    * Sends a packet to everyone in the map, conditionally excluding the source of the packet.
    *
    * @param map            the map
    * @param packetCreator  the packet to send
    * @param repeatToSource true if the packet should be sent to the source
    * @param source         the source
    */
   public void sendToAllInMap(MapleMap map, Function<MapleCharacter, byte[]> packetCreator, boolean repeatToSource, MapleCharacter source) {
      sendToAllInMap(map, mapleCharacter -> passRepeatToSource(repeatToSource, source, mapleCharacter), packetCreator);
   }

   public void sendToAllInMap(MapleMap map, PacketInput packetInput, boolean repeatToSource, MapleCharacter source) {
      sendToAllInMap(map, mapleCharacter -> passRepeatToSource(repeatToSource, source, mapleCharacter), character -> PacketCreator.create(packetInput));
   }

   /**
    * Identifies if the mapleCharacter is the source of the packet.
    *
    * @param repeatToSource false if we care about the mapleCharacter being the source
    * @param source         the source of the packet
    * @param mapleCharacter the mapleCharacter to verify
    * @return true if the message should be repeated
    */
   private Boolean passRepeatToSource(boolean repeatToSource, MapleCharacter source, MapleCharacter mapleCharacter) {
      if (repeatToSource) {
         return true;
      } else {
         return mapleCharacter != source;
      }
   }

   /**
    * Identifies if the mapleCharacter is in range of the reference point
    *
    * @param rangeThreshold the distance which determines whether the two points are in range.
    * @param mapleCharacter the character in question
    * @param referencePoint the reference point
    * @return true if the character is in range
    */
   private Boolean passRangeCheck(double rangeThreshold, MapleCharacter mapleCharacter, Point referencePoint) {
      //
      if (rangeThreshold < Double.POSITIVE_INFINITY) {
         return referencePoint.distanceSq(mapleCharacter.getPosition()) <= rangeThreshold;
      } else {
         return true;
      }
   }

   /**
    * Sends a packet to everyone in the map, conditionally excluding the source of the packet, and conditionally excluding those out of range.
    *
    * @param map            the map
    * @param packetCreator  the packet
    * @param repeatToSource true if the packet should be repeated to the source
    * @param source         the source of the packet
    * @param ranged         true if the range of the characters in the map should be considered
    */
   public void sendToAllInMapRange(MapleMap map, Function<MapleCharacter, byte[]> packetCreator, boolean repeatToSource, MapleCharacter source, boolean ranged) {
      sendToAllInMap(map, mapleCharacter -> {
         double rangeSq = ranged ? MapleMapProcessor.getInstance().getRangedDistance() : Double.POSITIVE_INFINITY;
         return passRepeatToSource(repeatToSource, source, mapleCharacter) && passRangeCheck(rangeSq, mapleCharacter, source.getPosition());
      }, packetCreator);
   }

   public void sendToAllInMapRange(MapleMap map, PacketInput packetInput, boolean repeatToSource, MapleCharacter source, boolean ranged) {
      sendToAllInMap(map, mapleCharacter -> {
         double rangeSq = ranged ? MapleMapProcessor.getInstance().getRangedDistance() : Double.POSITIVE_INFINITY;
         return passRepeatToSource(repeatToSource, source, mapleCharacter) && passRangeCheck(rangeSq, mapleCharacter, source.getPosition());
      }, character -> PacketCreator.create(packetInput));
   }

   /**
    * Sends a packet to everyone in the map, excluding those out of range of the reference point.
    *
    * @param map            the map
    * @param packetCreator  the packet
    * @param referencePoint the reference point
    */
   public void sendToAllInMapRange(MapleMap map, Function<MapleCharacter, byte[]> packetCreator, Point referencePoint) {
      sendToAllInMap(map, mapleCharacter -> passRangeCheck(MapleMapProcessor.getInstance().getRangedDistance(), mapleCharacter, referencePoint), packetCreator);
   }

   /**
    * Sends a packet to everyone in the map, excluding those out of range of the reference point.
    *
    * @param map            the map
    * @param packetInput    the packet
    * @param referencePoint the reference point
    */
   public void sendToAllInMapRange(MapleMap map, PacketInput packetInput, Point referencePoint) {
      sendToAllInMap(map, mapleCharacter -> passRangeCheck(MapleMapProcessor.getInstance().getRangedDistance(), mapleCharacter, referencePoint), character -> PacketCreator.create(packetInput));
   }

   /**
    * Sends a packet to everyone in the map, excluding the source, and those out of range of the reference point.
    *
    * @param map            the map
    * @param packetCreator  the packet
    * @param source         the source of the packet
    * @param referencePoint the reference point
    */
   public void sendToAllInMapRange(MapleMap map, Function<MapleCharacter, byte[]> packetCreator, MapleCharacter source, Point referencePoint) {
      sendToAllInMap(map, mapleCharacter -> passRepeatToSource(false, source, mapleCharacter) && passRangeCheck(MapleMapProcessor.getInstance().getRangedDistance(), mapleCharacter, referencePoint), packetCreator);
   }

   /**
    * Sends a packet to all GMs in the map. Including the source.
    *
    * @param map           the map
    * @param packetCreator the packet
    */
   public void sendToAllGMInMap(MapleMap map, Function<MapleCharacter, byte[]> packetCreator) {
      sendToAllInMap(map, MapleCharacter::isGM, packetCreator);
   }

   /**
    * Sends a packet to everyone in the map, conditionally applying filtering if supplied.
    *
    * @param map           the map
    * @param filter        a filter to apply
    * @param packetCreator the packet to send
    */
   public void sendToAllInMap(MapleMap map, Function<MapleCharacter, Boolean> filter, Function<MapleCharacter, byte[]> packetCreator) {
      send(map.getCharacters(), filter, packetCreator);
   }

   /**
    * Sends a packet to the family provided, conditionally excluding the source of the packet.
    *
    * @param family         the family
    * @param packetCreator  the packet
    * @param repeatToSource true if the packet should be repeated to the source
    * @param source         the source of the packet
    */
   public void sendToFamily(MapleFamily family, Function<MapleCharacter, byte[]> packetCreator, boolean repeatToSource, MapleCharacter source) {
      send(family.getMembers().stream().map(MapleFamilyEntry::getChr).collect(Collectors.toList()), mapleCharacter -> passRepeatToSource(repeatToSource, source, mapleCharacter), packetCreator);
   }

   /**
    * Sends a packet to the family member provided, conditionally including the members senior.
    *
    * @param familyMember       the family member
    * @param packetCreator      the packet
    * @param includeSuperSenior true if the senior of this family member should receive this packet
    */
   public void sendToSenior(MapleFamilyEntry familyMember, Function<MapleCharacter, byte[]> packetCreator, boolean includeSuperSenior) {
      List<MapleCharacter> recipients = new ArrayList<>();
      if (familyMember != null) {
         MapleCharacter familyMemberChr = familyMember.getChr();
         if (familyMemberChr != null) {
            recipients.add(familyMemberChr);
         }
         if (includeSuperSenior && familyMember.getSenior() != null) {
            MapleCharacter seniorChr = familyMember.getSenior().getChr();
            if (seniorChr != null) {
               recipients.add(seniorChr);
            }
         }
      }
      send(recipients, packetCreator);
   }

   /**
    * Sends a packet to both the game participants
    *
    * @param game        the game
    * @param packetInput the packet
    */
   public void sendToGamers(MapleMiniGame game, PacketInput packetInput) {
      sendToGamers(game, character -> PacketCreator.create(packetInput), true, true);
   }


   /**
    * Sends a packet to the game owner
    *
    * @param game        the game
    * @param packetInput the packet
    */
   public void sendToGameOwner(MapleMiniGame game, PacketInput packetInput) {
      sendToGamers(game, character -> PacketCreator.create(packetInput), true, false);
   }

   /**
    * Sends a packet to the game visitor
    *
    * @param game        the game
    * @param packetInput the packet
    */
   public void sendToGameVisitor(MapleMiniGame game, PacketInput packetInput) {
      sendToGamers(game, character -> PacketCreator.create(packetInput), false, true);
   }

   /**
    * Sends a packet conditionally to any of the game participants.
    *
    * @param game          the game
    * @param packetCreator the packet
    * @param toOwner       true if the packet should be sent to the owner
    * @param toVisitor     true if the packet should be sent to the visitor
    */
   private void sendToGamers(MapleMiniGame game, Function<MapleCharacter, byte[]> packetCreator, boolean toOwner, boolean toVisitor) {
      List<MapleCharacter> recipients = new ArrayList<>();
      if (toOwner) {
         recipients.add(game.getOwner());
      }
      if (toVisitor) {
         recipients.add(game.getVisitor());
      }
      send(recipients, packetCreator);
   }

   /**
    * Sends a packet to all participants in a shop.
    *
    * @param shop          the shop
    * @param packetCreator the packet
    */
   public void sendToShop(MaplePlayerShop shop, Function<MapleCharacter, byte[]> packetCreator) {
      List<MapleCharacter> recipients = new ArrayList<>();
      recipients.add(shop.getOwner());
      recipients.addAll(Arrays.stream(shop.getVisitors()).filter(Objects::nonNull).collect(Collectors.toList()));
      send(recipients, packetCreator);
   }

   public void sendToShop(MaplePlayerShop shop, PacketInput packetInput) {
      sendToShop(shop, character -> PacketCreator.create(packetInput));
   }

   /**
    * Sends a packet to everyone shopping in a shop.
    *
    * @param shop        the shop
    * @param packetInput the packet
    */
   public void sendToShoppers(MaplePlayerShop shop, PacketInput packetInput) {
      send(Arrays.stream(shop.getVisitors()).filter(Objects::nonNull).collect(Collectors.toList()), character -> PacketCreator.create(packetInput));
   }

   /**
    * Sends a packet to everyone shopping in a shop.
    *
    * @param shop          the shop
    * @param packetCreator the packet
    */
   public void sendToShoppers(MaplePlayerShop shop, BiFunction<MapleCharacter, Integer, byte[]> packetCreator) {
      send(Arrays.stream(shop.getVisitors()).filter(Objects::nonNull).collect(Collectors.toList()), packetCreator);
   }

   /**
    * Sends a packet to everyone of the targets if they're located in this world, and conditionally if they're not the source.
    *
    * @param world          the world
    * @param targetIds      the target character ids
    * @param packetCreator  the packet
    * @param repeatToSource true if the packet should be repeated to the source
    * @param sourceId       the source character id
    */
   public void sendToWorld(World world, List<Integer> targetIds, Function<MapleCharacter, byte[]> packetCreator, boolean repeatToSource, Integer sourceId) {
      Optional<MapleCharacter> source = world.getPlayerStorage().getCharacterById(sourceId);
      sendToWorld(world, mapleCharacter -> passRepeatToSource(repeatToSource, source.orElse(null), mapleCharacter) && targetIds.contains(mapleCharacter.getId()), packetCreator);
   }

   /**
    * Sends a packet to everyone in the world, excluding those filtered out.
    *
    * @param world         the world
    * @param filter        the filter to apply
    * @param packetCreator the packet
    */
   private void sendToWorld(World world, Function<MapleCharacter, Boolean> filter, Function<MapleCharacter, byte[]> packetCreator) {
      send(world.getPlayerStorage().getAllCharacters(), filter, packetCreator);
   }

   /**
    * Sends a packet to a filtered set of the supplied recipients. If the filter is null, no filtering applied.
    *
    * @param recipients    the base set of recipients
    * @param filter        the filter to apply
    * @param packetCreator the packet
    */
   public void send(Collection<MapleCharacter> recipients, Function<MapleCharacter, Boolean> filter, Function<MapleCharacter, byte[]> packetCreator) {
      if (filter == null) {
         send(recipients, packetCreator);
      } else {
         send(recipients.stream().filter(filter::apply).collect(Collectors.toList()), packetCreator);
      }
   }

   /**
    * Sends a packet to the recipients provided.
    *
    * @param recipients    the recipients
    * @param packetCreator the packet
    */
   public void send(Collection<MapleCharacter> recipients, Function<MapleCharacter, byte[]> packetCreator) {
      recipients.parallelStream().forEach(character -> character.announce(packetCreator.apply(character)));
   }

   /**
    * Sends a packet to the recipients provided.
    *
    * @param recipients    the recipients
    * @param packetCreator the packet
    */
   public void send(Collection<MapleCharacter> recipients, BiFunction<MapleCharacter, Integer, byte[]> packetCreator) {
      MapleCharacter[] recipientArray = recipients.toArray(MapleCharacter[]::new);
      IntStream.range(0, recipientArray.length).mapToObj(index -> new Pair<>(index, recipientArray[index])).parallel().forEach(pair -> pair.getRight().announce(packetCreator.apply(pair.getRight(), pair.getLeft())));
   }
}