package tools.packet.spawn;

import client.MapleCharacter;
import client.inventory.MaplePet;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public class ShowPet implements PacketInput {
   private final MapleCharacter character;

   private final MaplePet pet;

   private final boolean remove;

   private final boolean hunger;

   public ShowPet(MapleCharacter character, MaplePet pet, boolean remove, boolean hunger) {
      this.character = character;
      this.pet = pet;
      this.remove = remove;
      this.hunger = hunger;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.SPAWN_PET;
   }

   public MapleCharacter getCharacter() {
      return character;
   }

   public MaplePet getPet() {
      return pet;
   }

   public boolean isRemove() {
      return remove;
   }

   public boolean isHunger() {
      return hunger;
   }
}
