package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Jeff
	Map(s): 		El Nath : Ice Valley II
	Description: 	
*/


class NPC2030000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.haveItem(4031450, 1)) {
         cm.warp(921100100, 1)
         cm.dispose()
         return
      }

      cm.sendNext("Hey, you look like you want to go farther and deeper past this place. Over there, though, you'll find yourself surrounded by aggressive, dangerous monsters, so even if you feel that you're ready to go, please be careful. Long ago, a few brave men from our town went in wanting to eliminate anyone threatening the town, but never came back out...")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (status == 1 && mode == 0 && cm.getLevel() > 49) {
            cm.sendNext("Even if your level's high it's hard to actually go in there, but if you ever change your mind, please find me. After all, my job is to protect this place.")
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 1) {
            if (cm.getLevel() > 49) {
               cm.sendYesNo("If you are thinking of going in, I suggest you change your mind. But if you really want to go in... I'm only letting in the ones that are strong enough to stay alive in there. I do not wish to see anyone else die. Let's see... Hmmm...! You look pretty strong. All right, do you want to go in?")
            } else {
               cm.sendPrev("If you are thinking of going in, I suggest you change your mind. But if you really want to go in... I'm only letting in the ones that are strong enough to stay alive in there. I do not wish to see anyone else die. Let's see... Hmmm... You haven't reached Level 50 yet. I can't let you in, then, so forget it.")
            }
         } else if (status == 2) {
            if (cm.getLevel() >= 50) {
               cm.warp(211040300, 5)
            }
            cm.dispose()
         }
      }
   }
}

NPC2030000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2030000(cm: cm))
   }
   return (NPC2030000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }