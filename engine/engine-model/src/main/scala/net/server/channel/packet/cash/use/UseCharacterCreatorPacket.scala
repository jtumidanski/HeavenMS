package net.server.channel.packet.cash.use

class UseCharacterCreatorPacket(private var _position: Short, private var _itemId: Int, private var _name: String,
                                private var _face: Int, private var _hair: Int, private var _hairColor: Int,
                                private var _skin: Int, private var _gender: Int, private var _jobId: Int,
                                private var _improveSp: Int) extends AbstractUseCashItemPacket(_position, _itemId) {
  def name: String = _name

  def face: Int = _face

  def hair: Int = _hair

  def hairColor: Int = _hairColor

  def skin: Int = _skin

  def gender: Int = _gender

  def jobId: Int = _jobId

  def improveSp: Int = _improveSp
}
