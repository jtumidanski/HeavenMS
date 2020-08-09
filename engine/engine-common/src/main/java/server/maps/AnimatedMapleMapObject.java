package server.maps;

public interface AnimatedMapleMapObject extends MapleMapObject {
   int stance();

   void setStance(int stance);

   boolean isFacingLeft();
}
