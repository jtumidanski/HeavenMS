package net.server.channel.packet

import net.server.MaplePacket

class AssignAPPacket( private var _jobId: Byte,  private var _types: Array[Int],  private var _gains: Array[Int]) extends MaplePacket {
     def jobId: Byte = _jobId
     def types: Array[Int] = _types
     def gains: Array[Int] = _gains
}
