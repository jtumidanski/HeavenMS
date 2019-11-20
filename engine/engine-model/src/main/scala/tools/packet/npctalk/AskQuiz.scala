package tools.packet.npctalk

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class AskQuiz( private var _speakerTypeId: Int,  private var _speakerTemplateId: Int,  private var _resCode: Int,
               private var _title: String,  private var _problemText: String,  private var _hintText: String,
               private var _minInput: Int,  private var _maxInput: Int, private var _remainInitialQuiz: Int) extends PacketInput {
     def speakerTypeId: Int = _speakerTypeId
     def speakerTemplateId: Int = _speakerTemplateId
     def resCode: Int = _resCode
     def title: String = _title
     def problemText: String = _problemText
     def hintText: String = _hintText
     def minInput: Int = _minInput
     def maxInput: Int = _maxInput
     def remainInitialQuiz: Int = _remainInitialQuiz

  override def opcode(): SendOpcode = SendOpcode.NPC_TALK
}