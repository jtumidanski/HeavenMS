package net.server.channel.packet.duey

class DueyClaimPackagePacket(private var _operation: Byte, private var _packageId: Int) extends BaseDueyPacket(_operation = _operation) {
  def packageId: Int = _packageId
}
