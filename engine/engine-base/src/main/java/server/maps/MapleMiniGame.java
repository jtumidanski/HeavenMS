package server.maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import net.server.Server;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.packet.character.box.AddMatchCard;
import tools.packet.character.box.AddOmokBox;
import tools.packet.character.box.RemoveMiniGameBox;
import tools.packet.character.interaction.GetMatchCard;
import tools.packet.character.interaction.GetMiniGame;
import tools.packet.character.interaction.MiniGameClose;
import tools.packet.character.interaction.MiniGameMoveOmok;
import tools.packet.character.interaction.MiniGameNewVisitor;
import tools.packet.character.interaction.MiniGameOwnerWin;
import tools.packet.character.interaction.MiniGameRemoveVisitor;
import tools.packet.character.interaction.MiniGameTie;
import tools.packet.character.interaction.MiniGameVisitorWin;
import tools.packet.character.interaction.NewMatchCardVisitor;
import tools.packet.character.interaction.PlayerShopChat;

public class MapleMiniGame extends AbstractMapleMapObject {
   private MapleCharacter owner;
   private MapleCharacter visitor;
   private String password;
   private MiniGameType GameType = MiniGameType.UNDEFINED;
   private int pieceType;
   private int inProgress = 0;
   private int[] piece = new int[250];
   private List<Integer> list4x3 = new ArrayList<>();
   private List<Integer> list5x4 = new ArrayList<>();
   private List<Integer> list6x5 = new ArrayList<>();
   private String description;
   private int loser = 1;
   private int firstSlot = 0;
   private int visitorPoints = 0, visitorScore = 0, visitorForfeits = 0, lastVisitor = -1;
   private int ownerPoints = 0, ownerScore = 0, ownerForfeits = 0;
   private boolean visitorQuit, ownerQuit;
   private long nextAvailableTie = 0;
   private int matchesToWin = 0;

   public MapleMiniGame(MapleCharacter owner, String description, String password) {
      this.owner = owner;
      this.description = description;
      this.password = password;
   }

   public String getPassword() {
      return this.password;
   }

   public boolean checkPassword(String sentPw) {
      return this.password.length() == 0 || sentPw.toLowerCase().contentEquals(this.password.toLowerCase());
   }

   public boolean hasFreeSlot() {
      return visitor == null;
   }

   public boolean isOwner(MapleCharacter c) {
      return owner.equals(c);
   }

   public void addVisitor(MapleCharacter challenger) {
      visitor = challenger;
      if (lastVisitor != challenger.getId()) {
         ownerScore = 0;
         ownerForfeits = 0;

         visitorScore = 0;
         visitorForfeits = 0;
         lastVisitor = challenger.getId();
      }

      MapleCharacter owner = this.getOwner();
      if (GameType == MiniGameType.OMOK) {
         PacketCreator.announce(owner, new MiniGameNewVisitor(this, challenger, 1));
         MasterBroadcaster.getInstance().sendToAllInMap(owner.getMap(), new AddOmokBox(owner, 2, 0));
      } else if (GameType == MiniGameType.MATCH_CARD) {
         PacketCreator.announce(owner, new NewMatchCardVisitor(this, challenger, 1));
         MasterBroadcaster.getInstance().sendToAllInMap(owner.getMap(), new AddMatchCard(owner, 2, 0));
      }
   }

   public void closeRoom(boolean forceClose) {
      MasterBroadcaster.getInstance().sendToAllInMap(owner.getMap(), new RemoveMiniGameBox(owner.getId()));

      if (forceClose) {
         MasterBroadcaster.getInstance().sendToGameOwner(this, new MiniGameClose(false, 4));
      }
      MasterBroadcaster.getInstance().sendToGameVisitor(this, new MiniGameClose(true, 3));

      if (visitor != null) {
         visitor.setMiniGame(null);
         visitor = null;
      }

      owner.setMiniGame(null);
      owner = null;
   }

   public void removeVisitor(boolean forceClose, MapleCharacter challenger) {
      if (visitor == challenger) {
         if (forceClose) {
            PacketCreator.announce(visitor, new MiniGameClose(true, 4));
         }

         challenger.setMiniGame(null);
         visitor = null;

         PacketCreator.announce(this.getOwner(), new MiniGameRemoveVisitor());
         if (GameType == MiniGameType.OMOK) {
            MasterBroadcaster.getInstance().sendToAllInMap(owner.getMap(), new AddOmokBox(owner, 1, 0));
         } else if (GameType == MiniGameType.MATCH_CARD) {
            MasterBroadcaster.getInstance().sendToAllInMap(owner.getMap(), new AddMatchCard(owner, 1, 0));
         }
      }
   }

   public boolean isVisitor(MapleCharacter challenger) {
      return visitor == challenger;
   }

   public int getFirstSlot() {
      return firstSlot;
   }

