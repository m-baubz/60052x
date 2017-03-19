package expressivo;

import java.util.Objects;

/**
 * represents an expression which is a sum of two other expressions, left and right.
 */
public class Product implements Expression {
    private final Expression left;
    private final Expression right;
    
    public Product(Expression left, Expression right){
        this.left = left;
        this.right = right;
        checkRep();
    }

    @Override
    public String getExpressionType() {
        return "product";
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
    
    @Override
    public String toString(){
        // add parens if either left or right expression is a Sum i.e. has lower precedence
        // or if left  is a Product - to preserve structure and ensure that for all e:Expression, e.equals(Expression.parse(e.toString())).
        String leftString = (left.getExpressionType() == "sum") || (left.getExpressionType() == "product") ? 
                                left.toString() : "(" + left.toString() + ")"; 
        String rightString = (right.getExpressionType() == "sum") ? right.toString() : "(" + right.toString() + ")";
        return leftString + " * " + rightString;
    }
    
    /**
     * @param thatObject any object
     * @return true if and only if this and thatObject are instances of Product and their left and right parts are equal
     */
    @Override
    public boolean equals(Object thatObject){
        return (thatObject instanceof Product) && (left.equals(((Product)thatObject).getLeft())) && (right.equals(((Product)thatObject).getRight()));
    }
    
    @Override
    public int hashCode(){
        return Objects.hash(this.getExpressionType(), this.getLeft().hashCode(), this.getRight().hashCode());
    }
    
    private void checkRep(){
        assert (left != null && right != null);
    }
}
