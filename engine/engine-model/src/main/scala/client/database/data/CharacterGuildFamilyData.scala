package client.database.data

class CharacterGuildFamilyData(private var _world: Int, private var _guildId: Int, private var _guildRank: Int,
                               private var _familyId: Int) {
  def world: Int = _world

  def guildId: Int = _guildId

  def guildRank: Int = _guildRank

  def familyId: Int = _familyId
}
