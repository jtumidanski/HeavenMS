package net.server.channel.packet.cash.operation

class NameChangePacket(private var _action: Int, private var _itemId: Int, private var _oldName: String, private var _newName: String) extends BaseCashOperationPacket(_action = _action) {
  def itemId: Int = _itemId

  def oldName: String = _oldName

  def newName: String = _newName
}
