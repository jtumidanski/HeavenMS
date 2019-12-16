package rest.buddy

object AddBuddyResult extends Enumeration {
  type AddBuddyResult = Value
  val OK, ALREADY_REQUESTED, FULL, TARGET_CHARACTER_DOES_NOT_EXIST, BUDDY_ALREADY_REQUESTED, BUDDY_FULL = Value
}
