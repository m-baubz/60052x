package expressivo;
import lib6005.parser.*;
import java.io.*;
import java.util.Scanner;

class ParserSandbox {
    
    enum IntegerGrammar {ROOT, SUM, PRIMITIVE, NUMBER, WHITESPACE};
    
    public static void main(String[] args) throws UnableToParseException, IOException{
        String text = "";
        Scanner userInput = new Scanner(System.in);

        do{
            System.out.println("Enter somethings: ");
            text = userInput.nextLine();
            Parser<IntegerGrammar> parser = GrammarCompiler.compile(new File("src/expressivo/ExpressionTest.g"), IntegerGrammar.ROOT);
            ParseTree<IntegerGrammar> tree = parser.parse(text);
            tree.display();
        } while (!(text.equals("00")));
        userInput.close();

    }
    
}
