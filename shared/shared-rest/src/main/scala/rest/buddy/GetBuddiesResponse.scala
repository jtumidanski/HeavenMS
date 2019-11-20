package rest.buddy

class GetBuddiesResponse(private var _buddies: java.util.List[Buddy], private var _pending: java.util.List[Buddy]) {
  def getBuddies: java.util.List[Buddy] = _buddies

  def getPending: java.util.List[Buddy] = _pending
}
