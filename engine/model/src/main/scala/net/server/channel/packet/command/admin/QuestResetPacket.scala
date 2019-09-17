package net.server.channel.packet.command.admin

class QuestResetPacket(private var _mode: Byte, private var _questId: Int) extends BaseAdminCommandPacket(_mode = _mode) {
  def questId: Int = _questId
}
