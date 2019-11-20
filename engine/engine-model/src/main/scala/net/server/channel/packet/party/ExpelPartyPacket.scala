package net.server.channel.packet.party

class ExpelPartyPacket(private var _operation: Int, private var _characterId: Int) extends BasePartyOperationPacket(_operation = _operation) {
  def characterId: Int = _characterId
}

