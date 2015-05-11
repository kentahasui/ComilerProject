program funcTest(input, output);        
var
        i, j, k, l:integer;
        w, x, y, z:real;

function Sum(a,b:integer): result integer;
begin
        Sum := a + b
end

function writeAll(a, b, c: real): result real;
begin
		write(a);
		write(b);
		write(c);
		writeAll := c
end

procedure writeAllProcedure(a, b, c: real);
begin
		write(a);
		write(b);
		write(c)
end

function constantParams(one, two, three: integer): result integer;
begin
		write(one);
		write(two);
		write(three);
		constantParams := one + two + three
end
procedure constantParamsProcedure(one, two, three: integer);
begin
		write(one);
		write(two);
		write(three)
end
procedure testb(a,b:integer; c,d,e,f : real );
begin 
	write(a);
	write(b);
	write(c);
	write(d);
	write(e);
	write(f)
end
begin
        i := 10;
        j := 20;
        k := Sum(i,j) * 2;
        write(i, j, k);
        l := constantParams(1, 2, 3);
        write(l);
        constantParamsProcedure(1, 2, 3);
        
        x := 2.5;
        y := 92.7;
        z := 14.55;
        w := writeAll(x, y, z);
        write(w);
        writeAllProcedure(x, y, z);
        testb(1, 2, x, x, x, x);
        
end.