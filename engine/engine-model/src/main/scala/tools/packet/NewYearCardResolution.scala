package tools.packet

import client.newyear.NewYearCardRecord
import net.opcodes.SendOpcode

class NewYearCardResolution(private var _characterId: Int, private var _newYearCardRecord: NewYearCardRecord,
                            private var _mode: Int, private var _message: Int) extends PacketInput {
  def characterId: Int = _characterId

  def newYearCardRecord: NewYearCardRecord = _newYearCardRecord

  def mode: Int = _mode

  def message: Int = _message

  override def opcode(): SendOpcode = SendOpcode.NEW_YEAR_CARD_RES
}