package rest.cashshop

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class Gift @JsonCreator()(@JsonProperty("from") _from: String, @JsonProperty("message") _message: String, @JsonProperty("sn") _sn: Integer, @JsonProperty("ringId") _ringId: Integer) {
  @JsonGetter("from")
  def from: String = _from

  @JsonGetter("message")
  def message: String = _message

  @JsonGetter("sn")
  def sn: Integer = _sn

  @JsonGetter("ringId")
  def ringId: Integer = _ringId

}
