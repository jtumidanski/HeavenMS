package tools.packet.storage

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MesoStorage(private var _slots: Byte, private var _meso: Int) extends PacketInput {
  def slots: Byte = _slots

  def meso: Int = _meso

  override def opcode(): SendOpcode = SendOpcode.STORAGE
}