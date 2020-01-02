package tools.packet.buff

import client.MapleAbnormalStatus
import net.opcodes.SendOpcode
import server.life.MobSkill
import tools.Pair
import tools.packet.PacketInput

class GiveAbnormalStatus(private var _statIncreases: java.util.List[Pair[MapleAbnormalStatus, java.lang.Integer]], private var _mobSkill: MobSkill) extends PacketInput {
  def statIncreases: java.util.List[Pair[MapleAbnormalStatus, java.lang.Integer]] = _statIncreases

  def mobSkill: MobSkill = _mobSkill

  override def opcode(): SendOpcode = SendOpcode.GIVE_BUFF
}