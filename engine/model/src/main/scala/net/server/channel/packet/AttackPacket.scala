package net.server.channel.packet

import java.awt.Point
import scala.jdk.CollectionConverters._

import net.server.MaplePacket

class AttackPacket(var numAttacked: Int, var numDamage: Int, var numAttackedAndDamage: Int, var skill: Int,
                   var skillLevel: Int, var stance: Int, var direction: Int, var rangedDirection: Int, var charge: Int,
                   var display: Int, var ranged: Boolean, var magic: Boolean, var speed: Int, var allDamage: Map[Integer, java.util.List[Integer]], var position: Point) extends MaplePacket {
  def this() {
    this(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, false, false, -1, Map[Integer, java.util.List[Integer]](), null)
  }

  def clearAttacks(): Unit = {
    allDamage = Map[Integer, java.util.List[Integer]]()
  }

  def addDamage(key: Integer, damage: java.util.List[Integer]): Unit = {
    allDamage += (key -> damage)
  }

  def getDamage(): java.util.Map[Integer, java.util.List[Integer]] = {
    allDamage.asJava;
  }
}
