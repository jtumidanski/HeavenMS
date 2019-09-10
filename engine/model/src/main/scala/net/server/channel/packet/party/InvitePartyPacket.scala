package net.server.channel.packet.party

class InvitePartyPacket(private var _operation: Int, private var _name: String) extends BasePartyOperationPacket(_operation = _operation) {
  def name: String = _name
}

