package net.server.channel.packet.pet

import net.server.MaplePacket
import server.movement.LifeMovementFragment

class PetMovementPacket(private var _petId: Int, private var _numberOfCommands: Byte, private var _commands: java.util.List[LifeMovementFragment]) extends MaplePacket {
  def petId: Int = _petId

  def numberOfCommands: Byte = _numberOfCommands

  def commands: java.util.List[LifeMovementFragment] = _commands
}
