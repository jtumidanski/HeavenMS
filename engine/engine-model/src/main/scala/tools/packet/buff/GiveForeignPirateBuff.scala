package tools.packet.buff

import client.MapleBuffStat
import net.opcodes.SendOpcode
import tools.Pair
import tools.packet.PacketInput

class GiveForeignPirateBuff(private var _characterId: Int, private var _buffId: Int, private var _time: Int,
                            private var _statups: java.util.List[Pair[MapleBuffStat, java.lang.Integer]]) extends PacketInput {
  def characterId: Int = _characterId

  def buffId: Int = _buffId

  def time: Int = _time

  def statups: java.util.List[Pair[MapleBuffStat, java.lang.Integer]] = _statups

  override def opcode(): SendOpcode = SendOpcode.GIVE_FOREIGN_BUFF
}