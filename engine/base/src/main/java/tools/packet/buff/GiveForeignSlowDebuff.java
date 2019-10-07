package tools.packet.buff;

import java.util.List;

import client.MapleDisease;
import net.opcodes.SendOpcode;
import server.life.MobSkill;
import tools.Pair;
import tools.packet.PacketInput;

public class GiveForeignSlowDebuff implements PacketInput {
   private int characterId;

   private List<Pair<MapleDisease, Integer>> statups;

   private MobSkill mobSkill;

   public GiveForeignSlowDebuff(int characterId, List<Pair<MapleDisease, Integer>> statups, MobSkill mobSkill) {
      this.characterId = characterId;
      this.statups = statups;
      this.mobSkill = mobSkill;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.GIVE_FOREIGN_BUFF;
   }

   public int getCharacterId() {
      return characterId;
   }

   public List<Pair<MapleDisease, Integer>> getStatups() {
      return statups;
   }

   public MobSkill getMobSkill() {
      return mobSkill;
   }
}
