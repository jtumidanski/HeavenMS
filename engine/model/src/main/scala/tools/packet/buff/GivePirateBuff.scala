package tools.packet.buff

import client.MapleBuffStat
import net.opcodes.SendOpcode
import tools.Pair
import tools.packet.PacketInput

class GivePirateBuff(private var _statups: java.util.List[Pair[MapleBuffStat, java.lang.Integer]],
                     private var _buffId: Int, private var _duration: Int) extends PacketInput {
  def statups: java.util.List[Pair[MapleBuffStat, java.lang.Integer]] = _statups

  def buffId: Int = _buffId

  def duration: Int = _duration

  override def opcode(): SendOpcode = SendOpcode.GIVE_BUFF
}