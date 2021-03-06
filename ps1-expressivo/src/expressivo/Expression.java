package expressivo;
import java.io.*;
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
    
    enum ExpressionGrammar {ROOT, EXPRESSION, SUM, PRODUCT, NUMBER, VARIABLE, WHITESPACE};
    
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
     * @throws IOException 
     * @throws UnableToParseException 
     * @throws IllegalArgumentException if the expression is invalid
     */
    public static Expression parse(String input) {
        ParseTree<ExpressionGrammar> tree = null;
        try{
            Parser<ExpressionGrammar> parser = GrammarCompiler.compile(new File("src/expressivo/Expression.g"), ExpressionGrammar.ROOT);
            tree = parser.parse(input);
            
        } catch (UnableToParseException e){
            throw new IllegalArgumentException("invalid input (probably)");
        } catch (IOException e) {
            System.out.println("no find filez :(");
        }
        return buildAST(tree);
    }
        
    public static Expression buildAST(ParseTree<ExpressionGrammar> tree){    
        
        switch(tree.getName()){
        case NUMBER:
            return new Number(tree.getContents());
            
        case VARIABLE:
            return new Variable(tree.getContents());
            
        case SUM:{
            // SUM can have 2 or more children: EXPRESSION, PRODUCT, NUMBER or VARIABLE.
            // code builds a (nested) Sum expression by calling buildAST on every non-whitespace child
            boolean first = true;
            Expression result = null;
            for(ParseTree<ExpressionGrammar> child : tree.children()){
                if (child.getName() == ExpressionGrammar.WHITESPACE){
                    continue;
                } else {
                    if(first){
                        result = buildAST(child);
                        first = false;
                    }else{
                        result = new Sum(result, buildAST(child));
                    }
                }
            }
            if (first) {
                throw new RuntimeException("sum must have a non whitespace child:" + tree);
            }
            return result;
        }
        case PRODUCT:{
            // PRODUCT can have 2 or more children: EXPRESSION, NUMBER or VARIABLE.
            // code builds a (nested) Product expression by calling buildAST on every non-whitespace child
            boolean first = true;
            Expression result = null;
            for(ParseTree<ExpressionGrammar> child : tree.children()){
                if (child.getName() == ExpressionGrammar.WHITESPACE){
                    continue;
                } else {
                    if(first){
                        result = buildAST(child);
                        first = false;
                    }else{
                        result = new Product(result, buildAST(child));
                    }
                }
            }
            if (first) {
                throw new RuntimeException("sum must have a non whitespace child:" + tree);
            }
            return result;
        }
        case EXPRESSION:{
            // EXPRESSION can contain a single child SUM, PRODUCT, NUMBER or VARIABLE.
            // returns buildAST of first non-whitespace child. 
            for(ParseTree<ExpressionGrammar> child : tree.children()){
                if (child.getName() == ExpressionGrammar.WHITESPACE){
                    continue;
                } else {
                    return buildAST(child);                        
                }
            }            
        }            
        case ROOT:
            // ROOT only contains one EXPRESSION
            return buildAST(tree.childrenByName(ExpressionGrammar.EXPRESSION).get(0));
            
        case WHITESPACE:
            throw new RuntimeException("You should never reach here:" + tree);
        }
        throw new RuntimeException("You should never reach here:" + tree);
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
    
    /**
     * differentiate an expression
     * @param dVar - variable to differentiate by.
     * @return expression that represents the derivative d/dVar[original_expression]. does not simplify.
     */
    public Expression diff(Variable dVar);
    
    /**
     * simplify an expression
     * performs the following simplifications (recursively):
     *      sum(num(a), num(b)) -> num(a+b)
     *      sum(expr, num(0)) -> expr
     *      prod(num(a), num(b)) -> num(a*b)
     *      prod(expr, num(0)) -> num(0)
     *      prod(expr, num(1)) -> expr
     * @return simplified expression
     */
    public Expression simplify();
    
    // TODO more instance methods
    
    /* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires permission of course staff.
     */
}