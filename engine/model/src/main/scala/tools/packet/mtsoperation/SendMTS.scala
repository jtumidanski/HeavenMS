package tools.packet.mtsoperation

import net.opcodes.SendOpcode
import server.MTSItemInfo
import tools.packet.PacketInput

class SendMTS(private var _items: java.util.List[MTSItemInfo], private var _tab: Int, private var _type: Int, private var _page: Int, private var _pages: Int) extends PacketInput {
  def items: java.util.List[MTSItemInfo] = _items

  def tab: Int = _tab

  def theType: Int = _type

  def page: Int = _page

  def pages: Int = _pages

  override def opcode(): SendOpcode = SendOpcode.MTS_OPERATION
}