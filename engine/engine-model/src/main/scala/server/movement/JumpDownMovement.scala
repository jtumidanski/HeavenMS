package server.movement

import java.awt.Point

import tools.data.output.LittleEndianWriter

class JumpDownMovement(private var _type: Int, private var _position: Point, private var _duration: Int, private var _newState: Int, private var _pixelsPerSecond: Point, private var _fh: Int, private var _originFh: Int) extends AbstractLifeMovement(_type = _type, _position = _position, _duration = _duration, _newState = _newState) {
  def pixelsPerSecond: Point = _pixelsPerSecond

  def fh: Int = _fh

  def originFh: Int = _originFh

  override def serialize(lew: LittleEndianWriter): Unit = {
    lew.write(theType)
    lew.writeShort(position.x)
    lew.writeShort(position.y)
    lew.writeShort(pixelsPerSecond.x)
    lew.writeShort(pixelsPerSecond.y)
    lew.writeShort(fh)
    lew.writeShort(originFh)
    lew.write(newState)
    lew.writeShort(duration)
  }
}
