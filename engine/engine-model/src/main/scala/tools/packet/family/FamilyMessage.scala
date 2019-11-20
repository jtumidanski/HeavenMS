package tools.packet.family

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class FamilyMessage(private var _type: Int, private var _mesos: Int) extends PacketInput {
  def theType: Int = _type

  def mesos: Int = _mesos

  override def opcode(): SendOpcode = SendOpcode.FAMILY_RESULT
}