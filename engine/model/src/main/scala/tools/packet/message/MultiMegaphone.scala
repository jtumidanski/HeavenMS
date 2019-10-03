package tools.packet.message

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MultiMegaphone(private var _messages: Array[String], private var _channel: Int, private var _showEar: Boolean) extends PacketInput {
  def messages: Array[String] = _messages

  def channel: Int = _channel

  def showEar: Boolean = _showEar

  override def opcode(): SendOpcode = SendOpcode.SERVERMESSAGE
}