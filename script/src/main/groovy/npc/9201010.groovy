package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201010 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || mode == 0) {
         cm.dispose()
         return
      } else if (mode == 1) {
         status++
      } else {
         status--
      }

      EventInstanceManager eim = cm.getEventInstance()
      if (eim == null) {
         cm.warp(680000000, 0)
         cm.dispose()
         return
      }

      boolean isMarrying = (cm.getPlayer().getId() == eim.getIntProperty("groomId") || cm.getPlayer().getId() == eim.getIntProperty("brideId"))

      switch (status) {
         case 0:
            if (cm.getMapId() == 680000300) {
               cm.sendYesNo("Are you sure you want to #rquit the stage#k and head back to #bAmoria#k? You will be #rskipping the bonus stages#k that way.")
            } else {
               boolean hasEngagement = false
               for (int x = 4031357; x <= 4031364; x++) {
                  if (cm.haveItem(x, 1)) {
                     hasEngagement = true
                     break
                  }
               }

               if (cm.haveItem(4000313) && isMarrying) {
                  if (eim.getIntProperty("weddingStage") == 3) {
                     cm.sendOk("You guys totally rocked the stage!!! Go go, talk to #b#p9201007##k to start the after party.")
                     cm.dispose()
                  } else if (hasEngagement) {
                     if (!cm.createMarriageWishList()) {
                        cm.sendOk("You have already sent your wish list...")
                     }
                     cm.dispose()
                  } else {
                     cm.sendOk("Oh, hey, where are the credentials for the this so-lauded party? Oh man, we can't continue at this rate now... Sorry, the party is over.")
                  }
               } else {
                  if (eim.getIntProperty("weddingStage") == 3) {
                     if (!isMarrying) {
                        cm.sendYesNo("You guys didn't miss them right? Our superstars #rworked so good together#k, and soon #bthey will start the after party#k. Are you really going to #rdrop out of the show#k and return to #bAmoria#k?")
                     } else {
                        cm.sendOk("You guys totally rocked the stage!!! Go go, talk to #b#p9201007##k to start the after party.")
                        cm.dispose()
                     }
                  } else {
                     cm.sendYesNo("Are you sure you want to #rquit the stage#k and head to #bAmoria#k? You will be #rskipping the bonus stages#k, fam.")
                  }
               }
            }

            break
         case 1:
            cm.warp(680000000, 0)
            cm.dispose()
            break
      }
   }
}

NPC9201010 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201010(cm: cm))
   }
   return (NPC9201010) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }