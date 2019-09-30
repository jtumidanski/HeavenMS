package tools.packet.playerinteraction;

import client.MapleCharacter;
import net.opcodes.SendOpcode;
import server.maps.MapleHiredMerchant;
import tools.packet.PacketInput;

public class GetHiredMerchant implements PacketInput {
   private MapleCharacter character;

   private MapleHiredMerchant hiredMerchant;

   private boolean firstTime;

   public GetHiredMerchant(MapleCharacter character, MapleHiredMerchant hiredMerchant, boolean firstTime) {
      this.character = character;
      this.hiredMerchant = hiredMerchant;
      this.firstTime = firstTime;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.PLAYER_INTERACTION;
   }

   public MapleCharacter getCharacter() {
      return character;
   }

   public MapleHiredMerchant getHiredMerchant() {
      return hiredMerchant;
   }

   public boolean isFirstTime() {
      return firstTime;
   }
}