package tools.packet.ui

import client.KeyBinding
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetKeyMap(private var _bindings: java.util.Map[java.lang.Integer, KeyBinding]) extends PacketInput {
  def bindings: java.util.Map[java.lang.Integer, KeyBinding] = _bindings

  override def opcode(): SendOpcode = SendOpcode.KEYMAP
}