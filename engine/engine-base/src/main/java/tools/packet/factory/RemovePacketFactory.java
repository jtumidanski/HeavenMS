package tools.packet.factory;

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
      Handler.handle(RemoveTV.class).decorate(this::removeTV).size(2).register(registry);
      Handler.handle(RemoveSummon.class).decorate(this::removeSummon).size(11).register(registry);
      Handler.handle(RemoveKite.class).decorate(this::removeKite).register(registry);
      Handler.handle(RemovePlayer.class).decorate(this::removePlayerFromMap).register(registry);
      Handler.handle(RemoveItem.class).decorate(this::removeItemFromMap).register(registry);
      Handler.handle(RemoveMist.class).decorate(this::removeMist).register(registry);
      Handler.handle(RemoveNPC.class).decorate(this::removeNPC).register(registry);
      Handler.handle(RemoveDragon.class).decorate(this::removeDragon).register(registry);
   }

   /**
    * Removes TV
    */
   protected void removeTV(MaplePacketLittleEndianWriter writer, RemoveTV packet) {
   }

   /**
    * Gets a packet to remove a special map object.
    */
   protected void removeSummon(MaplePacketLittleEndianWriter writer, RemoveSummon packet) {
      writer.writeInt(packet.ownerId());
      writer.writeInt(packet.objectId());
      writer.write(packet.animated() ? 4 : 1); // ?
   }

   protected void removeKite(MaplePacketLittleEndianWriter writer, RemoveKite packet) {
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
    */
   protected void removeDragon(MaplePacketLittleEndianWriter writer, RemoveDragon packet) {
      writer.writeInt(packet.characterId());
   }
}