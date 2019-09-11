package net.server.channel.packet.pet

import net.server.MaplePacket

class PetChatPacket(private var _petId: Int, private var _act: Int, private var _text: String) extends MaplePacket {
  def petId: Int = _petId

  def act: Int = _act

  def text: String = _text
}
