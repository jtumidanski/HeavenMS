package tools.packet.message

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class FindReply(private var _target: String, private var _mapId: Int, private var _mapType: Int) extends PacketInput {
  def target: String = _target

  def mapId: Int = _mapId

  def mapType: Int = _mapType

  override def opcode(): SendOpcode = SendOpcode.WHISPER
}