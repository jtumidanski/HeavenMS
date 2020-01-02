package npc

import net.server.world.MaplePartyCharacter
import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2030008 {
   NPCConversationManager cm
   int status = -1
   int selectedType = -1
   EventManager em

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (cm.haveItem(4001109, 1)) {
            cm.warp(921100000, "out00")
            cm.dispose()
            return
         }

         if (!(cm.isQuestStarted(100200) || cm.isQuestCompleted(100200))) {
            if (cm.getPlayer().getLevel() >= 50) {
               cm.sendOk("Beware, for the power of old has not been forgotten... If you seek to defeat #rZakum#k someday, earn the #bChief's Residence Council#k approval foremost and then #bface the trials#k, only then you will become eligible to fight.")
            } else {
               cm.sendOk("Beware, for the power of old has not been forgotten...")
            }

            cm.dispose()
            return
         }

         em = cm.getEventManager("ZakumPQ")
         if (em == null) {
            cm.sendOk("The Zakum PQ has encountered an error.")
            cm.dispose()
            return
         }

         if (status == 0) {
            cm.sendSimple("#e#b<Party Quest: Zakum Campaign>\r\n#k#n" + em.getProperty("party") + "\r\n\r\nBeware, for the power of old has not been forgotten... #b\r\n#L0#Enter the Unknown Dead Mine (Stage 1)#l\r\n#L1#Face the Breath of Lava (Stage 2)#l\r\n#L2#Forging the Eyes of Fire (Stage 3)#l")
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
               if (cm.haveItem(4031061) && !cm.haveItem(4031062)) {
                  cm.sendYesNo("Would you like to attempt the #bBreath of Lava#k?  If you fail, there is a very real chance you will die.")
               } else {
                  if (cm.haveItem(4031062)) {
                     cm.sendNext("You've already got the #bBreath of Lava#k, you don't need to do this stage.")
                  } else {
                     cm.sendNext("Please complete the earlier trials first.")
                  }

                  cm.dispose()
               }
            } else {
               if (cm.haveItem(4031061) && cm.haveItem(4031062)) {
                  if (!cm.haveItem(4000082, 30)) {
                     cm.sendOk("You have completed the trials, however there's still the need of #b30 #t4000082##k to forge 5 #t4001017#.")
                  } else {
                     cm.completeQuest(100201)
                     cm.gainItem(4031061, (short) -1)
                     cm.gainItem(4031062, (short) -1)
                     cm.gainItem(4000082, (short) -30)

                     cm.gainItem(4001017, (short) 5)
                     cm.sendNext("You #rhave completed the trials#k, from now on having my approval to challenge Zakum.")
                  }

                  cm.dispose()
               } else {
                  cm.sendOk("You lack some of the required items to forge the #b#t4001017##k.")
                  cm.dispose()
               }
            }
         } else if (status == 2) {
            cm.warp(280020000, 0)
            cm.dispose()
         }
      }
   }
}

NPC2030008 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2030008(cm: cm))
   }
   return (NPC2030008) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }