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
    
    // 1+2+3 equals (1+2)+3
    @Override 
    public String toString(){        
        return left.toString() + " + " + right.toString(); 
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
    
    // d/dx[a+b] = d/dx[a] + d/dx[b]
    @Override
    public Expression diff(Variable dVar){
        return new Sum(left.diff(dVar), right.diff(dVar));
    }

    @Override
    public Expression simplify() {
        if (this.getLeft().getExpressionType().equals("number") && this.getRight().getExpressionType().equals("number")){
            return new Number(this.getLeft().getValue() + this.getRight().getValue());
        } else if (this.getLeft().getExpressionType().equals("number") && this.getLeft().getValue() == 0){
            return this.getRight().simplify();
        } else if (this.getRight().getExpressionType().equals("number") && this.getRight().getValue() == 0){
            return this.getLeft().simplify();
        } else {
            Expression simplifiedExpression = new Sum(this.getLeft().simplify(), this.getRight().simplify());
            if (simplifiedExpression.equals(this)){
                return simplifiedExpression;
            } else {
                return simplifiedExpression.simplify();
            }
        }
    }
    
    private void checkRep(){
        assert (left != null && right != null);
    }

}
