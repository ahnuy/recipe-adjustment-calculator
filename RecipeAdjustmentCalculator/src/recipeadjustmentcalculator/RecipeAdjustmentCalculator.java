
package recipeadjustmentcalculator;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class RecipeAdjustmentCalculator {
    //ARRAYS
    ArrayList <Fraction> amount = new ArrayList(); //original amount
    ArrayList <Fraction> adjustedAmount = new ArrayList(); //adjusted amount
    ArrayList <String> unit = new ArrayList(); //unit
    ArrayList <String> ingredient = new ArrayList(); //ingredient name
    String [] possibleUnit = {"cup", "tablespoon", "teaspoon", "cups", "tablespoons", "teaspoons"}; //units that this program recognizes and will convert
    
    //METHODS
    //make sure the user inputs a positive integer
    public int catchIntError(String str){
        Scanner s = new Scanner(System.in);
        int i = 0;
        System.out.println(str);
        boolean bool = true;
        //make sure they input a valid positive integer
        //otherwise, keep asking them to input again
        while (bool){
            try{
                i = Integer.valueOf(s.nextLine());
                if (i <= 0){
                    System.out.println("Invalid input. Please enter a positive integer.");
                    System.out.println(str);
                }else{
                    bool = false;
                }
            }catch (NumberFormatException e){
                System.out.println("Invalid input. Please enter a positive integer.");
                System.out.println(str);
            }
        }
        return i;
    }
    
    //rounds a number to the nearest 0.25
    public double roundToQuarter(double d){
        double ans = Math.round(d * 4) / 4.0;
        //if it rounds to 0, return the original number
        if (ans == 0)
            return d;
        else
            return ans;
    }
    
    //separate the ingredients into their rightful arrays
    public void separateIngredientsList(String list){
        String[] listSplit = list.split("\\s+"); 
        addAmount(listSplit[0]);
        //if the second word in the line is in the possibleUnit array, add that word to the unit array
        //otherwise, add nothing
        //this case helps with ingredients such as egg where the second word isn't an unit but an ingredient
        if (Arrays.asList(possibleUnit).contains(listSplit[1])){
            unit.add(listSplit[1]);
        }else{
            unit.add("");
        }
        addIngredient(listSplit);
    }
    
    //convert the amount into a fraction and adds it to the amount array
    public void addAmount (String a){
        //if the amount is a fraction
        if (a.contains("/")){
            String[] slash = a.split("/");
            //if the fraction is mixed
            if (a.contains("-")){
                String[] dash = slash[0].split("-");
                MixedFraction mixed = new MixedFraction(Integer.parseInt(dash[0]), Integer.parseInt(dash[1]), Integer.parseInt(slash[1]));
                amount.add(mixed.toImproper());
            }else{
                amount.add(new Fraction(Integer.parseInt(slash[0]), Integer.parseInt(slash[1])));
            }
        //if the amount is a whole number
        }else{
            amount.add(new Fraction(Integer.parseInt(a), 1));
        }
    }
    
    //add the ingredient name to the ingredient array
    public void addIngredient (String a []){
        int index = 0;
        String nameIngredient = "";
        for (int i = 0; i < unit.size(); i++){
            if (unit.get(i).isEmpty()) //if the unit array is empty, we know that the second word in the line
                                       //would be the ingredient name
                index = 1;
            else //otherwise the 3rd word onwards is the ingredient name
                index = 2;
        }
        //creates new array filled only with the ingredient name
        String [] ingredientName = Arrays.copyOfRange(a, index, a.length);
        for (int i = 0; i < ingredientName.length-1; i++){
            nameIngredient += ingredientName[i] + " ";
        }
        nameIngredient += ingredientName[ingredientName.length-1];
        ingredient.add(nameIngredient);
    }
    
    //calculate new amounts by multiplying the original by the conversion factor
    public void newAmount (Fraction cf){
        for (int i = 0; i < amount.size(); i++){
            adjustedAmount.add(amount.get(i).multiply(cf).reduce());
        }
    }
    
    //converts the ingredient amount to a simpler size measurement
    //for example 16 tbsp = 1 cup so if the recipe calls for 16 tbsp, it will output as a cup instead
    public void conversion (ArrayList<Fraction> a, ArrayList<String> u){
        for (int i = 0; i < a.size(); i++){
            double n = a.get(i).toDecimal();
            
            //if the number is greater than 0.5 and the decimal part of the fraction is divisible by 0.25 and the rounded number doesn't round to equal
            //the original number, round the fraction for a nicer output for an easier to understand measurement
            double round = roundToQuarter(n);
            double og = roundToQuarter(amount.get(i).toDecimal());
            if (round > 0.5 && ((round-(int)round) % 0.25) == 0 && round != og){
                Fraction rounded = Fraction.decimalToFraction(round);
                a.set(i, rounded);
            }
            
            //1 cup = 48 tsp
            if (n<0.25 && (u.get(i).equals("cup")||u.get(i).equals("cups"))){
                Fraction frac = Fraction.decimalToFraction(roundToQuarter(a.get(i).multiply(48).toDecimal()));
                u.set(i, "teaspoons");
                a.set(i, frac);
            }
            
            //if the n value is less than 3, don't bother converting because keeping it as its current unit is easier to read
            if (n > 3){
                //3 tsp = 1 tbsp
                //if the remainder of n divided by 3 is between 0 and 0.5 and the unit is tsp
                if (n%3>=0 && n%3<0.5 && (u.get(i).equals("teaspoon")||u.get(i).equals("teaspoons"))){
                    //divide the fraction by 3 to get new amount
                    Fraction frac = Fraction.decimalToFraction(roundToQuarter(a.get(i).divide(3).toDecimal()));
                    //fixes grammar
                    if (roundToQuarter(a.get(i).divide(3).toDecimal()) <= 1)
                        u.set(i, "tablespoon");
                    else
                        u.set(i, "tablespoons");
                    a.set(i, frac);
                }
                //16 tbsp = 1 cup
                //if the remainder of n divided by 4 is between 0 and 4 and the unit is tbsp
                else if (n%4>=0 && n%4<4 && (u.get(i).equals("tablespoon")||u.get(i).equals("tablespoons"))){
                    //divide the fraction by 16 to get new amount
                    Fraction frac = Fraction.decimalToFraction(roundToQuarter(a.get(i).divide(16).toDecimal()));
                    //fixes grammar
                    if (roundToQuarter(a.get(i).divide(16).toDecimal()) <= 1)
                        u.set(i, "cup");
                    else{
                        u.set(i, "cups");
                    }
                    a.set(i, frac);
                }
            }
        }
    }
    
    public String chooseRecipe() throws IOException {
        //asks the user which recipe they want to see
        Scanner s = new Scanner(System.in);
        System.out.println("Which recipe are you using today?");
        System.out.println("Enter a for chocolate chip cookies.");
        System.out.println("Enter b for brownies.");
        System.out.println("Enter c to input your own recipe.");
        String recipeName = "";
        
        boolean bool = true;
        while (bool){
            recipeName = s.nextLine();
            //while they don't select a valid response, keep asking them again
            if (!recipeName.equalsIgnoreCase("a") && !recipeName.equalsIgnoreCase("b") && !recipeName.equalsIgnoreCase("c")){
                System.out.println("Invalid input. Please enter the letter a, b or c.");
                System.out.println("Which recipe are you using today? ");
            }else{
                bool = false;
            }
        }
        
        //if they enter a, the recipe is chocolate chip cookies
        if (recipeName.equalsIgnoreCase("a")){
            recipeName = "Chocolate Chip Cookies";
        //if they enter b, the recipe is brownies
        }else if (recipeName.equalsIgnoreCase("b")){
            recipeName = "Brownies";
        //if they enter c, proceed to ask them a series of questions so they can create their own recipe
        }else{
            System.out.print("What is your recipe's name? ");
            recipeName = s.nextLine();
            //create a new file with their recipe
            PrintWriter newRecipeFile = new PrintWriter(recipeName + ".txt");
            newRecipeFile.println(recipeName);
            
            int numServings = catchIntError("How many servings does your recipe have? ");
            int numIngredients = catchIntError("How many ingredients does your recipe have? ");
            newRecipeFile.println("SERVINGS: " + numServings);
            newRecipeFile.println("~INGREDIENTS~");
            
            for (int i = 0; i < numIngredients; i++){
                int ingredientNum = i + 1;
                //user inputs their ingredient amount as an improper fraction
                //originally, I had the user input their amount as a decimal but I was having some major issues
                //with numbers such as 0.333 and how the program would recognize it as 1/3 instead of 333/1000
                //in the end, to have a nice output, I decided to change it so the user has to input a numerator and denominator
                //while this takes a lot more time, it gets rid of the errors that occur in the output file if they
                //input a decimal with the intent of it being non-terminating
                System.out.println("Please enter your ingredient's amount as an improper fraction.");
                int num = catchIntError("Enter ingredient #" + ingredientNum + "'s amount's numerator.");
                int denom = catchIntError("Enter ingredient #" + ingredientNum + "'s amount's denominator.");
                //create a string of the fraction the user inputted
                String fracAmt = new Fraction(num,denom).reduce().display();
                
                //ask the user for the unit of measurement
                System.out.println("Enter your ingredient #" + ingredientNum + "'s full unit of measurement name (ex. teaspoon not tsp)");
                System.out.println("If there is no unit (ex. eggs) leave it blank and press enter.");
                String unit = s.nextLine().toLowerCase();
                
                //ask the user for their ingredient's name
                System.out.println("Enter your ingredient #" + ingredientNum + "'s ingredient name");
                String ingredientName = s.nextLine().toLowerCase();
                
                //print their ingredient list into the new file
                if (unit.isEmpty()) //if no unit
                    newRecipeFile.println(fracAmt + " " + ingredientName);
                else //if there is a unit
                    newRecipeFile.println(fracAmt + " " + unit + " " +  ingredientName);
            }
            newRecipeFile.close();
        }
        return recipeName;
    }
    
    public static void main(String[] args) throws IOException {
        RecipeAdjustmentCalculator rac = new RecipeAdjustmentCalculator();
        //reads the original recipe file chosen
        FileReader originalRecipeFile = new FileReader (rac.chooseRecipe() +".txt");
        Scanner scan = new Scanner (originalRecipeFile);
        //the first line of the recipe is the title
        String title = scan.nextLine();
        //creates the output file
        PrintWriter newRecipeFile = new PrintWriter(title + " (Adjusted).txt");
        
        //adds the title of the recipe to the adjusted recipe
        newRecipeFile.println(title);
        
        //finds out how many servings the current recipe has
        String servings = scan.nextLine();
        String[] splitServings = servings.split("\\s+");
        int numServings = Integer.parseInt(splitServings[1]);
        
        //ask the user how many new servings they want
        Scanner input = new Scanner(System.in);
        System.out.println("Your recipe currently makes " + numServings + " servings.");
        int newServingSize = rac.catchIntError("How many servings would you like to make? ");
        input.close();
        
        //add the new serving count to the adjusted recipe
        newRecipeFile.println("SERVINGS: " + newServingSize);
        //adds the ingredient heading to the adjusted recipe
        newRecipeFile.println("~INGREDIENTS~");
        //calculate the conversion factor
        Fraction conversionFactor = new Fraction(newServingSize, numServings).reduce();
        
        //skip the ingredient heading in the original recipe
        scan.nextLine();
        int numIngredients = 0; //number of ingredients in the list  
        //iterate through the rest of the file
        while(scan.hasNext()) { //looks at the ingredient
            rac.separateIngredientsList(scan.nextLine());
            numIngredients += 1;          
        }
        rac.newAmount(conversionFactor);
        rac.conversion(rac.adjustedAmount,rac.unit);
        
        //outputs the new ingredient sizes into the new file  
        for (int i = 0; i < numIngredients; i++){
            String newUnit = rac.unit.get(i);
            String newIngredient = rac.ingredient.get(i);
            if (newUnit.isEmpty()) //if there is no unit
                newRecipeFile.println(rac.adjustedAmount.get(i).reduce().display() + " " + newIngredient);
            else //if there is a unit
                newRecipeFile.println(rac.adjustedAmount.get(i).reduce().display() + " " + newUnit + " " + newIngredient);
        }
        newRecipeFile.close();
    }
}