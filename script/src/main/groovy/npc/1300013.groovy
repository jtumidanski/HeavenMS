package npc

import net.server.world.MapleParty
import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Blocked Entrance
	Map(s): 		Mushroom Castle - East Castle Tower
	Description:
*/


class NPC1300013 {
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
         return
      } else if (mode == 0 && status == 0) {
         cm.dispose()
         return
      } else if (mode == 0) {
         status--
      } else {
         status++
      }


      if (cm.getMapId() == 106021402) {
         if (!(cm.isQuestCompleted(2331))) {
            cm.dispose()
            return
         }

         if (status == 0) {
            cm.sendSimple("#L0#Enter to fight #bKing Pepe#k and #bYeti Brothers#k.#l\r\n#L1#Enter to fight #bPrime Minister#k.#l")
         } else if (status == 1) {
            if (selection == 0) {
               EventManager pepe = cm.getEventManager("KingPepeAndYetis")
               pepe.setProperty("player", cm.getPlayer().getName())
               pepe.startInstance(cm.getPlayer())
               cm.dispose()
            } else if (selection == 1) {
               EventManager em = cm.getEventManager("MK_PrimeMinister2")

               Optional<MapleParty> party = cm.getPlayer().getParty()
               if (party.isPresent()) {
                  if (!em.startInstance(party.get(), cm.getMap(), 1)) {
                     cm.sendOk("Another party is already challenging the boss in this channel.")
                  }
               } else {
                  if (!em.startInstance(cm.getPlayer())) {
                     cm.sendOk("Another party is already challenging the boss in this channel.")
                  }
               }

               cm.dispose()
            }
         }
      } else {
         int questProgress = cm.getQuestProgressInt(2330, 3300005) + cm.getQuestProgressInt(2330, 3300006) + cm.getQuestProgressInt(2330, 3300007)
         //3 Yetis
         if (!(cm.isQuestStarted(2330) && questProgress < 3)) {
            cm.dispose()
            return
         }

         if (status == 0) {
            cm.sendSimple("#L1#Enter to fight #bKing Pepe#k and #bYeti Brothers#k.#l")
         } else if (status == 1) {
            if (selection == 1) {
               EventManager pepe = cm.getEventManager("KingPepeAndYetis")
               pepe.setProperty("player", cm.getPlayer().getName())
               pepe.startInstance(cm.getPlayer())
               cm.dispose()
            }
         }
      }
   }
}

NPC1300013 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1300013(cm: cm))
   }
   return (NPC1300013) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }