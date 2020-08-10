package rest.buddy

import AddBuddyResult._
import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class AddBuddyResponse @JsonCreator()(@JsonProperty("errorCode") _errorCode: AddBuddyResult) {
  @JsonGetter("errorCode")
  def errorCode: AddBuddyResult = _errorCode
}