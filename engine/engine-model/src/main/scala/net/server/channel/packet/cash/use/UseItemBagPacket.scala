package net.server.channel.packet.cash.use

class UseItemBagPacket(private var _position: Short, private var _itemId: Int) extends AbstractUseCashItemPacket(_position, _itemId) {
}