   public void setFirstSlot(int type) {
      firstSlot = type;
   }

   private void updateMiniGameBox() {
      MasterBroadcaster.getInstance().sendToAllInMap(owner.getMap(), new AddOmokBox(owner, visitor != null ? 2 : 1, inProgress));
   }

   private synchronized boolean miniGameMatchFinish() {
      if (isMatchInProgress()) {
         inProgress = 0;
         return true;
      } else {
         return false;
      }
   }

   private void miniGameMatchFinished() {
      updateMiniGameBox();

      if (ownerQuit) {
         owner.closeMiniGame(true);
      } else if (visitorQuit) {
         visitor.closeMiniGame(true);
      }
   }

   public void miniGameMatchStarted() {
      inProgress = 1;
      ownerQuit = false;
      visitorQuit = false;
   }

   public void setQuitAfterGame(MapleCharacter player, boolean quit) {
      if (isOwner(player)) {
         ownerQuit = quit;
      } else {
         visitorQuit = quit;
      }
   }

   public boolean isMatchInProgress() {
      return inProgress != 0;
   }

   public void denyTie(MapleCharacter chr) {
      if (this.isOwner(chr)) {
         inProgress |= (1 << 1);
      } else {
         inProgress |= (1 << 2);
      }
   }

   public boolean isTieDenied(MapleCharacter chr) {
      if (this.isOwner(chr)) {
         return ((inProgress >> 2) % 2) == 1;
      } else {
         return ((inProgress >> 1) % 2) == 1;
      }
   }

   public void miniGameMatchOwnerWins(boolean forfeit) {
      if (!miniGameMatchFinish()) {
         return;
      }

      owner.setMiniGamePoints(visitor, 1, this.isOmok());

      if (visitorForfeits < 4 || !forfeit) {
         ownerScore += 50;
      }
      visitorScore += (15 * (forfeit ? -1 : 1));
      if (forfeit) {
         visitorForfeits++;
      }

      MasterBroadcaster.getInstance().sendToGamers(this, new MiniGameOwnerWin(this, forfeit));

      miniGameMatchFinished();
   }

   public void miniGameMatchVisitorWins(boolean forfeit) {
      if (!miniGameMatchFinish()) {
         return;
      }

      owner.setMiniGamePoints(visitor, 2, this.isOmok());

      if (ownerForfeits < 4 || !forfeit) {
         visitorScore += 50;
      }
      ownerScore += (15 * (forfeit ? -1 : 1));
      if (forfeit) {
         ownerForfeits++;
      }

      MasterBroadcaster.getInstance().sendToGamers(this, new MiniGameVisitorWin(this, forfeit));

      miniGameMatchFinished();
   }

   public void miniGameMatchDraw() {
      if (!miniGameMatchFinish()) {
         return;
      }

      owner.setMiniGamePoints(visitor, 3, this.isOmok());

      long timeNow = Server.getInstance().getCurrentTime();
      if (nextAvailableTie <= timeNow) {
         visitorScore += 10;
         ownerScore += 10;

         nextAvailableTie = timeNow + 5 * 60 * 1000;
      }

      MasterBroadcaster.getInstance().sendToGamers(this, new MiniGameTie(this));

      miniGameMatchFinished();
   }

   public void setOwnerPoints() {
      ownerPoints++;
      if (ownerPoints + visitorPoints == matchesToWin) {
         if (ownerPoints == visitorPoints) {
            miniGameMatchDraw();
         } else if (ownerPoints > visitorPoints) {
            miniGameMatchOwnerWins(false);
         } else {
            miniGameMatchVisitorWins(false);
         }
         ownerPoints = 0;
         visitorPoints = 0;
      }
   }

   public void setVisitorPoints() {
      visitorPoints++;
      if (ownerPoints + visitorPoints == matchesToWin) {
         if (ownerPoints > visitorPoints) {
            miniGameMatchOwnerWins(false);
         } else if (visitorPoints > ownerPoints) {
            miniGameMatchVisitorWins(false);
         } else {
            miniGameMatchDraw();
         }
         ownerPoints = 0;
         visitorPoints = 0;
      }
   }

   public int getPieceType() {
      return pieceType;
   }

   public void setPieceType(int type) {
      pieceType = type;
   }

   public MiniGameType getGameType() {
      return GameType;
   }

   public void setGameType(MiniGameType game) {
      GameType = game;
      if (GameType == MiniGameType.MATCH_CARD) {
         if (matchesToWin == 6) {
            for (int i = 0; i < 6; i++) {
               list4x3.add(i);
               list4x3.add(i);
            }
         } else if (matchesToWin == 10) {
            for (int i = 0; i < 10; i++) {
               list5x4.add(i);
               list5x4.add(i);
            }
         } else {
            for (int i = 0; i < 15; i++) {
               list6x5.add(i);
               list6x5.add(i);
            }
         }
      }
   }

