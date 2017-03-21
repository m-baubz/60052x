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

