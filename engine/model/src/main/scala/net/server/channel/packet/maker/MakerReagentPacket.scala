package net.server.channel.packet.maker

class MakerReagentPacket(private var _type: Int, private var _toCreate: Int, private var _isStimulant: Boolean, private var _reagentCount: Int, private var _reagentIds: Array[Int]) extends BaseMakerActionPacket(_type = _type, _toCreate = _toCreate) {
  def isStimulant: Boolean = _isStimulant

  def reagentCount: Int = _reagentCount

  def reagentIds: Array[Int] = _reagentIds
}
