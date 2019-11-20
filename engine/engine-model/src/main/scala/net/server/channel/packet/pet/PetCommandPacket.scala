package net.server.channel.packet.pet

import net.server.MaplePacket

class PetCommandPacket( private var _petId: Int,  private var _command: Byte) extends MaplePacket {
     def petId: Int = _petId
     def command: Byte = _command
}
