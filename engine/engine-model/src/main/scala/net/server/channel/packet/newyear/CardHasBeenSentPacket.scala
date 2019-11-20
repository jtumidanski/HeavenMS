package net.server.channel.packet.newyear

class CardHasBeenSentPacket(private var _reqMode: Byte, private var _slot: Short, private var _itemId: Int, private var _receiver: String, private var _message: String) extends BaseNewYearCardPacket(_reqMode = _reqMode) {
  def slot: Short = _slot

  def itemId: Int = _itemId

  def receiver: String = _receiver

  def message: String = _message
}
