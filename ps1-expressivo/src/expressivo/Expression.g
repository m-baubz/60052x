/* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */

// grammar Expression;

/*
 *
 * You should make sure you have one rule that describes the entire input.
 * This is the "start rule". Below, "root" is the start rule.
 *
 * For more information, see the parsers reading.
 */
root ::= expression;
whitespace ::= [ \t\r\n]+;

@skip whitespace{
	expression ::= variable | number | sum | product | '(' expression ')';
	
	// sum is defined as 2 or more arguments separated by +
	sum ::= (variable | number | '(' expression ')' | product)('+' (variable | number | '(' expression ')' | product))+;
	
	//product is defined as 2 or more arguments separated by *
	product ::= (variable | number | '(' expression ')') ('*' (variable | number | '(' expression ')'))+;	
}
number ::= [0-9]+('.'[0-9]+)?;
variable ::= [A-Za-z]+;