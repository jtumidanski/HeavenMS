package net.server.channel.packet

class UsePetConsumePacket(private var _position: Short, private var _itemId: Int) extends AbstractUseCashItemPacket(_position, _itemId) {
}
