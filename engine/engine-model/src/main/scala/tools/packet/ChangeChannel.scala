package tools.packet

import java.net.InetAddress

import net.opcodes.SendOpcode

class ChangeChannel(private var _inetAddress: InetAddress, private var _port: Int) extends PacketInput {
  def inetAddress: InetAddress = _inetAddress

  def port: Int = _port

  override def opcode(): SendOpcode = SendOpcode.CHANGE_CHANNEL
}