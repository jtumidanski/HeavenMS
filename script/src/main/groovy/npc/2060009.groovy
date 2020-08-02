package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2060009 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   String menu
   boolean payment = false
   boolean atHerbTown = false

   def start() {
      if (cm.getPlayer().getMap().getId() == 251000100) {
         atHerbTown = true
      }

      if (cm.haveItem(4031242)) {
         if (atHerbTown) {
            menu = "#L0##bI will use #t4031242##k to move to #b#m230030200##k.#l\r\n#L1#Go to #b#m230000000##k after paying #b10000mesos#k.#l"
         } else {
            menu = "#L0##bI will use #t4031242##k to move to #b#m230030200##k.#l\r\n#L1#Go to #b#m251000000##k after paying #b10000mesos#k.#l"
         }
      } else {
         if (atHerbTown) {
            menu = "#L0#Go to #b#m230030200##k after paying #b1000mesos#k.#l\r\n#L1#Go to #b#m230000000##k after paying #b10000mesos#k.#l"
         } else {
            menu = "#L0#Go to #b#m230030200##k after paying #b1000mesos#k.#l\r\n#L1#Go to #b#m251000000##k after paying #b10000mesos#k.#l"
         }
         payment = true
      }
      cm.sendSimple("Ocean are all connected to each other. Place you can't reach by foot can easily reached oversea. How about taking #bDolphin Taxi#k with us today?\r\n" + menu)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         if (selection == 0) {
            if (payment) {
               if (cm.getPlayer().getMeso() < 1000) {
                  cm.sendOk(I18nMessage.from("2060009_NOT_ENOUGH_MONEY"))
                  cm.dispose()
               } else {
                  cm.gainMeso(-1000)
               }
            } else {
               cm.gainItem(4031242, (short) -1)
            }
            cm.warp(230030200, 2)
            cm.dispose()
            return
         } else if (selection == 1) {
            if (cm.getPlayer().getMeso() < 10000) {
               cm.sendOk(I18nMessage.from("2060009_NOT_ENOUGH_MONEY"))
               cm.dispose()
               return
            } else {
               cm.gainMeso(-10000)
               cm.warp(atHerbTown ? 230000000 : 251000100)
            }
         }
         cm.dispose()
      }
   }
}

NPC2060009 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2060009(cm: cm))
   }
   return (NPC2060009) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }