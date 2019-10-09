package tools.packet.ui

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowOXQuiz(private var _questionSet: Int, private var _questionId: Int, private var _askQuestion: Boolean) extends PacketInput {
  def questionSet: Int = _questionSet

  def questionId: Int = _questionId

  def askQuestion: Boolean = _askQuestion

  override def opcode(): SendOpcode = SendOpcode.OX_QUIZ
}