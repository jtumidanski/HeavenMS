package tools.packet.ui

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class RefreshTeleportRockMapList(private var _vips: java.util.List[java.lang.Integer], private var _regulars: java.util.List[java.lang.Integer], private var _delete: Boolean, private var _vip: Boolean) extends PacketInput {
  def vips: java.util.List[java.lang.Integer] = _vips

  def regulars: java.util.List[java.lang.Integer] = _regulars

  def delete: Boolean = _delete

  def vip: Boolean = _vip

  override def opcode(): SendOpcode = SendOpcode.MAP_TRANSFER_RESULT
}