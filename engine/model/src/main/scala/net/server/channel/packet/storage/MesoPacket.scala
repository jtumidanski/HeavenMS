package net.server.channel.packet.storage

class MesoPacket(private var _mode: Byte, private var _mesos: Int) extends BaseStoragePacket(_mode = _mode) {
  def mesos: Int = _mesos
}
