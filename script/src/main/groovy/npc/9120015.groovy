package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9120015 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendSimple("What do you want from me?\r #L0##bGather up some information on the hideout.#l\r\n#L1#Take me to the hideout#l\r\n#L2#Nothing#l#k")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         status++
         if (status == 1) {
            if (selection == 0) {
               cm.sendNext("I can take you to the hideout, but the place is infested with thugs looking for trouble. You'll need to be both incredibly strong and brave to enter the premise. At the hideaway, you'll find the Boss that controls all the other bosses around this area. It's easy to get to the hideout, but the room on the top floor of the place can only be entered ONCE a day. The Boss's Room is not a place to mess around. I suggest you don't stay there for too long; you'll need to swiftly take care of the business once inside. The boss himself is a difficult foe, but you'll run into some incredibly powerful enemies on you way to meeting the boss! It ain't going to be easy.")
               cm.dispose()
            } else if (selection == 1) {
               cm.sendNext("Oh, the brave one. I've been awaiting your arrival. If these\r\nthugs are left unchecked, there's no telling what going to\r\nhappen in this neighborhood. Before that happens, I hope\r\nyou take care of all them and beat the boss, who resides\r\non the 5th floor. You'll need to be on alert at all times, since\r\nthe boss is too tough for even wisemen to handle.\r\nLooking at your eyes, however, I can see that eye of the\r\ntiger, the eyes that tell me you can do this. Let's go!")
            } else {
               cm.sendOk("I'm a busy person! Leave me alone if that's all you need!")
               cm.dispose()
            }
         } else {
            cm.warp(801040000)
            cm.dispose()
         }
      }
   }
}

NPC9120015 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9120015(cm: cm))
   }
   return (NPC9120015) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }