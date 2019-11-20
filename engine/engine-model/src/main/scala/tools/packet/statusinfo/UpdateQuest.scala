package tools.packet.statusinfo

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class UpdateQuest(private var _questId: Int, private var _questStatusId: Int, private var _infoNumber: Int,
                  private var _questData: String, private var _infoUpdate: Boolean) extends PacketInput {
  def questId: Int = _questId

  def questStatusId: Int = _questStatusId

  def infoNumber: Int = _infoNumber

  def questData: String = _questData

  def infoUpdate: Boolean = _infoUpdate

  override def opcode(): SendOpcode = SendOpcode.SHOW_STATUS_INFO
}