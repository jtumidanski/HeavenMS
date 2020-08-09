package tools.packet.ui;

import java.util.List;

import client.database.data.NoteData;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowNotes(List<NoteData> notes) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MEMO_RESULT;
   }
}