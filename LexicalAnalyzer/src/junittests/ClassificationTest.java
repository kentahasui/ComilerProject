package junittests;
import lex.Classification;
import static org.junit.Assert.*;

import org.junit.Test;

/** JUnit tests for the Classification class */
public class ClassificationTest {
	
	
	Classification classification = Classification.getInstance();
	int size = Classification.SIZE;

	/** Tests for the isDigit method */
	@Test
	public void isDigitTest() {
		assertTrue(classification.isDigit('0'));
		assertTrue(classification.isDigit('1'));
		assertTrue(classification.isDigit('2'));
		assertTrue(classification.isDigit('3'));
		assertTrue(classification.isDigit('4'));
		assertTrue(classification.isDigit('5'));
		assertTrue(classification.isDigit('6'));
		assertTrue(classification.isDigit('7'));
		assertTrue(classification.isDigit('8'));
		assertTrue(classification.isDigit('9'));
		
		for(int i = '0'; i<='9'; i++){
			assertTrue(classification.isDigit((char)i));
		}
		
		for(int j = 'a'; j <='z'; j++){
			assertFalse(classification.isDigit((char)j));
		}
		for(int k = 'A'; k <='Z'; k++){
			assertFalse(classification.isDigit((char)k));
		}
		
		for(int l = 0; l < '0'; l++){
			assertFalse(classification.isDigit((char)l));
		}
		
		for(int m = '9'+1; m<size; m++){
			assertFalse(classification.isDigit((char) m));
		}
		assertFalse(classification.isDigit('a'));
	}
	
	/** Tests for the isLetter method */
	@Test
	public void isLetterTest(){
		// Check uppercase letters
		for(int i = 'A'; i<='Z'; i++){
			assertTrue(classification.isLetter((char)i));
		}
		// Check lowercase letters
		for(int j = 'a'; j<='z'; j++){
			assertTrue(classification.isLetter((char)j));
		}
		// Check non-letter values
		for(int k= 0; k<'A'; k++){
			assertFalse(classification.isLetter((char)k));
		}
		for(int m = 'Z'+1; m<'a'; m++){
			assertFalse(classification.isLetter((char)m));
		}
		for(int n = 'z'+1; n<size; n++){
			assertFalse(classification.isLetter((char)n));
		}
	}
	
	/** Tests for the isLetterOrDigit method */
	@Test
	public void isLetterOrDigitTest(){
		for(int i = 0; i<size; i++){
			char ch = (char)i;
			if(classification.isLetter(ch) || classification.isDigit(ch)){
				assertTrue(classification.isLetterOrDigit(ch));
			}
			else{
				assertFalse(classification.isLetterOrDigit(ch));
			}
		}
	}
	
	/** Tests for the operator methods */
	@Test
	public void isOperator(){
		assertTrue(classification.isOperator('='));
		assertTrue(classification.isOperator('<'));
		assertTrue(classification.isOperator('>'));
		assertTrue(classification.isOperator('-'));
		assertTrue(classification.isOperator('+'));
		assertTrue(classification.isOperator('*'));
		assertTrue(classification.isOperator('/'));
		assertFalse(classification.isOperator(' '));
		assertFalse(classification.isOperator('x'));
		assertFalse(classification.isOperator((char)0));
		assertFalse(classification.isOperator((char)127));
		
		assertTrue(classification.isPlusMinus('+'));
		assertTrue(classification.isPlusMinus('-'));
		
		assertTrue(classification.isRelop('='));
		assertTrue(classification.isRelop('<'));
		assertTrue(classification.isRelop('>'));
		
		assertTrue(classification.isMulop('*'));
		assertTrue(classification.isMulop('/'));
		
	}

}
