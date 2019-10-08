package tools.packet.field.effect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class RemoveWeather() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.BLOW_WEATHER
}