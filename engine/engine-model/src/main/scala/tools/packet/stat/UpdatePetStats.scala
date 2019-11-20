package tools.packet.stat

import client.inventory.MaplePet
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class UpdatePetStats(private var _pets: Array[MaplePet]) extends PacketInput {
  def pets: Array[MaplePet] = _pets

  override def opcode(): SendOpcode = SendOpcode.STAT_CHANGED
}