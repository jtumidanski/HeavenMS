package net.server

class RelativeMovementData(private var _stance: Byte) extends MovementData {
  def stance: Byte = _stance
}
