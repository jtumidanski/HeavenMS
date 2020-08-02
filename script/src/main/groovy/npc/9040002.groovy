package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9040002 {
   NPCConversationManager cm
   int status = -1
   int selectedOption = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (mode == 1 && status == 3) {
            status = 0
         }
         if (status == 0) {
            String prompt = "\r\n#b#L0# What's Sharenian?#l\r\n#b#L1# #t4001024#? What's that?#l\r\n#b#L2# Guild Quest?#l\r\n#b#L3# No, I'm fine now.#l"
            if (selectedOption == -1) {
               prompt = "We, the Union of Guilds, have been trying to decipher 'Emerald Tablet,' a treasured old relic, for a long time. As a result, we have found out that Sharenian, the mysterious country from the past, lay asleep here. We also found out that clues of #t4001024#, a legendary, mythical jewelry, may be here at the remains of Sharenian. This is why the Union of Guilds have opened Guild Quest to ultimately find #t4001024#." + prompt
            } else {
               prompt = "Do you have any other questions?" + prompt
            }
            cm.sendSimple(prompt)
         } else if (status == 1) {
            selectedOption = selection
            if (selectedOption == 0) {
               cm.sendNext(I18nMessage.from("9040002_LITERATE_CIVILIZATION"))
            } else if (selectedOption == 1) {
               cm.sendNext(I18nMessage.from("9040002_LEGENDARY_JEWEL"))
               status = -1
            } else if (selectedOption == 2) {
               cm.sendNext(I18nMessage.from("9040002_NONE_CAME_BACK"))
            } else if (selectedOption == 3) {
               cm.sendOk(I18nMessage.from("9040002_FEEL_FREE_TO_TALK_TO_ME"))
               cm.dispose()
            } else {
               cm.dispose()
            }
         } else if (status == 2) { //should only be available for options 0 and 2
            if (selectedOption == 0) {
               cm.sendNextPrev(I18nMessage.from("9040002_LAST_KING"))
            } else if (selectedOption == 2) {
               cm.sendNextPrev(I18nMessage.from("9040002_ULTIMATE_GOAL"))
            } else {
               cm.dispose()
            }
         }
      }
   }
}

NPC9040002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9040002(cm: cm))
   }
   return (NPC9040002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }