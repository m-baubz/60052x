package expressivo;

import lib6005.parser.*;

/**
 * An immutable data type representing a polynomial expression of:
 *   + and *
 *   nonnegative integers and floating-point numbers
 *   variables (case-sensitive nonempty strings of letters)
 * 
 * <p>PS1 instructions: this is a required ADT interface.
 * You MUST NOT change its name or package or the names or type signatures of existing methods.
 * You may, however, add additional methods, or strengthen the specs of existing methods.
 * Declare concrete variants of Expression in their own Java source files.
 */
public interface Expression {
    
    // Datatype definition
    // Expression = Number(double value, String stringValue) +
    //              Variable(String name) +
    //              Add(Expression left, Expression right) +
    //              Multiply(Expression left,Expression right)
    
    /**
     * get type of expression
     * @return type of expression as a string from ['number', 'variable', 'sum', 'product']
     */
    public String getExpressionType();
    
    /**
     * Getters for expression fields.
     * @return  value as double for Number, 
     *          Expressions left and right for Sum and Product.
     * @throws UnsupportedOperationException if called on a type that doesn't have the field in question. 
     */
    public double getValue();
    public Expression getLeft();
    public Expression getRight();
    
    /**
     * Parse an expression.
     * @param input expression to parse, as defined in the PS1 handout.
     * @return expression AST for the input
     * @throws IllegalArgumentException if the expression is invalid
     */
    public static Expression parse(String input) {
        throw new RuntimeException("unimplemented");
    }
    
    /**
     * @return a parsable representation of this expression, such that
     * for all e:Expression, e.equals(Expression.parse(e.toString())).
     * 
     * formatting:  operator symbols separated by spaces.
     *              Sum expression enclosed in parentheses when it comes as argument to a Product expression. 
     */
    @Override 
    public String toString();

    /**
     * @param thatObject any object
     * @return true if and only if this and thatObject are structurally-equal
     * Expressions, as defined in the PS1 handout.
     */
    @Override
    public boolean equals(Object thatObject);
    
    /**
     * @return hash code value consistent with the equals() definition of structural
     * equality, such that for all e1,e2:Expression,
     *     e1.equals(e2) implies e1.hashCode() == e2.hashCode()
     */
    @Override
    public int hashCode();
    
    // TODO more instance methods
    
    /* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires permission of course staff.
     */
}
