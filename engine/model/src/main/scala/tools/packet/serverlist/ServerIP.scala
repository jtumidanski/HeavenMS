package tools.packet.serverlist

import java.net.InetAddress

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ServerIP(private var _inetAddress: InetAddress, private var _port: Int, private var _clientId: Int) extends PacketInput {
  def inetAddress: InetAddress = _inetAddress

  def port: Int = _port

  def clientId: Int = _clientId

  override def opcode(): SendOpcode = SendOpcode.SERVER_IP
}