package net.server.channel.packet

class UseOwlSearchPacket(private var _position: Short, private var _itemId: Int, private var _searchedItemId:Int) extends AbstractUseCashItemPacket(_position, _itemId) {
  def searchedItemId: Int = _searchedItemId
}
