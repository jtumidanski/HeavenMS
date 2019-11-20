package tools.packet.npctalk

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetNPCTalkStyle(private var _npcId: Int, private var _talk: String, private var _styles: Array[Int]) extends PacketInput {
  def npcId: Int = _npcId

  def talk: String = _talk

  def styles: Array[Int] = _styles

  override def opcode(): SendOpcode = SendOpcode.NPC_TALK
}