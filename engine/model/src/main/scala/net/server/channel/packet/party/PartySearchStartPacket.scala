package net.server.channel.packet.party

import net.server.MaplePacket

class PartySearchStartPacket( private var _min: Int,  private var _max: Int,  private var _members: Int,  private var _jobs: Int) extends MaplePacket {
     def min: Int = _min
     def max: Int = _max
     def members: Int = _members
     def jobs: Int = _jobs
}
