package tools.packet

import net.opcodes.SendOpcode

class QuickSlotKey(private var _keyMap: Array[Byte]) extends PacketInput {
  def keyMap: Array[Byte] = _keyMap

  override def opcode(): SendOpcode = SendOpcode.QUICKSLOT_INIT
}