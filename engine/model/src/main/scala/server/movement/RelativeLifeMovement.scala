package server.movement

import java.awt.Point

import tools.data.output.LittleEndianWriter

class RelativeLifeMovement(private var _type: Int, private var _position: Point, private var _duration: Int, private var _newState: Int) extends AbstractLifeMovement(_type = _type, _position = _position, _duration = _duration, _newState = _newState) {
  override def serialize(lew: LittleEndianWriter): Unit = {
    lew.write(theType)
    lew.writeShort(position.x)
    lew.writeShort(position.y)
    lew.write(newState)
    lew.writeShort(duration)
  }
}
