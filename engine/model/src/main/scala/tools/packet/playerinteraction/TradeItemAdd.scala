package tools.packet.playerinteraction

import client.inventory.Item
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class TradeItemAdd( private var _number: Byte,  private var _item: Item) extends PacketInput {
     def number: Byte = _number
     def item: Item = _item

  override def opcode(): SendOpcode = SendOpcode.PLAYER_INTERACTION
}