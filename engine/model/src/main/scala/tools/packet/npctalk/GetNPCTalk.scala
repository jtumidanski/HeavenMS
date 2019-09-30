package tools.packet.npctalk

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetNPCTalk(private var _npcId: Int, private var _messageType: Byte, private var _talk: String,
                 private var _endBytes: String, private var _speaker: Byte) extends PacketInput {
  def npcId: Int = _npcId

  def messageType: Byte = _messageType

  def talk: String = _talk

  def endBytes: String = _endBytes

  def speaker: Byte = _speaker

  override def opcode(): SendOpcode = SendOpcode.NPC_TALK
}