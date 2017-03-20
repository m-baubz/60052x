package expressivo;

import java.util.Objects;

/**
 * represents an expression which is a sum of two other expressions, left and right.
 */
public class Sum implements Expression {
    private final Expression left;
    private final Expression right;
    
    public Sum(Expression left, Expression right){
        this.left = left;
        this.right = right;
        checkRep();
    }

    @Override
    public String getExpressionType() {
        return "sum";
    }

    @Override
    public double getValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Expression getLeft() {
        return left;
    }

    @Override
    public Expression getRight() {
        return right;
    }
    
    // add parens if left  is a Sum - to preserve structure and ensure that for all e:Expression, e.equals(Expression.parse(e.toString())).
    @Override 
    public String toString(){
        String leftString = (left.getExpressionType() == "sum") ? "(" + left.toString() + ")" : left.toString();
        return leftString + " + " + right.toString(); 
    }
    
    /**
     * @param thatObject any object
     * @return true if and only if this and thatObject are instances of Sum and their left and right parts are equal
     */
    @Override
    public boolean equals(Object thatObject){
        return (thatObject instanceof Sum) && (left.equals(((Sum)thatObject).getLeft())) && (right.equals(((Sum)thatObject).getRight()));
    }
    
    @Override
    public int hashCode(){
        return Objects.hash(this.getExpressionType(), this.getLeft().hashCode(), this.getRight().hashCode());
    }
    
    private void checkRep(){
        assert (left != null && right != null);
    }
}
