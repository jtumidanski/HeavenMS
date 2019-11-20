package tools.packet.character.interaction

import net.opcodes.SendOpcode
import tools.packet.{MiniRoomError, PacketInput}

class GetMiniRoomError(private var _status: MiniRoomError) extends PacketInput {
  def status: MiniRoomError = _status

  override def opcode(): SendOpcode = SendOpcode.PLAYER_INTERACTION
}