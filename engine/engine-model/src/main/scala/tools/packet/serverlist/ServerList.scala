package tools.packet.serverlist

import net.opcodes.SendOpcode
import tools.packet.{ChannelLoad, PacketInput}

class ServerList(private var _serverId: Int, private var _serverName: String, private var _flag: Int,
                 private var _eventMsg: String, private var _channelLoad: java.util.List[ChannelLoad]) extends PacketInput {
  def serverId: Int = _serverId

  def serverName: String = _serverName

  def flag: Int = _flag

  def eventMsg: String = _eventMsg

  def channelLoad: java.util.List[ChannelLoad] = _channelLoad

  override def opcode(): SendOpcode = SendOpcode.SERVER_LIST
}