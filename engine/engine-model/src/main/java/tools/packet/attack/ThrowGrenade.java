package tools.packet.attack;

import java.awt.Point;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ThrowGrenade(Integer characterId, Point position, Integer keyDown, Integer skillId,
                           Integer skillLevel) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.THROW_GRENADE;
   }
}
