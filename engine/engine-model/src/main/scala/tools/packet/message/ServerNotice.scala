package tools.packet.message

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ServerNotice(private var _type: Int, private var _channel: Int, private var _message: String, private var _smegaEar: Boolean) extends PacketInput {
  def theType: Int = _type

  def channel: Int = _channel

  def message: String = _message

  def smegaEar: Boolean = _smegaEar

  def this(_type: Int, _message: String) = {
    this(_type, 0, _message, false)
  }

  override def opcode(): SendOpcode = SendOpcode.SERVER_MESSAGE
}