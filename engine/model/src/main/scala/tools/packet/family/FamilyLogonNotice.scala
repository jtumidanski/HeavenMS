package tools.packet.family

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class FamilyLogonNotice(private var _characterName: String, private var _loggedIn: Boolean) extends PacketInput {
  def characterName: String = _characterName

  def loggedIn: Boolean = _loggedIn

  override def opcode(): SendOpcode = SendOpcode.FAMILY_NOTIFY_LOGIN_OR_LOGOUT
}