package net.server.channel.packet.interaction

class CreateShopPlayerInteractionPacket(private var _mode: Byte, private var _createType: Byte, private var _description: String, private var _itemId: Int) extends BaseCreatePlayerInteractionPacket(_mode: Byte, _createType: Byte) {
  def description: String = _description

  def itemId: Int = _itemId
}
