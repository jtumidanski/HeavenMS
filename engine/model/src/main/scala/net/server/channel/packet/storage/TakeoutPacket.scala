package net.server.channel.packet.storage

class TakeoutPacket(private var _mode: Byte, private var _type: Byte, private var _slot: Byte) extends BaseStoragePacket(_mode = _mode) {
  def theType: Byte = _type

  def slot: Byte = _slot
}
