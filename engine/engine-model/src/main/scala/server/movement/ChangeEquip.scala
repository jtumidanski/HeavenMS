package server.movement

import java.awt.Point

import tools.data.output.LittleEndianWriter

class ChangeEquip(private var _wui: Int) extends LifeMovementFragment {
  override def serialize(lew: LittleEndianWriter): Unit = {
    lew.write(10)
    lew.write(_wui)
  }

  override def position(): Point = {
    new Point(0, 0)
  }
}
