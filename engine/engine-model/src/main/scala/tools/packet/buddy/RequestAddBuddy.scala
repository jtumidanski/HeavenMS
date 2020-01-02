package tools.packet.buddy

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class RequestAddBuddy(private var _characterIdFrom: Int, private var _characterIdTo: Int,
                      private var _characterNameFrom: String) extends PacketInput {
  def characterIdFrom: Int = _characterIdFrom

  def characterIdTo: Int = _characterIdTo

  def characterNameFrom: String = _characterNameFrom

  override def opcode(): SendOpcode = SendOpcode.BUDDY_LIST
}