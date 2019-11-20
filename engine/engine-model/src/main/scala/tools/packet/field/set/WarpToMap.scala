package tools.packet.field.set

import java.awt.Point

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class WarpToMap(private var _channelId: Int, private var _mapId: Int, private var _spawnPoint: Int, private var _characterHp: Int, private var _spawnPosition: Option[Point]) extends PacketInput {
  def channelId: Int = _channelId

  def mapId: Int = _mapId

  def spawnPoint: Int = _spawnPoint

  def characterHp: Int = _characterHp

  def spawnPosition: Option[Point] = _spawnPosition

  def this(_channelId: Int, _mapId: Int, _spawnPoint: Int, _characterHp: Int) = {
    this(_channelId, _mapId, _spawnPoint, _characterHp, Option.empty)
  }

  override def opcode(): SendOpcode = SendOpcode.SET_FIELD
}