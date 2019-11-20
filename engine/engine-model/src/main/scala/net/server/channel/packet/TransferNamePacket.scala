package net.server.channel.packet

import net.server.MaplePacket

class TransferNamePacket(private var _characterId: Int, private var _birthday: Int) extends MaplePacket {
  def characterId: Int = _characterId

  def birthday: Int = _birthday
}
