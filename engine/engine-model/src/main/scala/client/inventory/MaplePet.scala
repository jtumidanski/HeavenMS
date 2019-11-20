package client.inventory

import java.awt.Point

import server.movement.{AbsoluteLifeMovement, LifeMovement, LifeMovementFragment}

class MaplePet(_id: Int, _position: Short, var uniqueId: Int) extends Item(_id, _position, 1) {
  var name: String = ""

  var closeness: Int = 0

  var level: Byte = 1

  var fullness: Int = 100

  var fh: Int = 0

  var pos: Point = new Point(0, 0)

  var stance: Int = 0

  var summoned: Boolean = false

  var petFlag: Int = 0

  def updatePosition(movement: java.util.List[LifeMovementFragment]): Unit = {
    movement.stream()
      .filter(_.isInstanceOf[LifeMovement])
      .forEach((move: LifeMovementFragment) => {
        if (move.isInstanceOf[AbsoluteLifeMovement]) {
          this.pos = move.position()
        }
        stance = move.asInstanceOf[LifeMovement].newState()
      })
  }
}
