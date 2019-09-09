package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2094001 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

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

         if (cm.getMapId() == 925100500) {
            if (status == 0) {
               if (cm.isEventLeader()) {
                  cm.sendOk("I have been saved thanks to your efforts! Thank you, guys!")
               } else {
                  cm.sendOk("I have been saved thanks to your efforts! Thank you, guys! Let your party leader talk to me first before I give you your rewards...")
                  cm.dispose()
               }
            } else {
               cm.getEventInstance().clearPQ()
               cm.dispose()
            }
         } else {
            if (status == 0) {
               cm.sendSimple("Thank you for saving me! How can I help you?\r\n#b#L0#Get me out of here.\r\n#L1#Give me Pirate Hat.")
            } else if (status == 1) {
               if (selection == 0) {
                  if (!cm.canHold(4001158, 1)) {
                     cm.sendOk("Please make room in ETC.")
                     cm.dispose()
                     return
                  }
                  cm.gainItem(4001158, (short) 1)
                  cm.warp(251010404, 0)
               } else {
                  if (cm.haveItem(1003267, 1)) {
                     cm.sendOk("You have the best hat.")
                  } else if (cm.haveItem(1002573, 1)) {
                     if (cm.haveItem(4001158, 20)) {
                        if (cm.canHold(1003267, 1)) {
                           cm.gainItem(1002573, (short) -1)
                           cm.gainItem(4001158, (short) -20)
                           cm.gainItem(1003267, (short) 1)
                           cm.sendOk("I have given you the hat.")
                        } else {
                           cm.sendOk("Please make room in your EQUIP inventory before receiving the hat.")
                        }
                     } else {
                        cm.sendOk("You need 20 #t4001158# to get the next hat.")
                     }
                  } else if (cm.haveItem(1002572, 1)) {
                     if (cm.haveItem(4001158, 20)) {
                        if (cm.canHold(1002573, 1)) {
                           cm.gainItem(1002572, (short) -1)
                           cm.gainItem(4001158, (short) -20)
                           cm.gainItem(1002573, (short) 1)
                           cm.sendOk("I have given you the hat.")
                        } else {
                           cm.sendOk("Please make room in your EQUIP inventory before receiving the hat.")
                        }
                     } else {
                        cm.sendOk("You need 20 #t4001158# to get the next hat.")
                     }
                  } else {
                     if (cm.haveItem(4001158, 20)) {
                        if (cm.canHold(1002572, 1)) {
                           cm.gainItem(4001158, (short) -20)
                           cm.gainItem(1002572, (short) 1)
                           cm.sendOk("I have given you the hat.")
                        } else {
                           cm.sendOk("Please make room in your EQUIP inventory before receiving the hat.")
                        }
                     } else {
                        cm.sendOk("You need 20 #t4001158# to get the next hat.")
                     }
                  }
               }

               cm.dispose()
            }
         }

      }
   }
}

NPC2094001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2094001(cm: cm))
   }
   return (NPC2094001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }