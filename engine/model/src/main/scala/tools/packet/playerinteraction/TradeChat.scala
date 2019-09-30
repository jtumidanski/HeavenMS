package tools.packet.playerinteraction

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class TradeChat(private var _name: String, private var _chat: String, private var _owner: Boolean) extends PacketInput {
  def name: String = _name

  def chat: String = _chat

  def owner: Boolean = _owner

  override def opcode(): SendOpcode = SendOpcode.PLAYER_INTERACTION
}