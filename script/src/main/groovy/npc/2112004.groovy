package npc

import net.server.world.MaplePartyCharacter
import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2112004 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   EventManager em

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

         if (cm.getMapId() != 261000011) {
            if (status == 0) {
               cm.sendYesNo("We must keep fighting to save Juliet, please keep your pace. If you are not feeling so well to continue, your companions and I will understand... So, are you going to retreat?")
            } else if (status == 1) {
               cm.warp(926100700, 0)
               cm.dispose()
            }
         } else {
            if (status == 0) {
               em = cm.getEventManager("MagatiaPQ_Z")
               if (em == null) {
                  cm.sendOk("The Magatia PQ (Zenumist) has encountered an error.")
                  cm.dispose()
                  return
               } else if (cm.isUsingOldPqNpcStyle()) {
                  action((byte) 1, (byte) 0, 0)
                  return
               }

               cm.sendSimple("#e#b<Party Quest: Romeo and Juliet>\r\n#k#n" + em.getProperty("party") + "\r\n\r\nMy beloved Juliet has been kidnapped! Although she is Alcadno's, I can't stand by and just see her suffer just because of this foolish clash. I need you and your colleagues help to save her! Please, help us!! Please have your #bparty leader#k talk to me.#b\r\n#L0#I want to participate in the party quest.\r\n#L1#I would like to " + (cm.getPlayer().isRecvPartySearchInviteEnabled() ? "disable" : "enable") + " Party Search.\r\n#L2#I would like to hear more details.")
            } else if (status == 1) {
               if (selection == 0) {
                  if (cm.getParty().isEmpty()) {
                     cm.sendOk("You can participate in the party quest only if you are in a party.")
                     cm.dispose()
                  } else if (!cm.isLeader()) {
                     cm.sendOk("Your party leader must talk to me to start this party quest.")
                     cm.dispose()
                  } else {
                     MaplePartyCharacter[] eli = em.getEligibleParty(cm.getParty().orElseThrow())
                     if (eli.size() > 0) {
                        if (!em.startInstance(cm.getParty().orElseThrow(), cm.getPlayer().getMap(), 1)) {
                           cm.sendOk("Another party has already entered the #rParty Quest#k in this channel. Please try another channel, or wait for the current party to finish.")
                        }
                     } else {
                        cm.sendOk("You cannot start this party quest yet, because either your party is not in the range size, some of your party members are not eligible to attempt it or they are not in this map. If you're having trouble finding party members, try Party Search.")
                     }

                     cm.dispose()
                  }
               } else if (selection == 1) {
                  boolean psState = cm.getPlayer().toggleRecvPartySearchInvite()
                  cm.sendOk("Your Party Search status is now: #b" + (psState ? "enabled" : "disabled") + "#k. Talk to me whenever you want to change it back.")
                  cm.dispose()
               } else {
                  cm.sendOk("#e#b<Party Quest: Romeo and Juliet>#k#n\r\nNot long ago, a scientist named Yulete has been banished from this town because of his researches of combined alchemies of Alcadno's and Zenumist's. Because of the immensurable amount of power coming from this combination, it is forbidden by law to study both. Yet, he ignored this law and got hands in both researches. As a result, he has been exiled.\r\nHe is now retaliating, already took my beloved one and his next target is me, as we are big pictures of Magatia, successors of both societies. But I'm not afraid. We must recover her at all costs!\r\n")
                  cm.dispose()
               }
            }
         }
      }
   }
}

NPC2112004 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2112004(cm: cm))
   }
   return (NPC2112004) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }