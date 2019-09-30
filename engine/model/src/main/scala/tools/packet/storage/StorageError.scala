package tools.packet.storage

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class StorageError(private var _type: Byte) extends PacketInput {
  def theType: Byte = _type

  override def opcode(): SendOpcode = SendOpcode.STORAGE
}