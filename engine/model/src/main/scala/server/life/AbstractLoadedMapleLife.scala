package server.life

import server.maps.AbstractAnimatedMapleMapObject

abstract class AbstractLoadedMapleLife(private var _id: Int) extends AbstractAnimatedMapleMapObject {
  var f: Int = _

  var hide: Boolean = _

  var fh: Int = _id

  private var _startFh: Int = _

  var cy: Int = _

  var rx0: Int = _

  var rx1: Int = _

  def this(life: AbstractLoadedMapleLife) = {
    this(life.id)
    this.f = life.f
    this.hide = life.hide
    this.fh = life.fh
    this._startFh = life.startFh
    this.cy = life.cy
    this.rx0 = life.rx0
    this.rx1 = life.rx1
  }

  def id: Int = _id

  def startFh: Int = _startFh
}
