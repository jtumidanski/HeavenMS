package tools.packet.character.interaction;

import client.MapleCharacter;
import net.opcodes.SendOpcode;
import server.MapleTrade;
import tools.packet.PacketInput;

public class GetTradeStart implements PacketInput {
   private final MapleCharacter character;

   private final MapleTrade trade;

   private final byte number;

   public GetTradeStart(MapleCharacter character, MapleTrade trade, byte number) {
      this.character = character;
      this.trade = trade;
      this.number = number;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.PLAYER_INTERACTION;
   }

   public MapleCharacter getCharacter() {
      return character;
   }

   public MapleTrade getTrade() {
      return trade;
   }

   public byte getNumber() {
      return number;
   }
}
