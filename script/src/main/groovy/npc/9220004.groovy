package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9220004 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (status == 0 && mode == 0) {
            cm.sendOk(I18nMessage.from("9220004_WHEN_YOU_WANT_TO"))
            cm.dispose()
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            cm.sendSimple(I18nMessage.from("9220004_RAID_QUEST_INFO"))
         } else if (status == 1) {
            if (selection == 0) {
               if (cm.getMap().getMonsters().size() > 1) {  //reactor as a monster? wtf
                  cm.sendOk(I18nMessage.from("9220004_ELIMINATE_ALL"))
                  cm.dispose()
                  return
               }

               cm.getMap().spawnMonsterOnGroundBelow(9500317, 1700, 80)
            } else if (selection == 1) {
               if (cm.getMap().getMonsters().size() > 6) {  //reactor as a monster? wtf
                  cm.sendOk(I18nMessage.from("9220004_TOO_CROWDED"))
                  cm.dispose()
                  return
               }

               cm.getMap().spawnMonsterOnGroundBelow(9500320, 1700, 80)
            } else {
               cm.sendOk(I18nMessage.from("9220004_FINE_THEN"))
            }

            cm.dispose()
         }
      }
   }
}

NPC9220004 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9220004(cm: cm))
   }
   return (NPC9220004) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }