package client.database.data;

public class FamilyData {
   private int characterId;

   private int familyId;

   private int seniorId;

   private int reputation;

   private int todaysReputation;

   private int totalReputation;

   private int reputationToSenior;

   private String precepts;

   public FamilyData(int characterId, int familyId, int seniorId, int reputation, int todaysReputation, int totalReputation, int reputationToSenior, String precepts) {
      this.characterId = characterId;
      this.familyId = familyId;
      this.seniorId = seniorId;
      this.reputation = reputation;
      this.todaysReputation = todaysReputation;
      this.totalReputation = totalReputation;
      this.reputationToSenior = reputationToSenior;
      this.precepts = precepts;
   }

   public int getCharacterId() {
      return characterId;
   }

   public int getFamilyId() {
      return familyId;
   }

   public int getSeniorId() {
      return seniorId;
   }

   public int getReputation() {
      return reputation;
   }

   public int getTodaysReputation() {
      return todaysReputation;
   }

   public int getTotalReputation() {
      return totalReputation;
   }

   public int getReputationToSenior() {
      return reputationToSenior;
   }

   public String getPrecepts() {
      return precepts;
   }
}
