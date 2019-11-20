package net.server.channel.packet

import net.server.MaplePacket

class QuestActionPacket(private var _action: Byte, private var _questId: Short, private var _itemId: Int, private var _npc: Int, private var _selection: Int, private var _x: Int, private var _y: Int) extends MaplePacket {
  def action: Byte = _action

  def questId: Short = _questId

  def itemId: Int = _itemId

  def npc: Int = _npc

  def selection: Int = _selection

  def x: Int = _x

  def y: Int = _y
}
