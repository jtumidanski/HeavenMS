package rest.buddy

class AddBuddyResponse(private var _errorCode: AddBuddyResult) {
  def getErrorCode: AddBuddyResult = _errorCode
}
