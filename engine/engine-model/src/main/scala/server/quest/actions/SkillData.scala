package server.quest.actions

import client.MapleJob

class SkillData(private var _id: Int, private var _level: Int, private var _masterLevel: Int, private var _jobs: java.util.List[Integer]) {
  def id: Int = _id

  def level: Int = _level

  def masterLevel: Int = _masterLevel

  def jobsContains(job: MapleJob): Boolean = _jobs.contains(job.getId)
}
