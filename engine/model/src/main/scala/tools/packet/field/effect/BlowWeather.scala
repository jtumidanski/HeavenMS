package tools.packet.field.effect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class BlowWeather(private var _message: String, private var _itemId: Int, private var _active: Boolean) extends PacketInput {
  def message: String = _message

  def itemId: Int = _itemId

  def active: Boolean = _active

  override def opcode(): SendOpcode = SendOpcode.BLOW_WEATHER
}