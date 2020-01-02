package server.maps;

public interface AnimatedMapleMapObject extends MapleMapObject {
   int stance();

   void stance_$eq(int stance);

   boolean isFacingLeft();
}
