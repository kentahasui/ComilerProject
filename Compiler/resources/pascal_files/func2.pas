program funcTest(input, output);        
var
        i, j, k:integer;

function Diff(a,b:integer): result integer;
begin
        Diff := a - b
end

begin
        i := 45;
        j := 15;
        k := Diff(i,j);
        write(i);
        {write(i, k);}
        write(k)
end.