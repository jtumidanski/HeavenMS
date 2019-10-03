package tools.packet.message

import client.inventory.Item
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GachaponMessage(private var _item: Item, private var _town: String, private var _characterName: String) extends PacketInput {
  def item: Item = _item

  def town: String = _town

  def characterName: String = _characterName

  override def opcode(): SendOpcode = SendOpcode.SERVERMESSAGE
}