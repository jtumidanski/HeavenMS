package npc
import tools.I18nMessage

import scripting.event.EventManager
import scripting.npc.NPCConversationManager
import server.events.MapleEvents
import server.events.RescueGaga

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9001105 {
   NPCConversationManager cm
   int status = -1
   int selected = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            if (cm.getPlayer().getMapId() == 922240200) {
               cm.sendOk(I18nMessage.from("9001105_A_SHAME"))
            }

            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            if (cm.getMapId() == 922240200) {
               cm.sendSimple(I18nMessage.from("9001105_DID_YOU_HAVE_SOMETHING_TO_SAY"))
               //#L1#I want to go to the Space Mine.#l
            } else if (cm.getMapId() >= 922240000 && cm.getMapId() <= 922240019) {
               cm.sendYesNo(I18nMessage.from("9001105_DO_NOT_WORRY"))
            } else if (cm.getMapId() >= 922240100 && cm.getMapId() <= 922240119) {
               String text = "You went through so much trouble to rescue Gaga, but it looks like we're back to square one. "
               RescueGaga rescueGaga = (RescueGaga) cm.getPlayer().getEvents().get("rescueGaga")
               if (rescueGaga.getCompleted() > 10) {
                  text += "Please don't give up until Gaga is rescued. To show you my appreciation for what you've accomplished thus far, I've given you a Spaceship. It's rather worn out, but it should still be operational. Check your #bSkill Window#k."
                  rescueGaga.giveSkill(cm.getPlayer())
               } else {
                  text += "Let's go back now."
               }

               cm.sendNext(text)
            }
         } else {
            if (cm.getPlayer().getMapId() == 922240200) {
               if (status == 1) {
                  if (selection == 0) {
                     selected = 1
                     cm.sendNext(I18nMessage.from("9001105_WELCOME"))
                  } else {
                     selected = 2
                     cm.sendYesNo(I18nMessage.from("9001105_SPACE_MINE"))
                  }
               } else if (status == 2) {
                  if (selected == 1) {
                     cm.sendYesNo(I18nMessage.from("9001105_SOMETHING_TERRIBLE"))
                  } else if (selected == 2) {
                     cm.sendOk(I18nMessage.from("9001105_NOT_CODED"))
                     cm.dispose()
                  }
               } else if (status == 3) {
                  EventManager em = cm.getEventManager("RescueGaga")
                  if (em == null) {
                     cm.sendOk(I18nMessage.from("9001105_UNAVAILABLE"))
                  } else if (!em.startInstance(cm.getPlayer())) {
                     cm.sendOk(I18nMessage.from("9001105_SOMEONE_IN_MAP"))
                  }

                  cm.dispose()
               }
            } else if (cm.getPlayer().getMapId() >= 922240000 && cm.getPlayer().getMapId() <= 922240019) {
               cm.warp(922240200, 0)
               cm.dispose()
            } else if (cm.getPlayer().getMapId() >= 922240100 && cm.getPlayer().getMapId() <= 922240119) {
               cm.warp(922240200, 0)
               cm.dispose()
            }
         }
      }
   }
}

NPC9001105 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9001105(cm: cm))
   }
   return (NPC9001105) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }