package net.server.channel.packet.newyear

class CardAcceptedPacket(private var _reqMode: Byte, private var _cardId: Int) extends BaseNewYearCardPacket(_reqMode = _reqMode) {
  def cardId: Int = _cardId
}
