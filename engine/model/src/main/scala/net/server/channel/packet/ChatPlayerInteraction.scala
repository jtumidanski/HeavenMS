package net.server.channel.packet

import net.server.channel.packet.interaction.BasePlayerInteractionPacket

class ChatPlayerInteraction(private var _mode: Byte, private var _message: String) extends BasePlayerInteractionPacket(_mode = _mode) {
  def message: String = _message
}