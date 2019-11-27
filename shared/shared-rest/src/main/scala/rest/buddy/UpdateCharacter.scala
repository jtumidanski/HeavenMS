package rest.buddy

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class UpdateCharacter @JsonCreator()(@JsonProperty("capacity") _capacity: Integer) {
  @JsonGetter("capacity")
  def capacity: Integer = _capacity
}
