package tools.packet.stat;

import java.util.List;

import client.MapleCharacter;
import client.MapleStat;
import net.opcodes.SendOpcode;
import tools.Pair;
import tools.packet.PacketInput;

public class UpdatePlayerStats implements PacketInput {
   private final List<Pair<MapleStat, Integer>> statup;

   private final boolean enableActions;

   private final MapleCharacter mapleCharacter;

   public UpdatePlayerStats(List<Pair<MapleStat, Integer>> statup, boolean enableActions, MapleCharacter mapleCharacter) {
      this.statup = statup;
      this.enableActions = enableActions;
      this.mapleCharacter = mapleCharacter;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.STAT_CHANGED;
   }

   public List<Pair<MapleStat, Integer>> getStatup() {
      return statup;
   }

   public boolean isEnableActions() {
      return enableActions;
   }

   public MapleCharacter getMapleCharacter() {
      return mapleCharacter;
   }
}
