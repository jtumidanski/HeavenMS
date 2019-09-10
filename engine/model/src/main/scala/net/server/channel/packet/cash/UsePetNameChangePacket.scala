package net.server.channel.packet.cash

class UsePetNameChangePacket(private var _position: Short, private var _itemId: Int, private var _newName:String) extends AbstractUseCashItemPacket(_position, _itemId) {
  def newName: String = _newName
}
