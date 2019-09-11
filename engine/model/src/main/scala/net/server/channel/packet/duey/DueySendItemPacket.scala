package net.server.channel.packet.duey

class DueySendItemPacket(private var _operation: Byte, private var _inventoryId: Byte, private var _itemPosition: Short, private var _amount: Short, private var _mesos: Int, private var _recipient: String, private var _quick: Boolean, private var _message: String) extends BaseDueyPacket(_operation = _operation) {
  def inventoryId: Byte = _inventoryId

  def itemPosition: Short = _itemPosition

  def amount: Short = _amount

  def mesos: Int = _mesos

  def recipient: String = _recipient

  def quick: Boolean = _quick

  def message: String = _message
}
