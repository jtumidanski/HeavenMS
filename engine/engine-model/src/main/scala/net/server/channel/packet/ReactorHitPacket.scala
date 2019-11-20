package net.server.channel.packet

import net.server.MaplePacket

class ReactorHitPacket( private var _objectId: Int,  private var _characterPosition: Int,  private var _stance: Short,  private var _skillId: Int) extends MaplePacket {
     def objectId: Int = _objectId
     def characterPosition: Int = _characterPosition
     def stance: Short = _stance
     def skillId: Int = _skillId
}
