package tools.packet.playerinteraction

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class PlayerShopChat(private var _name: String, private var _chat: String, private var _slot: Byte) extends PacketInput {
  def name: String = _name

  def chat: String = _chat

  def slot: Byte = _slot

  override def opcode(): SendOpcode = SendOpcode.PLAYER_INTERACTION
}