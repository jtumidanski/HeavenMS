package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.remove.RemoveDragon;
import tools.packet.remove.RemoveItem;
import tools.packet.remove.RemoveKite;
import tools.packet.remove.RemoveMist;
import tools.packet.remove.RemoveNPC;
import tools.packet.remove.RemovePlayer;
import tools.packet.remove.RemoveSummon;
import tools.packet.remove.RemoveTV;

public class RemovePacketFactory extends AbstractPacketFactory {
   private static RemovePacketFactory instance;

   public static RemovePacketFactory getInstance() {
      if (instance == null) {
         instance = new RemovePacketFactory();
      }
      return instance;
   }

   private RemovePacketFactory() {
      registry.setHandler(RemoveTV.class, packet -> create(SendOpcode.REMOVE_TV, this::removeTV, packet, 2));
      registry.setHandler(RemoveSummon.class, packet -> create(SendOpcode.REMOVE_SPECIAL_MAPOBJECT, this::removeSummon, packet, 11));
      registry.setHandler(RemoveKite.class, packet -> create(SendOpcode.REMOVE_KITE, this::removeKite, packet));
      registry.setHandler(RemovePlayer.class, packet -> create(SendOpcode.REMOVE_PLAYER_FROM_MAP, this::removePlayerFromMap, packet));
      registry.setHandler(RemoveItem.class, packet -> create(SendOpcode.REMOVE_ITEM_FROM_MAP, this::removeItemFromMap, packet));
      registry.setHandler(RemoveMist.class, packet -> create(SendOpcode.REMOVE_MIST, this::removeMist, packet));
      registry.setHandler(RemoveNPC.class, packet -> create(SendOpcode.REMOVE_NPC, this::removeNPC, packet));
      registry.setHandler(RemoveDragon.class, packet -> create(SendOpcode.REMOVE_DRAGON, this::removeDragon, packet));
   }

   /**
    * Removes TV
    *
    * @return The Remove TV Packet
    */
   protected void removeTV(MaplePacketLittleEndianWriter writer, RemoveTV packet) {
   }

   /**
    * Gets a packet to remove a special map object.
    *
    * @return The packet removing the object.
    */
   protected void removeSummon(MaplePacketLittleEndianWriter writer, RemoveSummon packet) {
      writer.writeInt(packet.getSummon().getOwner().getId());
      writer.writeInt(packet.getSummon().getObjectId());
      writer.write(packet.isAnimated() ? 4 : 1); // ?
   }

   protected void removeKite(MaplePacketLittleEndianWriter writer, RemoveKite packet) {
      // thanks to Arnah (MaplePacketLittleEndianWriter writer, Vertisy)
      writer.write(packet.animationType()); // 0 is 10/10, 1 just vanishes
      writer.writeInt(packet.objectId());
   }

   protected void removePlayerFromMap(MaplePacketLittleEndianWriter writer, RemovePlayer packet) {
      writer.writeInt(packet.characterId());
   }

   /**
    * animation: 0 - expire<br/> 1 - without animation<br/> 2 - pickup<br/> 4 -
    * explode<br/> cid is ignored for 0 and 1.<br /><br />Flagging pet as true
    * will make a pet pick up the item.
    *
    * @return
    */
   protected void removeItemFromMap(MaplePacketLittleEndianWriter writer, RemoveItem packet) {
      writer.write(packet.animation()); // expire
      writer.writeInt(packet.objectId());
      if (packet.animation() >= 2) {
         writer.writeInt(packet.characterId());
         if (packet.pet()) {
            writer.write(packet.slot());
         }
      }
   }

   protected void removeMist(MaplePacketLittleEndianWriter writer, RemoveMist packet) {
      writer.writeInt(packet.objectId());
   }

   protected void removeNPC(MaplePacketLittleEndianWriter writer, RemoveNPC packet) {
      writer.writeInt(packet.objectId());
   }

   /**
    * Sends a request to remove Mir<br>
    *
    * @return The packet
    */
   protected void removeDragon(MaplePacketLittleEndianWriter writer, RemoveDragon packet) {
      writer.writeInt(packet.characterId());
   }
}