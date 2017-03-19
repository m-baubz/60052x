root ::= sum;
whitespace ::= [ \t\r\n]+;

@skip whitespace{
	sum ::= primitive ('+' primitive)*;
	primitive ::= number | '(' sum ')';
}
number ::= [0-9]+;

