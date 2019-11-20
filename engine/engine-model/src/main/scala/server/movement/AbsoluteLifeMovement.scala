package server.movement

import java.awt.Point

import tools.data.output.LittleEndianWriter

class AbsoluteLifeMovement(private var _type: Int, private var _position: Point, private var _duration: Int, private var _newState: Int, private var _pixelsPerSecond: Point, private var _fh: Int) extends AbstractLifeMovement(_type = _type, _position = _position, _duration = _duration, _newState = _newState) {
  def pixelsPerSecond: Point = _pixelsPerSecond

  def fh: Int = _fh

  override def serialize(lew: LittleEndianWriter): Unit = {
    lew.write(theType)
    lew.writeShort(position.x)
    lew.writeShort(position.y)
    lew.writeShort(pixelsPerSecond.x)
    lew.writeShort(pixelsPerSecond.y)
    lew.writeShort(fh)
    lew.write(newState)
    lew.writeShort(duration)
  }
}
