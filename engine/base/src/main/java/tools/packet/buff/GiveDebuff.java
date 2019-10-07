package tools.packet.buff;

import java.util.List;

import client.MapleDisease;
import net.opcodes.SendOpcode;
import server.life.MobSkill;
import tools.Pair;
import tools.packet.PacketInput;

public class GiveDebuff implements PacketInput {
   private List<Pair<MapleDisease, Integer>> statups;

   private MobSkill mobSkill;

   public GiveDebuff(List<Pair<MapleDisease, Integer>> statups, MobSkill mobSkill) {
      this.statups = statups;
      this.mobSkill = mobSkill;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.GIVE_BUFF;
   }

   public List<Pair<MapleDisease, Integer>> getStatups() {
      return statups;
   }

   public MobSkill getMobSkill() {
      return mobSkill;
   }
}
