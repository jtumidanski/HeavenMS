package tools.packet.message

import client.inventory.Item
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ItemMegaphone(private var _message: String, private var _whisper: Boolean, private var _channel: Int,
                    private var _item: Item) extends PacketInput {
  def message: String = _message

  def whisper: Boolean = _whisper

  def channel: Int = _channel

  def item: Item = _item

  override def opcode(): SendOpcode = SendOpcode.SERVER_MESSAGE
}