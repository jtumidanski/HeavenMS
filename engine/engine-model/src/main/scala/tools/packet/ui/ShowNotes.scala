package tools.packet.ui

import client.database.data.NoteData
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowNotes(private var _notes: java.util.List[NoteData]) extends PacketInput {
  def notes: java.util.List[NoteData] = _notes

  override def opcode(): SendOpcode = SendOpcode.MEMO_RESULT
}