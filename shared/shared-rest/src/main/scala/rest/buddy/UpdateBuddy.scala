package rest.buddy

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class UpdateBuddy @JsonCreator()(@JsonProperty("pending") _pending: Integer, @JsonProperty("responseRequired") _responseRequired: java.lang.Boolean) {
  @JsonGetter("pending")
  def pending: Integer = _pending

  @JsonGetter("responseRequired")
  def responseRequired: java.lang.Boolean = _responseRequired
}
