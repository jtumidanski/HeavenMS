package tools.packet.npctalk

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetNPCTalkNum(private var _npcId: Int, private var _talk: String, private var _def: Int, private var _min: Int, private var _max: Int) extends PacketInput {
  def npcId: Int = _npcId

  def talk: String = _talk

  def theDef: Int = _def

  def min: Int = _min

  def max: Int = _max

  override def opcode(): SendOpcode = SendOpcode.NPC_TALK
}