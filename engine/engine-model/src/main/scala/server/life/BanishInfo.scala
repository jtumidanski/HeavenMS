package server.life

class BanishInfo(private var _msg: String, private var _map: Int, private var _portal: String) {
  def msg: String = _msg

  def map: Int = _map

  def portal: String = _portal
}
