package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2030006 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   List<List<Object>> questionTree = [
         //Questions Related to CHARACTERS
         ["In MapleStory, what is the EXP needed to level up from Lv1 to Lv2?", ["20", "15", "4", "12", "16"], 1],
         ["In 1st job adv. which of the following is WRONG requirement?", ["Magician - Level 8", "Pirate - 20 DEX or more", "Archer - 25 DEX or more", "Thief - 20 LUK or more", "Swordman - 35 STR or more"], 3],
         ["When you hit by monster, which of the following is not fully explained?", ["Sealed - skills become disabled", "Undead - turns undead & halved recovery amounts", "Weaken - slow down moving speed", "Cursed - EXP received are decreased", "Stunned - cannot move"], 2],
         ["For the 1st job adv. Which job fully states the job adv. requirement?", ["Pirate - 25 LUK", "Magician - Level 10", "Thief - 25 LUK", "Warrior - 30 STR", "Bowman - 25 DEX"], 4],

         //Questions Related to ITEMS
         ["Which of following monsters got CORRECT item corresponding to the monster?", ["Royal cactus - Needle", "Wild Boar - Boar fang", "Lazy Buffy - Buffy hat", "Chipmunk - Nut", "Stirge - Stirge's wing"], 4],
         ["Which of following monsters got WRONG item corresponding to the monster?", ["Greatest Oldies - Greatest oldies", "Nependeath - Nependeath's leaf", "Ghost stump - Seedling", "Sparker - Seal tooth", "Miner Zombie - Zombie's lost tooth"], 1],
         //["In GM Event, how many FRUIT CAKE you can get as reward?", ["20", "200", "5", "25", "100"], 2],
         ["Which of following potions got CORRECT info.?", ["Warrior Elixir - Attack +5 for 3 minutes", "Pure Water - Recover 700 MP", "Cake - Recover 150 HP & MP", "Salad - Recover 300 MP", "Pizza - Recover 400 HP"], 4],
         ["Which of following potions got WRONG info.?", ["Mana Elixir - Recover 300 MP", "Tonic - Cures state of weakness", "Apple - Recover 30 HP", "Sunrise Dew - Recover 3000 MP", "Ramen - Recover 1000 HP"], 3],

         //Questions Related to MONSTERS
         ["Green Mushroom, Tree Stump, Bubbling, Axe Stump, Octopus, which is highest level of all?", ["Tree Stump", "Bubbling", "Axe Stump", "Octopus", "Green Mushroom"], 2],
         ["Which monster will be seen during the ship trip to Orbis/Ellinia?", ["Werewolf", "Slime", "Crimson Balrog", "Zakum", "Star Pixie"], 2],
         ["Maple Island doesn't have which following monsters?", ["Shroom", "Blue Snail", "Slime", "Red Snail", "Pig"], 4],    // to get conformant with website answers, thanks to Vcoc
         ["Which monster is not at Victoria Island and Sleepywood?", ["Evil Eye", "Sentinel", "Jr. Balrog", "Ghost Stump", "Snail"], 1],
         ["El Nath doesn't have which following monsters?", ["Dark Yeti", "Dark Ligator", "Yeti & Pepe", "Bain", "Coolie Zombie"], 1],
         ["Which of following monsters can fly?", ["Malady", "Ligator", "Cold Eye", "Meerkat", "Alishar"], 0],
         ["Which of these monsters will you NOT be facing in Ossyria?", ["Lunar Pixie", "Lioner", "Cellion", "Croco", "Hector"], 3],
         ["Which monster has not appeared in Maple Island?", ["Snail", "Shroom", "Evil Eye", "Orange Mushroom", "Blue Snail"], 2],

         //Questions Related to QUESTS
         ["Which material doesn't need for awaken Hero's Gladius?", ["Flaming Feather", "Old Gladius", "Piece of Ice", "Ancient Scroll", "Fairy Wing"], 4],
         ["Which of following quests can be repeated?", ["Mystery of Niora Hospital", "Rightful Donation Culture", "The Ghost Whereabout", "Arwen and the Glass Shoe", "Maya and the Weird Medicine"], 3],
         ["Which of following are not 2nd job adv.?", ["Mage", "Cleric", "Assassin", "Gunslinger", "Fighter"], 0],
         ["Which of following is the highest level quest?", ["Cupid's Courier", "Lost in the Ocean", "Alcaster and the Dark Crystal", "Eliminating the Drumming Bunny", "War of Pang Pang"], 2],

         //Questions Related to TOWN/NPC
         ["Which town is not at Victoria Island?", ["Florina Beach or Nautilus", "Amherst or Southperry", "Kerning City & Square", "Perion or Ellinia", "Sleepywood"], 1],
         ["Which is the first NPC you meet in Maple Island?", ["Sera", "Heena", "Lucas", "Roger", "Shanks"], 1],
         ["Which NPC cannot be seen in El Nath?", ["Vogen", "Sophia", "Pedro", "Master Sergeant Fox", "Rumi"], 1],
         ["Which NPC cannot be seen in El Nath snowfield?", ["Hidden Rock", "Glibber", "Jeff", "Holy Stone", "Elma the Housekeeper"], 4],
         ["Which NPC cannot be seen in Perion?", ["Ayan", "Sophia", "Mr. Smith", "Francois", "Manji"], 3],
         ["Which NPC cannot be seen in Henesys?", ["Teo", "Vicious", "Mia", "Doofus", "Casey"], 0],
         ["Which NPC cannot be seen in Ellinia?", ["Mr. Park", "Mar the Fairy", "Roel", "Ria", "Shane"], 2],
         ["Which NPC cannot be seen in Kerning City?", ["Dr. Faymus", "Mong from Kong", "Ervine", "Luke", "Nella"], 3],
         ["Which NPC is not related to pets?", ["Doofus", "Vicious", "Patricia", "Weaver", "Cloy"], 1],
         ["In Kerning City, who is the father of Alex, the runaway kid?", ["Chief Stan", "JM From tha Streetz", "Dr. Faymus", "Vicious", "Luke"], 0],
         ["Which NPC is not belong to Alpha Platoon's Network of Communication?", ["Staff Sergeant Charlie", "Sergeant Bravo", "Corporal Easy", "Master Sergeant Fox", "Peter"], 4],
         ["What do you receive in return from giving 30 Dark Marbles to the 2nd job advancement NPC?", ["Old Ring", "Memory Powder", "Fairy Dust", "Proof of Hero", "Scroll of Secrets"], 3],
         ["Which item you give Maya at Henesys in order to cure her sickness?", ["Apple", "Power Elixir", "Weird Medicine", "Chrysanthemum", "Orange Juice"], 2],
         ["Which of following NPC is not related to item synthesis/refine?", ["Neve", "Serryl", "Shane", "Francois", "JM From tha Streetz"], 2],
         ["Which NPC cannot be seen in Maple Island?", ["Bari", "Teo", "Pio", "Sid", "Maria"], 1],
         ["Who do you see in the monitor in the navigation room with Kyrin?", ["Lucas", "Dr. Kim", "Chief Stan", "Scadur", "Professor Foxwit"], 1],
         ["You know Athena Pierce in Henesys? What color are her eyes?", ["Blue", "Green", "Brown", "Red", "Black"], 1],
         ["How many feathers are there on Dances with Barlog's Hat?", ["7", "8", "3", "13", "16"], 3],
         ["What's the color of the marble Grendel the Really Old from Ellinia carries with him?", ["White", "Orange", "Blue", "Purple", "Green"], 2]
   ]

   int question
   int[] questionPool
   int questionPoolCursor
   int questionAnswer

   def start() {
      status = -1
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
            if (cm.getPlayer().gotPartyQuestItem("JBQ") && !cm.haveItem(4031058, 1)) {
               if (cm.haveItem(4005004, 1)) {
                  if (!cm.canHold(4031058)) {
                     cm.sendNext("Have a free ETC slot available before accepting this trial.")
                     cm.dispose()
                  } else {
                     cm.sendNext("Alright... I'll be testing out your wisdom here. Answer all the questions correctly, and you will pass the test BUT, if you even lie to me once, then you'll have to start over again ok, here we go.")
                  }
               } else {
                  cm.sendNext("Bring me a #b#t4005004##k to proceed with the trial.")
                  cm.dispose()
               }
            } else {
               cm.dispose()
            }
         } else if (status == 1) {
            cm.gainItem(4005004, (short) -1)
            instantiateQuestionPool()

            question = fetchNextQuestion()
            String questionHead = generateQuestionHeading()
            List rawQuestionData = questionTree[question]
            String questionEntry = rawQuestionData[0]

            def questionData = generateSelectionMenu(rawQuestionData[1] as String[], rawQuestionData[2] as Integer)
            String questionOptions = questionData[0]
            questionAnswer = questionData[1] as Integer

            cm.sendSimple(questionHead + questionEntry + "\r\n\r\n#b" + questionOptions + "#k")
         } else if (status >= 2 && status <= 5) {
            if (!evaluateAnswer(selection)) {
               cm.sendNext("You have failed the question.")
               cm.dispose()
               return
            }

            question = fetchNextQuestion()
            String questionHead = generateQuestionHeading()
            List rawQuestionData = questionTree[question]
            String questionEntry = rawQuestionData[0]

            def questionData = generateSelectionMenu(rawQuestionData[1] as String[], rawQuestionData[2] as Integer)
            String questionOptions = questionData[0]
            questionAnswer = questionData[1] as Integer

            cm.sendSimple(questionHead + questionEntry + "\r\n\r\n#b" + questionOptions + "#k")
         } else if (status == 6) {
            if (!evaluateAnswer(selection)) {
               cm.sendNext("You have failed the question.")
               cm.dispose()
               return
            }

            cm.sendOk("Alright. All your answers have been proven as the truth. Your wisdom has been proven.\r\nTake this necklace and go back.")
            cm.gainItem(4031058, (short) 1)
            cm.dispose()
         } else {
            cm.sendOk("Unexpected branch.")
            cm.dispose()
         }
      }
   }

   def evaluateAnswer(selection) {
      return selection == questionAnswer
   }

   def generateQuestionHeading() {
      return "Here's the ".concat(status.toString()) + (status == 1 ? "st" : status == 2 ? "nd" : status == 3 ? "rd" : "th") + " question. "
   }

   static def shuffleArray(int[] array) {
      for (int i = array.length - 1; i > 0; i--) {
         int j = Math.floor(Math.random() * (i + 1)).intValue()
         int temp = array[i]
         array[i] = array[j]
         array[j] = temp
      }
   }

   def instantiateQuestionPool() {
      questionPool = []

      for (int i = 0; i < questionTree.size(); i++) {
         questionPool << i
      }

      shuffleArray(questionPool)
      questionPoolCursor = 0
   }

   def fetchNextQuestion() {
      int next = questionPool[questionPoolCursor]
      questionPoolCursor++

      return next
   }

   static def shuffle(String[] array) {
      int currentIndex = array.length, randomIndex
      String temporaryValue

      // While there remain elements to shuffle...
      while (0 != currentIndex) {

         // Pick a remaining element...
         randomIndex = Math.floor(Math.random() * currentIndex).intValue()
         currentIndex -= 1

         // And swap it with the current element.
         temporaryValue = array[currentIndex]
         array[currentIndex] = array[randomIndex]
         array[randomIndex] = temporaryValue
      }

      return array
   }

   static def generateSelectionMenu(String[] array, int answer) {
      String answerStr = array[answer]
      int answerPos = -1

      shuffle(array)

      String menu = ""
      for (int i = 0; i < array.length; i++) {
         menu += "#L" + i + "#" + array[i] + "#l\r\n"
         if (answerStr == array[i]) {
            answerPos = i
         }
      }
      return [menu, answerPos]
   }
}

NPC2030006 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2030006(cm: cm))
   }
   return (NPC2030006) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }