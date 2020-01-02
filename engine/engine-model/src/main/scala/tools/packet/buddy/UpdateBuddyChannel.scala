package tools.packet.buddy

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class UpdateBuddyChannel(private var _characterId: Int, private var _channel: Int) extends PacketInput {
  def characterId: Int = _characterId

  def channel: Int = _channel

  override def opcode(): SendOpcode = SendOpcode.BUDDY_LIST
}