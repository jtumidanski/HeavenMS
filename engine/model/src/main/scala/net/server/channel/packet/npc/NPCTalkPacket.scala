package net.server.channel.packet.npc

class NPCTalkPacket(private var _available: Int, private var _first: Int, private var _second: Short) extends BaseNPCAnimationPacket(_available = _available) {
  def first: Int = _first

  def second: Short = _second
}
