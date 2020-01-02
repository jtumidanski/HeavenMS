package net.server.coordinator.partysearch

import client.MapleJob

import scala.util.control._

class LeaderSearchMetadata(private var _minLevel: Int, private var _maxLevel: Int, private var _jobs: Int, private var _jobTable: java.util.Map[Integer, MapleJob]) {
  def minLevel: Int = _minLevel

  def maxLevel: Int = _maxLevel

  def searchedJobs: java.util.List[MapleJob] = _searchedJobs

  private var _reentryCount: Int = 0

  def reentryCount: Int = _reentryCount

  def incrementReentryCount(): Unit = _reentryCount += 1

  private val _searchedJobs: java.util.List[MapleJob] = decodeSearchedJobs(_jobs)

  def decodeSearchedJobs(_jobsSelected: Int): java.util.List[MapleJob] = {
    var jobsSelected = _jobsSelected
    val topByte: Int = ((Math.log(jobsSelected) / Math.log(2)) + 1e-5).asInstanceOf[Int]
    val loop = new Breaks

    loop.breakable {
      for (i <- 0 to topByte) {
        if (jobsSelected % 2 == 1) {
          val job = _jobTable.get(i)
          if (job != null) {
            _searchedJobs.add(job)
          }
          jobsSelected = jobsSelected >> 1
          if (jobsSelected == 0) {
            loop.break()
          }
        }
      }
    }
    _searchedJobs
  }
}
