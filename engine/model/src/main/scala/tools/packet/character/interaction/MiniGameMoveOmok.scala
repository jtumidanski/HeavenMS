package tools.packet.character.interaction

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MiniGameMoveOmok(private var _move1: Int, private var _move2: Int, private var _move3: Int) extends PacketInput {
  def move1: Int = _move1

  def move2: Int = _move2

  def move3: Int = _move3

  override def opcode(): SendOpcode = SendOpcode.PLAYER_INTERACTION
}