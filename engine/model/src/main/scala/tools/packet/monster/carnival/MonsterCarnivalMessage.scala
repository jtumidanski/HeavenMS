package tools.packet.monster.carnival

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MonsterCarnivalMessage(private var _message: Byte) extends PacketInput {
  def message: Byte = _message

  override def opcode(): SendOpcode = SendOpcode.MONSTER_CARNIVAL_MESSAGE
}