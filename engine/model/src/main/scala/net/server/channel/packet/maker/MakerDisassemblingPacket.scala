package net.server.channel.packet.maker

class MakerDisassemblingPacket(private var _type: Int, private var _toCreate: Int, private var _inventoryType: Int, private var _position: Int) extends BaseMakerActionPacket(_type = _type, _toCreate = _toCreate) {
  def inventoryType: Int = _inventoryType

  def position: Int = _position
}
