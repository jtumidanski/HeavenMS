package rest.buddy

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class AddCharacter @JsonCreator()(@JsonProperty("characterId") _characterId: Integer, @JsonProperty("accountId") _accountId: Integer) {
  @JsonGetter("characterId")
  def characterId: Integer = _characterId

  @JsonGetter("accountId")
  def accountId: Integer = _accountId
}
