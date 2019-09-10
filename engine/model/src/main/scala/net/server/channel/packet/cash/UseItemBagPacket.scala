package net.server.channel.packet.cash

class UseItemBagPacket(private var _position: Short, private var _itemId: Int) extends AbstractUseCashItemPacket(_position, _itemId) {
}
