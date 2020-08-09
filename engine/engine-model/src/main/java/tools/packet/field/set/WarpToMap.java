package tools.packet.field.set;

import java.awt.Point;
import java.util.Optional;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record WarpToMap(Integer channelId, Integer mapId, Integer spawnPoint, Integer characterHp,
                        Optional<Point> spawnPosition) implements PacketInput {
   public WarpToMap(Integer channelId, Integer mapId, Integer spawnPoint, Integer characterHp) {
      this(channelId, mapId, spawnPoint, characterHp, Optional.empty());
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.SET_FIELD;
   }
}