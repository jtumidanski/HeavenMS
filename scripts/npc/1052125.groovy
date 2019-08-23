package npc

import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		June
	Map(s): 		Kerning Square : 7th Floor
	Description: 	Entrance to Spirit of Rock
*/


class NPC1052125 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendSimple("Hold up! Access to this area is limited due to remodeling. I can only allow people who meet certain conditions to enter here.#b\n\r\n#L0#I'm helping #eBlake#n right now.#l\r\n#L1#I'm a #rVIP#b at this shopping Center!#l")
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0 && type != 4) {
            status -= 2
         } else {
            cm.dispose()
            return
         }
      }
      if (status == 0) {
         if (selection == 0) {
            if (cm.isQuestStarted(2286) || cm.isQuestStarted(2287) || cm.isQuestStarted(2288)) {
               EventManager em = cm.getEventManager("RockSpirit")
               if (!em.startInstance(cm.getPlayer())) {
                  cm.sendOk("Uh... It looks like the rooms ahead are a bit crowded right now. Please wait around here for a bit, ok?")
               }
               cm.dispose()
               return
            } else {
               cm.sendOk("I did not hear anything from Blake that you are assisting him.")
            }
         } else {
            if (cm.isQuestCompleted(2290)) {
               if (cm.getPlayer().getLevel() > 50) {
                  cm.sendOk("The VIP area is available only for players #rlevel 50 or below#k.")
               } else {
                  cm.sendOk("The VIP area only gets available after handing over #r#t4032521#s#k from the #b'Admission to the VIP Zone'#k quest.")
               }
            } else {
               cm.sendOk("#rVIP#k? Yeah that is funny #rMr. VIP#k, now get lost before I call security.")
            }
         }
         cm.dispose()
      }
   }
}

NPC1052125 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1052125(cm: cm))
   }
   return (NPC1052125) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }