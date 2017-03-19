package expressivo;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * represents a basic expression that contains a single variable
 * variable names are case-sensitive nonempty sequences of letters (e.g. y and Foo)
 */
public class Variable implements Expression {
    private final String name;
    
    public Variable(String name){
        this.name = name;
        checkRep();
    }

    @Override
    public String getExpressionType() {
        return "variable";
    }

    @Override
    public double getValue() {
        throw new UnsupportedOperationException();
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
        return name; 
    }
    
    /**
     * @param thatObject any object
     * @return true if and only if this and thatObject are instances of Variable and have the same name
     */
    @Override
    public boolean equals(Object thatObject){
        return (thatObject instanceof Variable) && (name == ((Variable)thatObject).toString());
    }
    
    @Override
    public int hashCode(){
        return Objects.hashCode(this.toString());
    }
    
    private void checkRep(){
        assert Pattern.compile("[A-Za-z]+").matcher(name).matches();
    }
}
