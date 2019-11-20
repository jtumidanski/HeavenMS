package tools.packet.storage

import client.inventory.Item
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetStorage(private var _npcId: Int, private var _slots: Byte, private var _items: java.util.List[Item], private var _meso: Int) extends PacketInput {
  def npcId: Int = _npcId

  def slots: Byte = _slots

  def items: java.util.List[Item] = _items

  def meso: Int = _meso

  override def opcode(): SendOpcode = SendOpcode.STORAGE
}