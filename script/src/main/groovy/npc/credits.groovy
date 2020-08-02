package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NpcCredits {
   NPCConversationManager cm
   int status = -1
   int sel = -1


   String[] name_tree = []
   String[] role_tree = []
   String[] name_cursor, role_cursor

// new server names are to be appended at the start of the name stack, building up the chronology.
// make sure the server names are lexicograffically equivalent to their correspondent function.
   String[] servers = ["HeavenMS", "MapleSolaxia", "MoopleDEV", "MetroMS", "BubblesDEV", "OdinMS", "Contributors"]
   String[] servers_history = []

   def addPerson(name, role) {
      name_cursor << name
      role_cursor << role
   }

   def setHistory(from, to) {
      servers_history << [from, to]
   }

/*
function writeServerStaff_MapleNext() {
        addPerson("John Doe", "The role");

        setHistory(INITIAL_YEAR [, CURRENT_YEAR]);
}
*/

   def writeServerStaff_HeavenMS() {
      addPerson("Ronan", "Developer")
      addPerson("Vcoc", "Freelance Developer")
      addPerson("Thora", "Contributor")
      addPerson("GabrielSin", "Contributor")
      addPerson("Masterrulax", "Contributor")
      addPerson("MedicOP", "Adjunct Developer")

      setHistory(2015, 2019)
   }

   def writeServerStaff_MapleSolaxia() {
      addPerson("Aria", "Administrator")
      addPerson("Twdtwd", "Administrator")
      addPerson("Exorcist", "Developer")
      addPerson("SharpAceX", "Developer")
      addPerson("Zygon", "Freelance Developer")
      addPerson("SourMjolk", "Game Master")
      addPerson("Kanade", "Game Master")
      addPerson("Kitsune", "Game Master")

      setHistory(2014, 2015)
   }

   def writeServerStaff_MoopleDEV() {
      addPerson("kevintjuh93", "Developer")
      addPerson("hindie93", "Contributor")
      addPerson("JuniarZ-", "Contributor")

      setHistory(2010, 2012)
   }

   def writeServerStaff_MetroMS() {
      addPerson("David!", "Developer")
      addPerson("XxOsirisxX", "Contributor")
      addPerson("Generic", "Contributor")

      setHistory(2009, 2010)
   }

   def writeServerStaff_BubblesDEV() {
      addPerson("David!", "Developer")
      addPerson("Moogra", "Developer")
      addPerson("XxOsirisxX", "Contributor")
      addPerson("MrMysterious", "Contributor")

      setHistory(2009, 2009)
   }

   def writeServerStaff_OdinMS() {
      addPerson("Serpendiem", "Administrator")
      addPerson("Frz", "Developer")
      addPerson("Patrick", "Developer")
      addPerson("Matze", "Developer")
      addPerson("Vimes", "Developer")

      setHistory(2007, 2008)
   }

   def writeServerStaff_Contributors() {
      addPerson("Jayd", "Contributor")
      addPerson("Dragohe4rt", "Contributor")
      addPerson("Jvlaple", "Contributor")
      addPerson("Stereo", "Contributor")
      addPerson("AngelSL", "Contributor")
      addPerson("Lerk", "Contributor")
      addPerson("Leifde", "Contributor")
      addPerson("ThreeStep", "Contributor")
      addPerson("RMZero213", "Contributor")
      addPerson("ExtremeDevilz", "Contributor")
      addPerson("aaroncsn", "Contributor")
      addPerson("xQuasar", "Contributor")
      addPerson("Xterminator", "Contributor")
      addPerson("XoticStory", "Contributor")
   }

   def writeAllServerStaffs() {
      for (int i = 0; i < servers.length; i++) {
         name_cursor = []
         role_cursor = []

         String srvName = servers[i]
         GroovyObject.invokeMethod("writeServerStaff_" + srvName, "")

         name_tree << name_cursor
         role_tree << role_cursor
      }
   }


   def start() {
      status = -1
      writeAllServerStaffs()
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            String sendStr = "There is the history tree of all participating parties on the build of this server:\r\n\r\n"
            for (int i = 0; i < servers.length; i++) {
               String[] hist = servers_history[i]

               if (hist && hist.length > 0) {
                  sendStr += "#L" + i + "##b" + servers[i] + "#k  --  " + ((hist[0] != hist[1]) ? hist[0] + " ~ " + hist[1] : hist[0]) + "#l\r\n"
               } else {
                  sendStr += "#L" + i + "##b" + servers[i] + "#k#l\r\n"
               }
            }

            cm.sendSimple(sendStr)
         } else if (status == 1) {
            String[] lvName = [], lvRole = []

            for (int i = 0; i < servers.length; i++) {
               if (selection == i) {
                  lvName = name_tree[i]
                  lvRole = role_tree[i]
                  break
               }
            }

            String sendStr = "The staff of #b" + servers[selection] + "#k:\r\n\r\n"
            for (int i = 0; i < lvName.length; i++) {
               sendStr += "  #L" + i + "# " + lvName[i] + " - " + lvRole[i]
               sendStr += "#l\r\n"
            }

            cm.sendPrev(sendStr)
         } else {
            cm.dispose()
         }
      }
   }
}

NpcCredits getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NpcCredits(cm: cm))
   }
   return (NpcCredits) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }