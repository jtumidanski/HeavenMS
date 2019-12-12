package rest.buddy

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class AddBuddy @JsonCreator()(@JsonProperty("addId") _addId: Integer, @JsonProperty("addName") _addName: String,
                              @JsonProperty("group") _group: String) {
  @JsonGetter("addId")
  def addId(): Integer = _addId

  @JsonGetter("addName")
  def addName(): String = _addName

  @JsonGetter("group")
  def group: String = _group
}
