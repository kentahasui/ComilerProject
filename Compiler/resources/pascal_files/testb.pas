program funcTest(input, output);        
var
       z:real;

procedure testb(a,b:integer; c,d,e,f : real );
begin 
	write(a);
	write(b);
	write(c);
	write(d);
	write(e);
	write(f)
end
function testbFunc(a,b:integer; c,d,e,f : real ): result real;
begin 
	write(a);
	write(b);
	write(c);
	write(d);
	write(e);
	write(f);
	testbFunc := f
end

begin
	z := testbFunc(100, 200, 10.1, 10.2, 10.3, 10.4);
	testb(100, 200, 10.1, 10.2, 10.3, 10.4)
end.