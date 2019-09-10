package net.server.channel.packet

import net.server.MaplePacket

class ScrollPacket( private var _slot: Short,  private var _destination: Short,  private var _whiteScroll: Byte) extends MaplePacket {
     def slot: Short = _slot
     def destination: Short = _destination
     def whiteScroll: Byte = _whiteScroll
}
