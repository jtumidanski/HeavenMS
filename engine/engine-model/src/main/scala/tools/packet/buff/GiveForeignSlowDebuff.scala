package tools.packet.buff

import client.MapleDisease
import net.opcodes.SendOpcode
import server.life.MobSkill
import tools.Pair
import tools.packet.PacketInput

class GiveForeignSlowDebuff(private var _characterId: Int,
                            private var _statups: java.util.List[Pair[MapleDisease, java.lang.Integer]],
                            private var _mobSkill: MobSkill) extends PacketInput {
  def characterId: Int = _characterId

  def statups: java.util.List[Pair[MapleDisease, java.lang.Integer]] = _statups

  def mobSkill: MobSkill = _mobSkill

  override def opcode(): SendOpcode = SendOpcode.GIVE_FOREIGN_BUFF
}