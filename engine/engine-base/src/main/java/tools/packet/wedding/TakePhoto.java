package tools.packet.wedding;

import java.util.List;

import client.MapleCharacter;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public class TakePhoto implements PacketInput {
   private final String reservedGroomName;

   private final String reservedBrideName;

   private final int field;

   private final List<MapleCharacter> attendees;

   public TakePhoto(String reservedGroomName, String reservedBrideName, int field, List<MapleCharacter> attendees) {
      this.reservedGroomName = reservedGroomName;
      this.reservedBrideName = reservedBrideName;
      this.field = field;
      this.attendees = attendees;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.WEDDING_PHOTO;
   }

   public String getReservedGroomName() {
      return reservedGroomName;
   }

   public String getReservedBrideName() {
      return reservedBrideName;
   }

   public int getField() {
      return field;
   }

   public List<MapleCharacter> getAttendees() {
      return attendees;
   }
}
