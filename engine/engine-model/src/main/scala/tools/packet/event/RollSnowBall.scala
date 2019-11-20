package tools.packet.event

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class RollSnowBall(private var _enterMap: Boolean, private var _state: Int, private var _firstSnowmanHP: Int,
                   private var _firstSnowBallPosition: Int, private var _secondSnowmanHP: Int,
                   private var _secondSnowBallPosition: Int) extends PacketInput {
  def enterMap: Boolean = _enterMap

  def state: Int = _state

  def firstSnowmanHP: Int = _firstSnowmanHP

  def firstSnowBallPosition: Int = _firstSnowBallPosition

  def secondSnowmanHP: Int = _secondSnowmanHP

  def secondSnowBallPosition: Int = _secondSnowBallPosition

  def this(_enterMap: Boolean, _state: Int) = {
    this(_enterMap, _state, 0, 0, 0, 0)
  }

  override def opcode(): SendOpcode = SendOpcode.SNOWBALL_STATE
}