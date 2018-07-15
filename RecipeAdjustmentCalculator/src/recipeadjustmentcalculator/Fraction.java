
package recipeadjustmentcalculator;

public class Fraction {
    //FIELDS
    int numerator;
    int denominator; 
    
    //CONSTRUCTOR
    public Fraction(int n, int d) {
        numerator = n;
        denominator = d;
    }
    
    //METHODS
    //reduces the fraction
    public Fraction reduce() {
        int gcd = getGCD(numerator, denominator);
        return new Fraction (numerator/gcd, denominator/gcd);
    } 
    
    //converts the fraction into a decimal
    public double toDecimal() {
        return (double) numerator/denominator; 
    }   
    
    //displays the fraction
    public String display() {
        if (numerator > denominator){
            MixedFraction mixed = toMixedFraction(numerator, denominator);
            return mixed.display();
        }else if (denominator == 1){
            return Integer.toString(numerator);
        }else{
            return Integer.toString(numerator) + "/" + Integer.toString(denominator);
        }
    }
    
    //multiplies two fractions together
    public Fraction multiply(Fraction other){
        return new Fraction(this.numerator*other.numerator,this.denominator*other.denominator);    
    }
    
    //multiples the fraction by a constant
    public Fraction multiply(int constant){
        return new Fraction(this.numerator*constant, this.denominator);
    }
    
    //divides the fraction by a constant
    public Fraction divide (int constant){
        return new Fraction(this.numerator, this.denominator*constant);
    }
    
    //gets the GCD of two numbers
    public static int getGCD(int a, int b){
        if (b == 0) //base case
            return a;
        else //recursive case
            return getGCD(b, a%b); 
    }
    
    //converts a decimal into a fraction
    public static Fraction decimalToFraction(double d){
        double numerator = d;
        int denominator = 1;  
        while (! isWholeNumber(numerator)) {
            numerator *= 10;
            denominator *= 10;
        }
        Fraction frac = new Fraction((int)numerator,denominator);
        return frac.reduce();
    }
    
    //checks if double is a whole number
    private static boolean isWholeNumber(double d){
        return d == Math.round(d);
    }
    
    //converts an improper fraction into mixed
    private MixedFraction toMixedFraction (int n, int d){
        int w = n / d;
        int num = n % d;
        return new MixedFraction (w, num, d);
    }
}