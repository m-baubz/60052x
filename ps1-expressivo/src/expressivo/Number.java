package expressivo;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * represents a basic expression that contains a single non negative number.
 * (!) currently the type contains both a string representationa and a numeric value of type double.
 * might be preferable to move the type transformations to Simplify and Differential and keep only string rep here.
 */
public class Number implements Expression {
    private final String stringValue;
    private final double value;
    
    public Number(String stringValue){
        this.stringValue = stringValue;
        this.value = Double.parseDouble(stringValue);
        checkRep();
    }
    
    public Number(double value){
        DecimalFormat df = new DecimalFormat("#.#"); //to avoid sci notation
        df.setMaximumFractionDigits(340); //340 = DecimalFormat.DOUBLE_FRACTION_DIGITS        
        this.stringValue = df.format(value);
        this.value = value;
        checkRep();
    }

    @Override
    public String getExpressionType() {
        return "number";
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public Expression getLeft() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Expression getRight() {
        throw new UnsupportedOperationException();
    }
    
    @Override 
    public String toString(){
        return stringValue; 
    }
    
    /**
     * @param thatObject any object
     * @return true if and only if this and thatObject are instances of Number and have equal value
     * equal Numbers may have different stringValue ("1.0" vs "1" for example)
     */
    @Override
    public boolean equals(Object thatObject){
        if (!(thatObject instanceof Number)){
            return false;
        } else {
            return (this.getValue() == ((Number)thatObject).getValue());
        }
    }
    
    @Override
    public int hashCode(){
        return Objects.hashCode(this.getValue());
    }
    
    @Override
    public Expression diff(Variable dVar){
        return new Number(0);       
    }
    
    //returns a copy of itself
    @Override
    public Expression simplify() {
        return new Number(this.getValue());
    }
    
    private void checkRep(){
        assert Pattern.compile("([0-9]*\\.)?[0-9]+").matcher(this.stringValue).matches();
        assert (this.value >= 0);
    }


}