   public boolean isOmok() {
      return GameType.equals(MiniGameType.OMOK);
   }

   public void shuffleList() {
      if (matchesToWin == 6) {
         Collections.shuffle(list4x3);
      } else if (matchesToWin == 10) {
         Collections.shuffle(list5x4);
      } else {
         Collections.shuffle(list6x5);
      }
   }

   public int getCardId(int slot) {
      int cardId;
      if (matchesToWin == 6) {
         cardId = list4x3.get(slot);
      } else if (matchesToWin == 10) {
         cardId = list5x4.get(slot);
      } else {
         cardId = list6x5.get(slot);
      }
      return cardId;
   }

   public int getMatchesToWin() {
      return matchesToWin;
   }

   public void setMatchesToWin(int type) {
      matchesToWin = type;
   }

   public int getLoser() {
      return loser;
   }

   public void setLoser(int type) {
      loser = type;
   }

   public void chat(MapleClient c, String chat) {
      MasterBroadcaster.getInstance().sendToGamers(this, new PlayerShopChat(c.getPlayer().getName(), chat, (byte) (isOwner(c.getPlayer()) ? 0 : 1)));
   }

   public void sendOmok(MapleClient c, int type) {
      PacketCreator.announce(c, new GetMiniGame(this, isOwner(c.getPlayer()), type));
   }

   public void sendMatchCard(MapleClient c, int type) {
      PacketCreator.announce(c, new GetMatchCard(this, isOwner(c.getPlayer()), type));
   }

   public MapleCharacter getOwner() {
      return owner;
   }

   public MapleCharacter getVisitor() {
      return visitor;
   }

   public void setPiece(int move1, int move2, int type, MapleCharacter chr) {
      int slot = move2 * 15 + move1 + 1;
      if (piece[slot] == 0) {
         piece[slot] = type;
         MasterBroadcaster.getInstance().sendToGamers(this, new MiniGameMoveOmok(move1, move2, type));
         for (int y = 0; y < 15; y++) {
            for (int x = 0; x < 11; x++) {
               if (searchCombo(x, y, type)) {
                  if (this.isOwner(chr)) {
                     this.miniGameMatchOwnerWins(false);
                     this.setLoser(0);
                  } else {
                     this.miniGameMatchVisitorWins(false);
                     this.setLoser(1);
                  }
                  for (int y2 = 0; y2 < 15; y2++) {
                     for (int x2 = 0; x2 < 15; x2++) {
                        int slot2 = (y2 * 15 + x2 + 1);
                        piece[slot2] = 0;
                     }
                  }
               }
            }
         }
         for (int y = 0; y < 15; y++) {
            for (int x = 4; x < 15; x++) {
               if (searchCombo2(x, y, type)) {
                  if (this.isOwner(chr)) {
                     this.miniGameMatchOwnerWins(false);
                     this.setLoser(0);
                  } else {
                     this.miniGameMatchVisitorWins(false);
                     this.setLoser(1);
                  }
                  for (int y2 = 0; y2 < 15; y2++) {
                     for (int x2 = 0; x2 < 15; x2++) {
                        int slot2 = (y2 * 15 + x2 + 1);
                        piece[slot2] = 0;
                     }
                  }
               }
            }
         }
      }
   }

   private boolean searchCombo(int x, int y, int type) {
      int slot = y * 15 + x + 1;
      for (int i = 0; i < 5; i++) {
         if (piece[slot + i] == type) {
            if (i == 4) {
               return true;
            }
         } else {
            break;
         }
      }
      for (int j = 15; j < 17; j++) {
         for (int i = 0; i < 5; i++) {
            if (piece[slot + i * j] == type) {
               if (i == 4) {
                  return true;
               }
            } else {
               break;
            }
         }
      }
      return false;
   }

   private boolean searchCombo2(int x, int y, int type) {
      int slot = y * 15 + x + 1;
      for (int j = 14; j < 15; j++) {
         for (int i = 0; i < 5; i++) {
            if (piece[slot + i * j] == type) {
               if (i == 4) {
                  return true;
               }
            } else {
               break;
            }
         }
      }
      return false;
   }

   public String getDescription() {
      return description;
   }

   public int getOwnerScore() {
      return ownerScore;
   }

   public int getVisitorScore() {
      return visitorScore;
   }

   @Override
   public MapleMapObjectType type() {
      return MapleMapObjectType.MINI_GAME;
   }

   public enum MiniGameType {
      UNDEFINED(0), OMOK(1), MATCH_CARD(2);
      private int value;

      MiniGameType(int value) {
         this.value = value;
      }

      public int getValue() {
         return value;
      }
   }

   public enum MiniGameResult {
      WIN, LOSS, TIE
   }
}
