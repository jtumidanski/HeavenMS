package client;

public interface AbstractCharacterListener {
   void onHpChanged(int oldHp);

   void onHpMpPoolUpdate();

   void onStatUpdate();

   void onAnnounceStatPoolUpdate();
}
