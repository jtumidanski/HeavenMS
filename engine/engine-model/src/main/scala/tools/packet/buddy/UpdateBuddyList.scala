package tools.packet.buddy

import client.BuddyListEntry
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class UpdateBuddyList(private var _buddies: java.util.Collection[BuddyListEntry]) extends PacketInput {
  def buddies: java.util.Collection[BuddyListEntry] = _buddies

  override def opcode(): SendOpcode = SendOpcode.BUDDYLIST
}