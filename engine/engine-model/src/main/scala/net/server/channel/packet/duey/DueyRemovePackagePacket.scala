package net.server.channel.packet.duey

class DueyRemovePackagePacket(private var _operation: Byte, private var _packageId: Int) extends BaseDueyPacket(_operation = _operation) {
  def packageId: Int = _packageId
}
