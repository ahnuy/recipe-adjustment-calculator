
package recipeadjustmentcalculator;

public class MixedFraction extends Fraction{
    //FIELDS
    int whole;
    
    //CONSTRUCTOR
    public MixedFraction(int w, int n, int d) {
        super(n,d);
        this.whole = w;
    }
    
    //METHODS
    public double toDecimal(){
        return (this.whole + (double)this.numerator/this.denominator);
    }
    
    public String display(){
        if (numerator == 0){
            return Integer.toString(whole);
        }else{
            return Integer.toString(whole) + "-" + Integer.toString(numerator) + 
                "/" + Integer.toString(denominator);
        }
    }
    
    public Fraction toImproper(){
        int n = this.numerator + this.whole * this.denominator;
        return new Fraction (n, this.denominator);
    }
}