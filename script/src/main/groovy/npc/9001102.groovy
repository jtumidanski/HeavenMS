package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9001102 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.getPlayer().getMapId() == 100000000) {
         cm.sendNext("There! Did you see that? You didn't? A UFO just passed... there!! Look, someone is getting dragged into the UFO... arrrrrrgh, it's Gaga! #rGaga just got kidnapped by a UFO!#k")
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode > 0) {
         status++
         if (cm.getPlayer().getMapId() == 100000000) {
            if (status == 1) {
               if (cm.getPlayer().getLevel() >= 12) {
                  cm.sendYesNo("What do we do now? It's just a rumor yet, but... I've heard that scary things happen to you if you get kidnapped by aliens... may be that's what happening to Gaga right now! Please, please rescue Gaga! \r\n #bGaga may be a bit undetermined and clueless, but#k he has a really good heart. I can't let something terrible happen to him. Right! Grandpa from the moon might know how to rescue him! I will send you to the moon, so please go meet Grandpa and rescue Gaga!!!")
               } else {
                  cm.sendOk("Oh! It seems you don't reach the level requirements to save Gaga. Please come back when you are level 12 or higher.")
               }

            } else if (status == 2) {
               cm.sendNext("Thank you so much. Please rescue Gaga! Grandpa from the moon will help you.")
            } else if (status == 3) {
               cm.warp(922240200, 0)
               cm.dispose()
            }
         }
      } else if (mode < 1) {
         cm.dispose()
      }
   }
}

NPC9001102 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9001102(cm: cm))
   }
   return (NPC9001102) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }