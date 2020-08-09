package tools.packet.character.interaction;

import client.inventory.Item;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record TradeItemAdd(Byte number, Item item) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.PLAYER_INTERACTION;
   }
}