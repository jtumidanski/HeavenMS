package tools.packet.buff

import client.MapleAbnormalStatus
import net.opcodes.SendOpcode
import server.life.MobSkill
import tools.Pair
import tools.packet.PacketInput

class GiveForeignAbnormalStatusSlow(private var _characterId: Int,
                                    private var _statIncreases: java.util.List[Pair[MapleAbnormalStatus, java.lang.Integer]],
                                    private var _mobSkill: MobSkill) extends PacketInput {
  def characterId: Int = _characterId

  def statIncreases: java.util.List[Pair[MapleAbnormalStatus, java.lang.Integer]] = _statIncreases

  def mobSkill: MobSkill = _mobSkill

  override def opcode(): SendOpcode = SendOpcode.GIVE_FOREIGN_BUFF
}