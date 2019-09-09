package net.server.channel.packet

class UseJukeboxPacket(private var _position: Short, private var _itemId: Int) extends AbstractUseCashItemPacket(_position, _itemId) {
}
