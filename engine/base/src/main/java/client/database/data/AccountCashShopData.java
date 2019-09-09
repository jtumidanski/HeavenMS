package client.database.data;

public class AccountCashShopData {
   private int nxCredit;

   private int maplePoint;

   private int nxPrepaid;

   public AccountCashShopData(int nxCredit, int maplePoint, int nxPrepaid) {
      this.nxCredit = nxCredit;
      this.maplePoint = maplePoint;
      this.nxPrepaid = nxPrepaid;
   }

   public int getNxCredit() {
      return nxCredit;
   }

   public int getMaplePoint() {
      return maplePoint;
   }

   public int getNxPrepaid() {
      return nxPrepaid;
   }
}
