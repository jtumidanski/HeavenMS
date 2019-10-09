package tools.packet.transfer.world

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class WorldTransferError(private var _error: Int) extends PacketInput {
  def error: Int = _error

  override def opcode(): SendOpcode = SendOpcode.CASHSHOP_CHECK_TRANSFER_WORLD_POSSIBLE_RESULT
}