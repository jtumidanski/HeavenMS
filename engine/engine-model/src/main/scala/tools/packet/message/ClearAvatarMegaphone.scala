package tools.packet.message

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ClearAvatarMegaphone() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.CLEAR_AVATAR_MEGAPHONE
}