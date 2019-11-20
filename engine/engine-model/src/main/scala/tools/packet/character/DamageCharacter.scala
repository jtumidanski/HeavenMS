package tools.packet.character

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class DamageCharacter(private var _skill: Int, private var _monsterIdFrom: Int, private var _characterId: Int,
                      private var _damage: Int, private var _fake: Int, private var _direction: Int,
                      private var _pgmr: Boolean, private var _pgmr_1: Int, private var _is_pg: Boolean,
                      private var _objectId: Int, private var _xPosition: Int, private var _yPosition: Int) extends PacketInput {
  def skill: Int = _skill

  def monsterIdFrom: Int = _monsterIdFrom

  def characterId: Int = _characterId

  def damage: Int = _damage

  def fake: Int = _fake

  def direction: Int = _direction

  def pgmr: Boolean = _pgmr

  def pgmr_1: Int = _pgmr_1

  def is_pg: Boolean = _is_pg

  def objectId: Int = _objectId

  def xPosition: Int = _xPosition

  def yPosition: Int = _yPosition

  override def opcode(): SendOpcode = SendOpcode.DAMAGE_PLAYER
}