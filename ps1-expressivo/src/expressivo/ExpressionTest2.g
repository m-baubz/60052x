root ::= expression;

@skip whitespace{
	expression ::= primitive | sum | product;
	
	// sum is defined as 2 or more arguments separated by +
	sum ::= (primitive | product)('+' (primitive | product))+;
	
	//product is defined as 2 or more arguments separated by *
	product ::= primitive ('*' primitive)+;
	
	primitive ::= variable | number | '(' expression ')';
}
number ::= [0-9]+('.'[0-9]+)?;
variable ::= [A-Za-z]+;
whitespace ::= [ \t\r\n]+;
