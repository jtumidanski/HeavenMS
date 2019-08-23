package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201110 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      switch (cm.getPlayer().getMapId()) {
         case 610030500:
            cm.sendOk("As every Thief knows, the best attack is the one you never see coming. So, to best illustrate this, you'll be in a chamber with platforms and ledges that you can only get to with Haste, as well as All-Seeing Eyes that your dagger or claw must close--permanently. After all the All-Seeing Eyes have been eliminated, get ti the Thief Statue and lay claim to the Primal Claw! Good luck!")
            break
         case 610030000:
            cm.sendOk("Once known as the 'Prince of Shadows', Grandmaster Ryo possessed supreme speed and power with short-ranged daggers and longer chain-like Claw. A part-time member of the Bosshunters, he was reowned for unparalleled ability to blend into the very night itself. His legend grew during a battle with Crimson Balrog, where he moved so swiftly that Balrog's attacks only caught air. Ryo also performed occasional 'retrievals' for those less fortunate than himself.")
            break
         case 610030530:
            if (cm.isAllReactorState(6108004, 1)) {
               EventInstanceManager eim = cm.getEventInstance()
               int stgStatus = eim.getIntProperty("glpq5_room")
               int jobNiche = cm.getPlayer().getJob().getJobNiche()

               if ((stgStatus >> jobNiche) % 2 == 0) {
                  if (cm.canHold(4001256, 1)) {
                     cm.gainItem(4001256, (short) 1)
                     cm.sendOk("Good job.")

                     stgStatus += (1 << jobNiche)
                     eim.setIntProperty("glpq5_room", stgStatus)
                  } else {
                     cm.sendOk("Make room on your ETC inventory first.")
                  }
               } else {
                  cm.sendOk("The weapon inside this room has already been retrieved.")
               }
            } else {
               cm.sendOk("Go now, destroy all of the watchful eyes with your mobility skills, fellow Thief. Report back to me when you are done.")
            }
            break
      }
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC9201110 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201110(cm: cm))
   }
   return (NPC9201110) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }