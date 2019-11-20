package tools.packet.login

import net.opcodes.SendOpcode
import tools.packet.PacketInput
import tools.packet.login.LoginFailedReason

class LoginFailed(private var _reason: LoginFailedReason) extends PacketInput {
  def reason: LoginFailedReason = _reason

  override def opcode(): SendOpcode = SendOpcode.LOGIN_STATUS
}
