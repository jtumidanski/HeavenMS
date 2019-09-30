package tools.packet.partyoperation;

import net.opcodes.SendOpcode;
import net.server.world.MapleParty;
import tools.packet.PacketInput;

public class PartyCreated implements PacketInput {
   private MapleParty party;

   private int partyCharacterId;

   public PartyCreated(MapleParty party, int partyCharacterId) {
      this.party = party;
      this.partyCharacterId = partyCharacterId;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.PARTY_OPERATION;
   }

   public MapleParty getParty() {
      return party;
   }

   public int getPartyCharacterId() {
      return partyCharacterId;
   }
}
