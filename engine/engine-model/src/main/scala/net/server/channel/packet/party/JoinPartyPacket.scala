package net.server.channel.packet.party

class JoinPartyPacket(private var _operation: Int, private var _partyId: Int) extends BasePartyOperationPacket(_operation = _operation) {
  def partyId: Int = _partyId
}

