package net.server.channel.packet.maker

import net.server.MaplePacket

class BaseMakerActionPacket(private var _type: Int, private var _toCreate: Int) extends MaplePacket {
     def theType: Int = _type
     def toCreate: Int = _toCreate
}
