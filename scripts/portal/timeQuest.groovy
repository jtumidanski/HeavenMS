package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   int mapid = pi.getPlayer().getMapId()
   pi.playPortalSound()
   int map = ((mapid - 270010000) / 100).intValue()
   //pi.getPlayer().dropMessage(5, map + " " + pi.isQuestCompleted(3534));
   if (map < 5 && pi.isQuestCompleted(3500 + map)) {
      pi.warp(mapid + 10, "out00")
   } else if (map == 5 && pi.isQuestCompleted(3502 + map)) {
      pi.warp(270020000, "out00")
   } else if (map > 100 && map < 105 && pi.isQuestCompleted(3407 + map)) {
      pi.warp(mapid + 10, "out00")
   } else if (map == 105 && pi.isQuestCompleted(3514)) {
      pi.warp(270030000, "out00")
   } else if (map > 200 && map < 205 && pi.isQuestCompleted(3314 + map)) {
      pi.warp(mapid + 10, "out00")
   } else if (map == 205 && pi.isQuestCompleted(3519)) {
      pi.warp(270040000, "out00")
   } else if (map == 300 && (pi.haveItem(4032002) || pi.isQuestCompleted(3522))) {
      pi.warp(270040100, "out00")
   } else {
      if (map > 200) {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "As the time starts to flow oddly, you are transported back to a safe lane.")
         pi.warp(270030000, "in00")
      } else if (map > 100) {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "As the time starts to flow oddly, you are transported back to a safe lane.")
         pi.warp(270020000, "in00")
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "As the time starts to flow oddly, you are transported back to a safe lane.")
         pi.warp(270010000, "in00")
      }
   }
   return true
}