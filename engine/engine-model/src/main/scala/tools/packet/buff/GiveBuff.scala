package tools.packet.buff

import client.MapleBuffStat
import net.opcodes.SendOpcode
import tools.Pair
import tools.packet.PacketInput

class GiveBuff(private var _buffId: Int, private var _buffLength: Int, private var _statups: java.util.List[Pair[MapleBuffStat, java.lang.Integer]]) extends PacketInput {
  def buffId: Int = _buffId

  def buffLength: Int = _buffLength

  def statups: java.util.List[Pair[MapleBuffStat, java.lang.Integer]] = _statups

  override def opcode(): SendOpcode = SendOpcode.GIVE_BUFF
}