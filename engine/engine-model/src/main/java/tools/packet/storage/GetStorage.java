package tools.packet.storage;

import java.util.List;

import client.inventory.Item;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GetStorage(Integer npcId, Byte slots, List<Item> items, Integer meso) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.STORAGE;
   }
}