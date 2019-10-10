package tools.packet.factory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.character.npc.GetPlayerNPC;
import tools.packet.character.npc.RemovePlayerNPC;

public class PlayerNPCPacketFactory extends AbstractPacketFactory {
   private static PlayerNPCPacketFactory instance;

   public static PlayerNPCPacketFactory getInstance() {
      if (instance == null) {
         instance = new PlayerNPCPacketFactory();
      }
      return instance;
   }

   private PlayerNPCPacketFactory() {
      registry.setHandler(GetPlayerNPC.class, packet -> this.getPlayerNPC((GetPlayerNPC) packet));
      registry.setHandler(RemovePlayerNPC.class, packet -> this.removePlayerNPC((RemovePlayerNPC) packet));
   }

   protected byte[] getPlayerNPC(GetPlayerNPC packet) {     // thanks to Arnah
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.IMITATED_NPC_DATA.getValue());
      mplew.write(0x01);
      mplew.writeInt(packet.getPlayerNPC().getScriptId());
      mplew.writeMapleAsciiString(packet.getPlayerNPC().getName());
      mplew.write(packet.getPlayerNPC().getGender());
      mplew.write(packet.getPlayerNPC().getSkin());
      mplew.writeInt(packet.getPlayerNPC().getFace());
      mplew.write(0);
      mplew.writeInt(packet.getPlayerNPC().getHair());
      Map<Short, Integer> equip = packet.getPlayerNPC().getEquips();
      Map<Short, Integer> myEquip = new LinkedHashMap<>();
      Map<Short, Integer> maskedEquip = new LinkedHashMap<>();
      for (short position : equip.keySet()) {
         short pos = (byte) (position * -1);
         if (pos < 100 && myEquip.get(pos) == null) {
            myEquip.put(pos, equip.get(position));
         } else if ((pos > 100 && pos != 111) || pos == -128) { // don't ask. o.o
            pos -= 100;
            if (myEquip.get(pos) != null) {
               maskedEquip.put(pos, myEquip.get(pos));
            }
            myEquip.put(pos, equip.get(position));
         } else if (myEquip.get(pos) != null) {
            maskedEquip.put(pos, equip.get(position));
         }
      }
      for (Map.Entry<Short, Integer> entry : myEquip.entrySet()) {
         mplew.write(entry.getKey());
         mplew.writeInt(entry.getValue());
      }
      mplew.write(0xFF);
      for (Map.Entry<Short, Integer> entry : maskedEquip.entrySet()) {
         mplew.write(entry.getKey());
         mplew.writeInt(entry.getValue());
      }
      mplew.write(0xFF);
      Integer cWeapon = equip.get((byte) -111);
      mplew.writeInt(Objects.requireNonNullElse(cWeapon, 0));
      for (int i = 0; i < 3; i++) {
         mplew.writeInt(0);
      }
      return mplew.getPacket();
   }

   protected byte[] removePlayerNPC(RemovePlayerNPC packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.IMITATED_NPC_DATA.getValue());
      mplew.write(0x00);
      mplew.writeInt(packet.objectId());
      return mplew.getPacket();
   }
}