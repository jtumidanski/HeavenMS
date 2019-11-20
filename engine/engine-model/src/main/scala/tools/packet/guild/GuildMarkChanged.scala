package tools.packet.guild

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GuildMarkChanged(private var _characterId: Int, private var _logoBackground: Int,
                       private var _logoBackgroundColor: Int, private var _logo: Int, private var _logoColor: Int) extends PacketInput {
  def characterId: Int = _characterId

  def logoBackground: Int = _logoBackground

  def logoBackgroundColor: Int = _logoBackgroundColor

  def logo: Int = _logo

  def logoColor: Int = _logoColor

  override def opcode(): SendOpcode = SendOpcode.GUILD_MARK_CHANGED
}