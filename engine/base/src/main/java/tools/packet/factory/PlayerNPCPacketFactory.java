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
      registry.setHandler(GetPlayerNPC.class, packet -> create(SendOpcode.IMITATED_NPC_DATA, this::getPlayerNPC, packet));
      registry.setHandler(RemovePlayerNPC.class, packet -> create(SendOpcode.IMITATED_NPC_DATA, this::removePlayerNPC, packet));
   }

   protected void getPlayerNPC(MaplePacketLittleEndianWriter writer, GetPlayerNPC packet) {     // thanks to Arnah
      writer.write(0x01);
      writer.writeInt(packet.getPlayerNPC().getScriptId());
      writer.writeMapleAsciiString(packet.getPlayerNPC().getName());
      writer.write(packet.getPlayerNPC().getGender());
      writer.write(packet.getPlayerNPC().getSkin());
      writer.writeInt(packet.getPlayerNPC().getFace());
      writer.write(0);
      writer.writeInt(packet.getPlayerNPC().getHair());
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
         writer.write(entry.getKey());
         writer.writeInt(entry.getValue());
      }
      writer.write(0xFF);
      for (Map.Entry<Short, Integer> entry : maskedEquip.entrySet()) {
         writer.write(entry.getKey());
         writer.writeInt(entry.getValue());
      }
      writer.write(0xFF);
      Integer cWeapon = equip.get((byte) -111);
      writer.writeInt(Objects.requireNonNullElse(cWeapon, 0));
      for (int i = 0; i < 3; i++) {
         writer.writeInt(0);
      }
   }

   protected void removePlayerNPC(MaplePacketLittleEndianWriter writer, RemovePlayerNPC packet) {
      writer.write(0x00);
      writer.writeInt(packet.objectId());
   }
}