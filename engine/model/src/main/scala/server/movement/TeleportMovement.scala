package server.movement

import java.awt.Point

import tools.data.output.LittleEndianWriter

class TeleportMovement(private var _type: Int, private var _position: Point, private var _newState: Int, private var _pixelsPerSecond: Point) extends AbsoluteLifeMovement(_type = _type, _position = _position, _duration = 0, _newState = _newState, _pixelsPerSecond = _pixelsPerSecond, _fh = 0) {
  override def serialize(lew: LittleEndianWriter): Unit = {
    lew.write(theType)
    lew.writeShort(position.x)
    lew.writeShort(position.y)
    lew.writeShort(pixelsPerSecond.x)
    lew.writeShort(pixelsPerSecond.y)
    lew.write(newState)
  }
}
