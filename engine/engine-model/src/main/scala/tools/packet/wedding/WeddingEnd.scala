package tools.packet.wedding

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class WeddingEnd(private var _blessEffect: Boolean, private var _groomId: Int, private var _brideId: Int,
                 private var _step: Byte) extends PacketInput {
  def blessEffect: Boolean = _blessEffect

  def groomId: Int = _groomId

  def brideId: Int = _brideId

  def step: Byte = _step

  override def opcode(): SendOpcode = SendOpcode.WEDDING_CEREMONY_END
}