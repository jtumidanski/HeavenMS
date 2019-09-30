package tools.packet.npctalk

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class AskSpeedQuiz(private var _speakerTypeId: Int, private var _speakerTemplateId: Int, private var _resCode: Int,
                   private var _type: Int, private var _answer: Int, private var _correct: Int,
                   private var _remain: Int, private var _remainInitialQuiz: Int) extends PacketInput {
  def speakerTypeId: Int = _speakerTypeId

  def speakerTemplateId: Int = _speakerTemplateId

  def resCode: Int = _resCode

  def theType: Int = _type

  def answer: Int = _answer

  def correct: Int = _correct

  def remain: Int = _remain

  def remainInitialQuiz: Int = _remainInitialQuiz

  override def opcode(): SendOpcode = SendOpcode.NPC_TALK
}