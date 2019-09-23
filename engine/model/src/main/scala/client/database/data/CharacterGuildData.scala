package client.database.data

class CharacterGuildData(private var _id: Int, private var _guildId: Int, private var _guildRank: Int,
                         private var _name: String, private var _allianceRank: Int, private var _level: Int,
                         private var _job: Int) {
  def id: Int = _id

  def guildId: Int = _guildId

  def guildRank: Int = _guildRank

  def name: String = _name

  def allianceRank: Int = _allianceRank

  def level: Int = _level

  def job: Int = _job
}
