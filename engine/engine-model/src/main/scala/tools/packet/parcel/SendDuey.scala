package tools.packet.parcel

import client.DueyAction
import net.opcodes.SendOpcode
import server.DueyPackage
import tools.packet.PacketInput

class SendDuey(private var _operation: DueyAction, private var _packages: Option[java.util.List[DueyPackage]]) extends PacketInput {
  def operation: DueyAction = _operation

  def packages: Option[java.util.List[DueyPackage]] = _packages

  def this(_operation: DueyAction) = {
    this(_operation, Option.empty)
  }

  override def opcode(): SendOpcode = SendOpcode.PARCEL
}