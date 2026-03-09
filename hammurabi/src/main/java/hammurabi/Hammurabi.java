import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Hammurabi {

    Random rand = new Random();
    Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new Hammurabi().playGame();
    }

    void playGame() {
        int population = 100;
        int grain = 2800;
        int acres = 1000;
        int landPrice = 19;
        int totalStarved = 0;
        int totalPercentStarved = 0;

        // Print initial state
        printSummary(1, 0, 5, population, 3000, 3, 200, grain, acres, landPrice);

        for (int year = 1; year <= 10; year++) {

            //coin flip gambling 
            grain = coinFlipGamble(grain);

            // Ask player decisions
            int acresToBuy = askHowManyAcresToBuy(landPrice, grain);
            if (acresToBuy > 0) {
                grain -= acresToBuy * landPrice;
                acres += acresToBuy;
            }

            int acresToSell = 0;
            if (acresToBuy == 0) {
                acresToSell = askHowManyAcresToSell(acres);
                grain += acresToSell * landPrice;
                acres -= acresToSell;
            }

            int bushelsForFood = askHowMuchGrainToFeedPeople(grain);
            grain -= bushelsForFood;

            int acresToPlant = askHowManyAcresToPlant(acres, population, grain);
            grain -= acresToPlant * 2;

            // Compute results
            int plague = plagueDeaths(population);
            population -= plague;

            int starved = starvationDeaths(population, bushelsForFood);

            if (uprising(population, starved)) {
                System.out.println(" You starved " + starved + " people in one year! ");
                System.out.println(" DUE TO THIS EXTREME MISMANAGEMENT YOU HAVE BEEN IMPEACHED! ");
                return;
            }

            totalStarved += starved;
            totalPercentStarved += (population > 0 ? starved * 100 / population : 0);
            population -= starved;

            int newImmigrants = 0;
            if (starved == 0) {
                newImmigrants = immigrants(population, acres, grain);
                population += newImmigrants;
            }

            int bushelsHarvested = harvest(acresToPlant);
            grain += bushelsHarvested;

            int ratsAte = grainEatenByRats(grain);
            grain -= ratsAte;

            landPrice = newCostOfLand();

            if (year < 10) {
                printSummary(year + 1, starved, newImmigrants, population,
                        bushelsHarvested, bushelsHarvested / (acresToPlant > 0 ? acresToPlant : 1),
                        ratsAte, grain, acres, landPrice);
            }

            if (population <= 0) {
                System.out.println(" Everyone has died. Game over. ");
                return;
            }
        }

        finalSummary(totalPercentStarved / 10, totalStarved, acres, population);
    }

    int coinFlipGamble(int grain){

        System.out.println("\nHello there great Hammurabi, would you like to gamble? ");
        System.out.println(" You have " + grain + " bushels in storage. ");
        int bet = getNumber(" How many bushels do you wish to bet?(0 to skip) ");

        if (bet == 0) {
            System.out.println(" You chose not to gamble. wise decision. ");
            return grain;
        }

        boolean heads = rand.nextBoolean();
        System.out.println(" The coin is in the air.. ");

        if (heads) {
            System.out.println(" *** heads! you won! " + bet + " bushels doubled to " + (bet * 2) + " ! *** ");
            return grain + bet;
        } else {
            System.out.println(" *** tails! you lost! ***" + bet + " bushels ** ");
            return grain - bet;
        }
    }

    int tradeGrain(int grain){
        int buyPrice = rand.nextInt(5) + 3;
        int sellPrice = rand.nextInt(5) + 2;

        System.out.println("\n-- GRAIN TRADING MARKET -- ");
        System.out.println(" Neighboring kingdoms are trading grain today! ");
        System.out.println(" You can Buy grain at " + buyPrice + " bushels per 10 bushels received ");
        System.out.println(" You can sell grain at " + sellPrice + " bushels per 10 bushels sold ");
        System.out.println(" You cerrently have + grain " + grain + " bushels ");
        
        int choice = getNumber(" Do you want to (1) Buy, (2) Sell, or (3) Skip? ");

        if (choice == 0) {
            System.out.println(" You chose not to trade. the merchants move on " );
            return grain;
        } else if (choice == 1) {
            int amount = getNumber("How many bushels do you wish to buy? (multiples of 10) ");
            amount = (amount / 10) * 10; // Round down to nearest multiple of 10
            int cost = (amount / 10) * buyPrice;
            if (cost > grain) {
                System.out.println("You can't afford that! You only have" + grain + "bushels.");
                return grain;
            }
            System.out.println("You bought " + amount + " bushels for " + cost + " bushels. great deal! ");
            return grain - cost + amount;
        } else if (choice == 2) {
            int amount = getNumber("How many bushels do you wish to sell? (multiples of 10");
            amount = (amount / 10) * 10;
            if (amount > grains) {
                System.out.println("you don't have that many bushels!");
                return grain;
            }
            int earnings = amount / 10 * sellPrice;
            System.out.println(" you sold " + amount + "bushels for " + earnings + " bushels profit!");
            return grain - amount + earnings;
        } else {
            System.out.println(" invalid choice. the merchant move on. ");
            return grain;
        }
    }

    int askHowManyAcresToBuy(int price, int bushels) {
        while (true) {
            int amount = getNumber("O great Hammurabi, how many acres do you wish to buy? ");
            if (amount < 0) {
                System.out.println("Hammurabi: Please enter a positive number.");
            } else if (amount * price > bushels) {
                System.out.println("Hammurabi: Think again! You only have " + bushels + " bushels.");
            } else {
                return amount;
            }
        }
    }

    int askHowManyAcresToSell(int acresOwned) {
        while (true) {
            int amount = getNumber("O great Hammurabi, how many acres do you wish to sell? ");
            if (amount < 0) {
                System.out.println("Hammurabi: Please enter a positive number.");
            } else if (amount > acresOwned) {
                System.out.println("Hammurabi: Think again! You only own " + acresOwned + " acres.");
            } else {
                return amount;
            }
        }
    }

    int askHowMuchGrainToFeedPeople(int bushels) {
        while (true) {
            int amount = getNumber("O great Hammurabi, how many bushels do you wish to feed your people? ");
            if (amount < 0) {
                System.out.println("Hammurabi: Please enter a positive number.");
            } else if (amount > bushels) {
                System.out.println("Hammurabi: Think again! You only have " + bushels + " bushels.");
            } else {
                return amount;
            }
        }
    }

    int askHowManyAcresToPlant(int acresOwned, int population, int bushels) {
        while (true) {
            int amount = getNumber("O great Hammurabi, how many acres do you wish to plant? ");
            if (amount < 0) {
                System.out.println("Hammurabi: Please enter a positive number.");
            } else if (amount > acresOwned) {
                System.out.println("Hammurabi: Think again! You only own " + acresOwned + " acres.");
            } else if (amount * 2 > bushels) {
                System.out.println("Hammurabi: Think again! You only have " + bushels + " bushels of grain for seed.");
            } else if (amount > population * 10) {
                System.out.println("Hammurabi: Think again! You only have " + population + " people to tend the fields.");
            } else {
                return amount;
            }
        }
    }

    int plagueDeaths(int population) {
        if (rand.nextInt(100) < 15) {
            return population / 2;
        }
        return 0;
    }

    int starvationDeaths(int population, int bushelsFedToPeople) {
        int peopleFed = bushelsFedToPeople / 20;
        if (peopleFed >= population) return 0;
        return population - peopleFed;
    }

    boolean uprising(int population, int howManyPeopleStarved) {
        return howManyPeopleStarved > population * 0.45;
    }

    int immigrants(int population, int acresOwned, int grainInStorage) {
        return (20 * acresOwned + grainInStorage) / (100 * population) + 1;
    }

    int harvest(int acres) {
        int yield = rand.nextInt(6) + 1;
        return acres * yield;
    }

    int grainEatenByRats(int bushels) {
        if (rand.nextInt(100) < 40) {
            int percent = rand.nextInt(21) + 10;
            return bushels * percent / 100;
        }
        return 0;
    }

    int newCostOfLand() {
        return rand.nextInt(7) + 17;
    }

    void printSummary(int year, int starved, int immigrants, int population,
                      int harvested, int yieldPerAcre, int ratsAte, int grain,
                      int acres, int landPrice) {
        System.out.println("\nO great Hammurabi!");

        System.out.println("You are in year " + year + " of your ten year rule.");

        System.out.println("In the previous year " + starved + " people starved to death.");

        System.out.println("In the previous year " + immigrants + " people entered the kingdom.");

        System.out.println("The population is now " + population + ".");

        System.out.println("We harvested " + harvested + " bushels at " + yieldPerAcre + " bushels per acre.");

        System.out.println("Rats destroyed " + ratsAte + " bushels, leaving " + grain + " bushels in storage.");

        System.out.println("The city owns " + acres + " acres of land.");

        System.out.println("Land is currently worth " + landPrice + " bushels per acre.");
    }

    void finalSummary(int avgPercentStarved, int totalStarved, int acres, int population) {
        System.out.println("\nYour ten year rule has ended.");

        System.out.println("In total, " + totalStarved + " people starved.");
        System.out.println("On average, " + avgPercentStarved + "% of people starved per year.");
        System.out.println("You ended with " + acres + " acres and " + population + " people.");
        System.out.println("That is " + acres / population + " acres per person.");

        if (avgPercentStarved > 33 || acres / population < 7) {
            System.out.println("DUE TO THIS EXTREME MISMANAGEMENT YOU HAVE BEEN THROWN OUT OF OFFICE TRUMP WILL TAKE YOUR PLACE !");
        } else if (avgPercentStarved > 10 || acres / population < 9) {
            System.out.println("Your heavy-handed performance smacks of Nero. The people hate your guts!");
        } else if (avgPercentStarved > 3 || acres / population < 10) {
            System.out.println("Your performance could have been better, but wasn't too bad.");
        } else {
            System.out.println("A FANTASTIC PERFORMANCE! Charlemagne, Disraeli, and Jefferson combined could not have done better!");
        }
    }

    int getNumber(String message) {
        while (true) {
            System.out.print(message);
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("\"" + scanner.next() + "\" isn't a number!");
            }
        }
    }
}