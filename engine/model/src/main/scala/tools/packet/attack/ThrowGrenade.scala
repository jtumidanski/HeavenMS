package tools.packet.attack

import java.awt.Point

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ThrowGrenade(private var _characterId: Int, private var _position: Point, private var _keyDown: Int,
                   private var _skillId: Int, private var _skillLevel: Int) extends PacketInput {
  def characterId: Int = _characterId

  def position: Point = _position

  def keyDown: Int = _keyDown

  def skillId: Int = _skillId

  def skillLevel: Int = _skillLevel

  override def opcode(): SendOpcode = SendOpcode.THROW_GRENADE
}