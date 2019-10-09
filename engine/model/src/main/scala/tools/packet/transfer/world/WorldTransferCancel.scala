package tools.packet.transfer.world

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class WorldTransferCancel(private var _success: Boolean) extends PacketInput {
  def success: Boolean = _success

  override def opcode(): SendOpcode = SendOpcode.CANCEL_TRANSFER_WORLD_RESULT
}