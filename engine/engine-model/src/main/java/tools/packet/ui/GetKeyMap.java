package tools.packet.ui;

import java.util.Map;

import client.KeyBinding;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GetKeyMap(Map<Integer, KeyBinding> bindings) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.KEYMAP;
   }
}