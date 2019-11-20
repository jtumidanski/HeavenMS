package tools.packet.character.interaction

import net.opcodes.SendOpcode
import server.maps.MaplePlayerShopSoldItem
import tools.packet.PacketInput

class PlayerShopOwnerUpdate(private var _soldItem: MaplePlayerShopSoldItem, private var _position: Int) extends PacketInput {
  def soldItem: MaplePlayerShopSoldItem = _soldItem

  def position: Int = _position

  override def opcode(): SendOpcode = SendOpcode.PLAYER_INTERACTION
}