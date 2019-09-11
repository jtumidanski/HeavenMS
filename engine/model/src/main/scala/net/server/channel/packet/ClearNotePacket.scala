package net.server.channel.packet

class ClearNotePacket(private var _action: Int, private var _ids: Array[Int]) extends BaseNoteActionPacket(_action = _action) {
  def ids: Array[Int] = _ids
}
