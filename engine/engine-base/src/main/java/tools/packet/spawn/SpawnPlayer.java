package tools.packet.spawn;

import client.MapleCharacter;
import client.MapleClient;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public class SpawnPlayer implements PacketInput {
   private MapleClient target;

   private MapleCharacter character;

   private boolean enteringField;

   public SpawnPlayer(MapleClient target, MapleCharacter character, boolean enteringField) {
      this.target = target;
      this.character = character;
      this.enteringField = enteringField;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.SPAWN_PLAYER;
   }

   public MapleClient getTarget() {
      return target;
   }

   public MapleCharacter getCharacter() {
      return character;
   }

   public boolean isEnteringField() {
      return enteringField;
   }
}
