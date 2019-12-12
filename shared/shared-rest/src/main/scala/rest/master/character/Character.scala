package rest.master.character

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class Character @JsonCreator()(@JsonProperty("id") _id: Integer, @JsonProperty("accountId") _accountId: Integer) {
  @JsonGetter("id")
  def id: Integer = _id

  @JsonGetter("accountId")
  def accountId: Integer = _accountId
}
