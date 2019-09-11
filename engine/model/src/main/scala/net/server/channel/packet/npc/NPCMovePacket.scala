package net.server.channel.packet.npc

class NPCMovePacket(private var _available: Int, private var _movement: Array[Byte]) extends BaseNPCAnimationPacket(_available = _available) {
  def movement: Array[Byte] = _movement
}
