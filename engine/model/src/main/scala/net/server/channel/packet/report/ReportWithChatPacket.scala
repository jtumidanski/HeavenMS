package net.server.channel.packet.report

class ReportWithChatPacket(private var _type: Int, private var _victim: String, private var _reason: Int, private var _description: String, private var _chatLog: String) extends BaseReportPacket(_type = _type, _victim = _victim, _reason = _reason, _description = _description) {
  def chatLog: String = _chatLog
}