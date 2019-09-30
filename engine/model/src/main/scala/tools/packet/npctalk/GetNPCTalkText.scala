package tools.packet.npctalk

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetNPCTalkText(private var _npcId: Int, private var _talk: String, private var _def: String) extends PacketInput {
  def npcId: Int = _npcId

  def talk: String = _talk

  def theDef: String = _def

  override def opcode(): SendOpcode = SendOpcode.NPC_TALK
}