package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager
import server.life.MapleLifeFactory
import server.life.MapleMonster
import server.maps.MapleMap
import server.maps.MapleMapObject
import tools.MessageBroadcaster
import tools.ServerNoticeType

import java.awt.*

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2112000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def playersTooClose() {
      Point npcpos = cm.getMap().getMapObject(cm.getNpcObjectId()).getPosition()
      MapleMapObject[] listchr = cm.getMap().getPlayers()

      for (Iterator<MapleMapObject> iterator = listchr.iterator(); iterator.hasNext();) {
         MapleMapObject chr = iterator.next()

         Point chrpos = chr.getPosition()
         if (Math.sqrt(Math.pow((npcpos.getX() - chrpos.getX()), 2) + Math.pow((npcpos.getY() - chrpos.getY()), 2)) < 310) {
            return true
         }
      }

      return false
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

         EventInstanceManager eim = cm.getEventInstance()

         if (cm.getMapId() == 926100203) {
            if (status == 0) {
               int state = eim.getIntProperty("yuleteTalked")

               if (state == -1) {
                  cm.sendOk("Heh, it seems you guys have company. Have fun with them, as I politely request my leave.")

               } else if (playersTooClose()) {
                  cm.sendOk("Oh, hello there. I have been #bmonitoring your moves#k since you guys entered this perimeter. Quite the feat reaching here, I commend all of you. Now, now, look at the time, I've got an appointment right now, I'm afraid I will need to request my leave. But worry not, my #raccessors#k will deal with all of you. Now, if you permit me, I'm leaving now.")

                  eim.setIntProperty("yuleteTalked", -1)
               } else if (eim.getIntProperty("npcShocked") == 0) {
                  cm.sendOk("Ho~ Aren't you quite the sneaky one? Well, it matters not. I have been #bmonitoring your moves#k since you guys entered this perimeter. Quite the feat reaching here, I commend all of you. Now, now, look at the time, I've got an appointment right now, I'm afraid I will need to request my leave. But worry not, my #raccessors#k will deal with all of you. Now, if you permit me, I'm leaving now.")

                  eim.setIntProperty("yuleteTalked", -1)
               } else {
                  cm.sendOk("... Hah! What, wh-- How did you get here?! I though I had sealed all paths here! No matter, this situation will be resolved soon. Guys: DEPLOY the #rmaster weapon#k!! You! Yes, you. Don't you think this ends here, look back at your companions, they need some help! I'll be retreating for now.")

                  eim.setIntProperty("yuleteTalked", 1)
               }
            }

            cm.dispose()
         } else {
            if (status == 0) {
               if (eim.isEventCleared()) {
                  cm.sendOk("Nooooo... I have been beated? But how? Everything I did was for the sake of the development of a greater alchemy! You can't jail me, I did what everybody standing in a place like mine would do! But no, they simply decided to damp up the progress of the science JUST BECAUSE it was deemed dangerous??? Oh, come on!")
               } else {
                  int state = eim.getIntProperty("yuletePassed")

                  if (state == -1) {
                     cm.sendOk("Behold! The pinnacle of Magatia's alchemy studies! Hahahahahahaha...")
                  } else if (state == 0) {
                     cm.sendOk("You guys are such a pain, geez. Very well, I present you my newest weapon, brought by the finest alchemy, #rFrankenroid#k.")
                     MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, "Yulete: I present you my newest weapon, brought by the finest alchemy, Frankenroid!")

                     MapleMap mapobj = eim.getMapInstance(926100401)
                     MapleMonster bossobj = MapleLifeFactory.getMonster(9300139)

                     //mapobj.spawnMonsterWithEffect(bossobj, 13, new Packages.java.awt.Point(250, 100));
                     mapobj.spawnMonsterOnGroundBelow(bossobj, new Point(250, 100))

                     eim.setIntProperty("statusStg7", 1)
                     eim.setIntProperty("yuletePassed", -1)
                  } else {
                     cm.sendOk("You guys are such a pain, geez. Very well, I present you my newest weapon, brought by the finest combined alchemy of Alcadno's and Zenumist's, those that the boring people of Magatia societies have banned to bring along, the #rmighty Frankenroid#k!")
                     MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, "Yulete: I present you my newest weapon, brought by the finest combined alchemy of Alcadno's and Zenumist's, those that the boring people of Magatia societies have banned to bring along, the mighty Frankenroid!!")

                     MapleMap mapobj = eim.getMapInstance(926100401)
                     MapleMonster bossobj = MapleLifeFactory.getMonster(9300140)

                     //mapobj.spawnMonsterWithEffect(bossobj, 14, new Packages.java.awt.Point(250, 100));
                     mapobj.spawnMonsterOnGroundBelow(bossobj, new Point(250, 100))

                     eim.setIntProperty("statusStg7", 2)
                     eim.setIntProperty("yuletePassed", -1)
                  }
               }
            }

            cm.dispose()
         }
      }
   }
}

NPC2112000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2112000(cm: cm))
   }
   return (NPC2112000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }