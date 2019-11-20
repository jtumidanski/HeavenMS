package tools.packet.statusinfo

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class UpdateDojoStats(private var _dojoPoints: Int, private var _finishedDojoTutorial: Boolean,
                      private var _belt: Int) extends PacketInput {
  def dojoPoints: Int = _dojoPoints

  def finishedDojoTutorial: Boolean = _finishedDojoTutorial

  def belt: Int = _belt

  override def opcode(): SendOpcode = SendOpcode.SHOW_STATUS_INFO
}