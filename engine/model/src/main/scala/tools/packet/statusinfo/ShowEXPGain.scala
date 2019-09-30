package tools.packet.statusinfo

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowEXPGain(private var _gain: Int, private var _equip: Int, private var _party: Int,
                  private var _inChat: Boolean, private var _white: Boolean) extends PacketInput {
  def gain: Int = _gain

  def equip: Int = _equip

  def party: Int = _party

  def inChat: Boolean = _inChat

  def white: Boolean = _white

  override def opcode(): SendOpcode = SendOpcode.SHOW_STATUS_INFO
}