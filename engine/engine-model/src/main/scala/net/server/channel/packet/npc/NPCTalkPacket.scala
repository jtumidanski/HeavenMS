package net.server.channel.packet.npc

class NPCTalkPacket(private var _available: Int, private var _first: Int, private var _second: Byte, private var _third: Byte) extends BaseNPCAnimationPacket(_available = _available) {
  def first: Int = _first

  def second: Byte = _second

  def third: Byte = _third
}
