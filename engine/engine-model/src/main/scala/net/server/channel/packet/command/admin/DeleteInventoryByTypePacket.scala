package net.server.channel.packet.command.admin

class DeleteInventoryByTypePacket(private var _mode: Byte, private var _inventoryType: Byte) extends BaseAdminCommandPacket(_mode = _mode) {
  def inventoryType: Byte = _inventoryType
}
