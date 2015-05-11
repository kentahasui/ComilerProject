
program funcTest(input, output);        
var
        i, j, k:integer;

function Sum(a,b:integer): result integer;
begin
        Sum := a + b
end

begin
        i := 10;
        j := 20;
        k := Sum(i,j) * 2;
        i := Sum(Sum(1, 2), 2);
        write(i);
        write(i, k);
        write(Sum(5, 6));
        write(k)
end.