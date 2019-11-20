package net.server.channel.packet.command.admin

class SummonMonstersItemPacket(private var _mode: Byte, private var _summonItemId: Int) extends BaseAdminCommandPacket(_mode = _mode) {
  def summonItemId: Int = _summonItemId
}
