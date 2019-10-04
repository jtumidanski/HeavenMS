package tools.packet.attack

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CloseRangeAttack(private var _characterId: Int, private var _skill: Int, private var _skillLevel: Int,
                       private var _stance: Int, private var _numAttackedAndDamage: Int,
                       private var _damage: java.util.Map[java.lang.Integer, java.util.List[java.lang.Integer]],
                       private var _speed: Int, private var _direction: Int, private var _display: Int) extends PacketInput {
  def characterId: Int = _characterId

  def skill: Int = _skill

  def skillLevel: Int = _skillLevel

  def stance: Int = _stance

  def numAttackedAndDamage: Int = _numAttackedAndDamage

  def damage: java.util.Map[java.lang.Integer, java.util.List[java.lang.Integer]] = _damage

  def speed: Int = _speed

  def direction: Int = _direction

  def display: Int = _display

  override def opcode(): SendOpcode = SendOpcode.CLOSE_RANGE_ATTACK
}