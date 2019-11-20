package net.server.channel.packet.party

class ChangeLeaderPartyPacket(private var _operation: Int, private var _leaderId: Int) extends BasePartyOperationPacket(_operation = _operation) {
  def leaderId: Int = _leaderId
}

