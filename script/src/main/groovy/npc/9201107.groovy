package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201107 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.getPlayer().getMapId() == 610030500) {
         cm.sendOk("Unbelievable strength and power, anyone can be achieve. But what makes a warrior special is their iron will. No matter the odds, a true warrior pushes through until victory is assured. Thus, the Warrior Chamber is a brutal road where the room itself is against you, as well as the ultra-strong monsters within. Use your skills to shake off the effects and defeat the monsters within to reach the Warrior Statue and claim the Master Sword. Good luck!")
         cm.dispose()
      } else if (cm.getPlayer().getMap().getId() == 610030000) {
         cm.sendOk("A legendary family of heroes, the de Vrisiens are the original founders of the Storm Casters. The family is unique, as each son or daughter inherits the full fighting techniques of their ancestors. This ability has proven to be immensely useful; as it allows for nearly unlimited strategy, improvisation and tactics to defeat all enemies. A true family for the generations.")
         cm.dispose()
      } else if (cm.getPlayer().getMapId() == 610030510) {
         if (cm.getPlayer().getMap().countMonsters() == 0) {
            EventInstanceManager eim = cm.getEventInstance()
            int stgStatus = eim.getIntProperty("glpq5_room")
            int jobNiche = cm.getPlayer().getJob().getJobNiche()

            if ((stgStatus >> jobNiche) % 2 == 0) {
               if (cm.canHold(4001259, 1)) {
                  cm.gainItem(4001259, (short) 1)
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
            cm.sendOk("Eliminate all Crimson Guardians.")
         }
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC9201107 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201107(cm: cm))
   }
   return (NPC9201107) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }