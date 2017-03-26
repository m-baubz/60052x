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
        String leftString = (left.getExpressionType() == "sum") ? "(" + left.toString() + ")" : left.toString(); 
        String rightString = (right.getExpressionType() == "sum") ? "(" + right.toString() + ")" : right.toString();
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
    
    // d/dx[a * b] = a * d/dx[b] + b * d/dx[a]
    @Override
    public Expression diff(Variable dVar){
        Expression leftDRight = new Product(left, right.diff(dVar));
        Expression rightDLeft = new Product(right, left.diff(dVar));
        
        return new Sum(leftDRight, rightDLeft);
    }
    
    @Override
    public Expression simplify() {
        if (this.getLeft().getExpressionType().equals("number") && this.getRight().getExpressionType().equals("number")){
            return new Number(this.getLeft().getValue() * this.getRight().getValue());
        } 
        if (this.getLeft().getExpressionType().equals("number")){
            if (this.getLeft().getValue() == 0){
                return new Number(0);
            }
            if (this.getLeft().getValue() == 1){
                return this.getRight().simplify();
            }
        } 
        if (this.getRight().getExpressionType().equals("number")){
            if (this.getRight().getValue() == 0){
                return new Number(0);
            }
            if (this.getRight().getValue() == 1){
                return this.getLeft().simplify();
            }        
        }
        Expression simplifiedExpression = new Product(this.getLeft().simplify(), this.getRight().simplify());
        if (simplifiedExpression.equals(this)){
            return simplifiedExpression;
        } else {
            return simplifiedExpression.simplify();
        }        
    }
    
    private void checkRep(){
        assert (left != null && right != null);
    }
}
