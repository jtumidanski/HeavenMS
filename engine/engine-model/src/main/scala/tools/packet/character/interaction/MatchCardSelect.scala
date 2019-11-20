package tools.packet.character.interaction

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MatchCardSelect(private var _turn: Int, private var _slot: Int, private var _firstSlot: Int, private var _type: Int) extends PacketInput {
  def turn: Int = _turn

  def slot: Int = _slot

  def firstSlot: Int = _firstSlot

  def theType: Int = _type

  override def opcode(): SendOpcode = SendOpcode.PLAYER_INTERACTION
}