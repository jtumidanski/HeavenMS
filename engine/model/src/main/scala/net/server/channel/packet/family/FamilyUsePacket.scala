package net.server.channel.packet.family

import net.server.MaplePacket

class FamilyUsePacket( private var _entitlementId: Int,  private var _characterName: String) extends MaplePacket {
     def entitlementId: Int = _entitlementId
     def characterName: String = _characterName
}
