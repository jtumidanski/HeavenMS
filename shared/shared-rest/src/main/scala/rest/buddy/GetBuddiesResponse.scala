package rest.buddy

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class GetBuddiesResponse @JsonCreator()(@JsonProperty("buddies") _buddies: java.util.List[Buddy], @JsonProperty("pending") _pending: java.util.List[Buddy]) {
  @JsonGetter("buddies")
  def buddies: java.util.List[Buddy] = _buddies

  @JsonGetter("pending")
  def pending: java.util.List[Buddy] = _pending
}
