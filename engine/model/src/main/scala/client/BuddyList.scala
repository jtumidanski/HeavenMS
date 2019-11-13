package client

import scala.collection.mutable
import scala.jdk.CollectionConverters._

class BuddyList(var capacity: Int) {
  private val buddies: mutable.Map[Int, BuddyListEntry] = new mutable.LinkedHashMap[Int, BuddyListEntry]

  private val pendingRequests: mutable.ArrayDeque[CharacterNameAndId] = new mutable.ArrayDeque[CharacterNameAndId]()

  def contains(characterId: Int): Boolean = {
    var result: Boolean = false
    buddies.synchronized {
      result = buddies.contains(characterId)
    }
    result
  }

  def containsVisible(characterId: Int): Boolean = {
    var buddyListEntry: Option[BuddyListEntry] = Option.empty
    buddies.synchronized {
      buddyListEntry = buddies.get(characterId)
    }
    if (buddyListEntry.isEmpty) {
      return false
    }
    buddyListEntry.get.visible
  }

  def get(characterId: Int): BuddyListEntry = {
    var buddyListEntry: Option[BuddyListEntry] = Option.empty
    buddies.synchronized {
      buddyListEntry = buddies.get(characterId)
    }
    buddyListEntry.orNull
  }

  def get(characterName: String): BuddyListEntry = {
    var buddyListEntry: Option[BuddyListEntry] = Option.empty
    buddies.synchronized {
      buddyListEntry = buddies.values.to(LazyList)
        .find(_.name.toLowerCase.eq(characterName.toLowerCase()))
    }

    buddyListEntry.orNull
  }

  def put(entry: BuddyListEntry): Unit = {
    buddies.synchronized {
      buddies.put(entry.characterId, entry)
    }
  }

  def remove(characterId: Int): Unit = {
    buddies.synchronized {
      buddies.remove(characterId)
    }
  }

  def getBuddies: java.util.Collection[BuddyListEntry] = {
    var result: java.util.Collection[BuddyListEntry] = null
    buddies.synchronized {
      result = buddies.values.asJavaCollection
    }
    result
  }

  def isFull: Boolean = {
    var result: Boolean = false
    buddies.synchronized {
      result = buddies.size >= capacity
    }
    result
  }

  def getBuddyIds: Array[Int] = {
    var result: Array[Int] = null
    buddies.synchronized {
      result = new Array[Int](buddies.size)
      result = buddies.values.to(LazyList).map(_.characterId).toArray
    }
    result
  }

  def addRequest(buddy: CharacterNameAndId): Unit = {
    pendingRequests.addOne(buddy)
  }

  def pollPendingRequest: Option[CharacterNameAndId] = {
    if (pendingRequests.isEmpty) {
      Option.empty
    } else {
      Option.apply(pendingRequests.removeLast(true))
    }
  }

  def hasPendingRequest: Boolean = {
    pendingRequests.nonEmpty
  }
}
