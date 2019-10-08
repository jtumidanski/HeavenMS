package tools.packet.party;

import net.opcodes.SendOpcode;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import net.server.world.PartyOperation;
import tools.packet.PacketInput;

public class UpdateParty implements PacketInput {
   private int forChannel;

   private MapleParty party;

   private PartyOperation operation;

   private MaplePartyCharacter target;

   public UpdateParty(int forChannel, MapleParty party, PartyOperation operation, MaplePartyCharacter target) {
      this.forChannel = forChannel;
      this.party = party;
      this.operation = operation;
      this.target = target;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.PARTY_OPERATION;
   }

   public int getForChannel() {
      return forChannel;
   }

   public MapleParty getParty() {
      return party;
   }

   public PartyOperation getOperation() {
      return operation;
   }

   public MaplePartyCharacter getTarget() {
      return target;
   }
}
