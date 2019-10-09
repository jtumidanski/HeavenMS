package tools.packet

import net.opcodes.SendOpcode

class DojoWarpUp() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.DOJO_WARP_UP
}