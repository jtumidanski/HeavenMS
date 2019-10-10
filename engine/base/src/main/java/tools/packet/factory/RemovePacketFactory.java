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
      registry.setHandler(RemoveTV.class, packet -> this.removeTV((RemoveTV) packet));
      registry.setHandler(RemoveSummon.class, packet -> this.removeSummon((RemoveSummon) packet));
      registry.setHandler(RemoveKite.class, packet -> this.removeKite((RemoveKite) packet));
      registry.setHandler(RemovePlayer.class, packet -> this.removePlayerFromMap((RemovePlayer) packet));
      registry.setHandler(RemoveItem.class, packet -> this.removeItemFromMap((RemoveItem) packet));
      registry.setHandler(RemoveMist.class, packet -> this.removeMist((RemoveMist) packet));
      registry.setHandler(RemoveNPC.class, packet -> this.removeNPC((RemoveNPC) packet));
      registry.setHandler(RemoveDragon.class, packet -> this.removeDragon((RemoveDragon) packet));
   }

   /**
    * Removes TV
    *
    * @return The Remove TV Packet
    */
   protected byte[] removeTV(RemoveTV packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(2);
      mplew.writeShort(SendOpcode.REMOVE_TV.getValue());
      return mplew.getPacket();
   }

   /**
    * Gets a packet to remove a special map object.
    *
    * @return The packet removing the object.
    */
   protected byte[] removeSummon(RemoveSummon packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(11);
      mplew.writeShort(SendOpcode.REMOVE_SPECIAL_MAPOBJECT.getValue());
      mplew.writeInt(packet.getSummon().getOwner().getId());
      mplew.writeInt(packet.getSummon().getObjectId());
      mplew.write(packet.isAnimated() ? 4 : 1); // ?
      return mplew.getPacket();
   }

   protected byte[] removeKite(RemoveKite packet) {    // thanks to Arnah (Vertisy)
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.REMOVE_KITE.getValue());
      mplew.write(packet.animationType()); // 0 is 10/10, 1 just vanishes
      mplew.writeInt(packet.objectId());
      return mplew.getPacket();
   }

   protected byte[] removePlayerFromMap(RemovePlayer packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.REMOVE_PLAYER_FROM_MAP.getValue());
      mplew.writeInt(packet.characterId());
      return mplew.getPacket();
   }

   /**
    * animation: 0 - expire<br/> 1 - without animation<br/> 2 - pickup<br/> 4 -
    * explode<br/> cid is ignored for 0 and 1.<br /><br />Flagging pet as true
    * will make a pet pick up the item.
    *
    * @return
    */
   protected byte[] removeItemFromMap(RemoveItem packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.REMOVE_ITEM_FROM_MAP.getValue());
      mplew.write(packet.animation()); // expire
      mplew.writeInt(packet.objectId());
      if (packet.animation() >= 2) {
         mplew.writeInt(packet.characterId());
         if (packet.pet()) {
            mplew.write(packet.slot());
         }
      }
      return mplew.getPacket();
   }

   protected byte[] removeMist(RemoveMist packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.REMOVE_MIST.getValue());
      mplew.writeInt(packet.objectId());
      return mplew.getPacket();
   }

   protected byte[] removeNPC(RemoveNPC packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.REMOVE_NPC.getValue());
      mplew.writeInt(packet.objectId());
      return mplew.getPacket();
   }

   /**
    * Sends a request to remove Mir<br>
    *
    * @return The packet
    */
   protected byte[] removeDragon(RemoveDragon packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.REMOVE_DRAGON.getValue());
      mplew.writeInt(packet.characterId());
      return mplew.getPacket();
   }
}