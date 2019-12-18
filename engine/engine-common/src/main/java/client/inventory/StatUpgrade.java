package client.inventory;

public enum StatUpgrade {
   incDEX(0), incSTR(1), incINT(2), incLUK(3),
   incMHP(4), incMMP(5), incPAD(6), incMAD(7),
   incPDD(8), incMDD(9), incEVA(10), incACC(11),
   incSpeed(12), incJump(13), incVicious(14), incSlot(15);
   private int value;

   StatUpgrade(int value) {
      this.value = value;
   }
}
