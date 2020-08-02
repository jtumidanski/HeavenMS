package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2010005 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int pay = 2000
   int ticket = 4031134
   String msg
   int check
   boolean access

   def start() {
      cm.sendSimple(I18nMessage.from("2010005_HAVE_YOU_HEARD").with(cm.getPlayer().getMapId(), pay, ticket, pay, ticket, ticket))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || (mode == 0 && status == 0)) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 1) {
            cm.sendNext(I18nMessage.from("2010005_MUST_HAVE_SOME_BUSINESS"))
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 1) {
            if (selection == 0 || selection == 1) {
               check = selection
               if (selection == 0) {
                  msg = "You want to pay #b" + pay + " mesos#k and leave for #m110000000#?"
               } else if (selection == 1) {
                  msg = "So you have #b#t" + ticket + "##k? You can always head over to #m110000000# with that."
               }
               cm.sendYesNo(msg + " Okay!! Please beware that you may be running into some monsters around there though, so make sure not to get caught off-guard. Okay, would you like to head over to #m110000000# right now?")
            } else if (selection == 2) {
               cm.sendNext(I18nMessage.from("2010005_TICKET").with(ticket, ticket))
               status = 3
            }
         } else if (status == 2) {
            if (check == 0) {
               if (cm.getMeso() < pay) {
                  cm.sendOk(I18nMessage.from("2010005_NOT_ENOUGH_MESO"))
                  cm.dispose()
               } else {
                  cm.gainMeso(-pay)
                  access = true
               }
            } else if (check == 1) {
               if (!cm.haveItem(ticket)) {
                  cm.sendOk(I18nMessage.from("2010005_MISSING_TICKET").with(ticket))
                  cm.dispose()
               } else {
                  access = true
               }
            }
            if (access) {
               cm.getPlayer().saveLocation("FLORINA")
               cm.warp(110000000, "st00")
               cm.dispose()
            }
         } else if (status == 3) {
            cm.sendNext(I18nMessage.from("2010005_TICKET").with(ticket, ticket))
         } else if (status == 4) {
            cm.sendPrev(I18nMessage.from("2010005_HOPEFULLY_SOMEONE_PICKED_IT_UP"))
         } else if (status == 5) {
            cm.dispose()
         }

      }
   }
}

NPC2010005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2010005(cm: cm))
   }
   return (NPC2010005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }