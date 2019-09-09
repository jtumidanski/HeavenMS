package npc


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
            cm.sendOk("Talk to me again when you want to.")
            cm.dispose()
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            cm.sendSimple("#b<Raid Quest: Happyville>#k\r\nA raid is nothing but many people joining up in an attempt to defeat extremely powerful creatures. Here is no different. Everyone can take part in defeating the spawned creature. What will you do?\r\n#b\r\n#L0#Spawn Kid Snowman.\r\n#L1#Spawn Lost Rudolph.\r\n#L2#Nothing, just chilling.#k")
         } else if (status == 1) {
            if (selection == 0) {
               if (cm.getMap().getMonsters().size() > 1) {  //reactor as a monster? wtf
                  cm.sendOk("Eliminate all mobs in the area to call Kid Snowman.")
                  cm.dispose()
                  return
               }

               cm.getMap().spawnMonsterOnGroundBelow(9500317, 1700, 80)
            } else if (selection == 1) {
               if (cm.getMap().getMonsters().size() > 6) {  //reactor as a monster? wtf
                  cm.sendOk("The place is too crowded right now. Eliminate some mobs before trying again.")
                  cm.dispose()
                  return
               }

               cm.getMap().spawnMonsterOnGroundBelow(9500320, 1700, 80)
            } else {
               cm.sendOk("Fine then.")
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