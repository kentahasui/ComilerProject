program functionTest(input, output); 
var
        i, j, k:integer;

function Func1(a,b:integer): result integer;
  begin
        Func1 := a + b
  end

function Func2(x, y:integer): result integer;
  var n: integer;
  begin
  		n := 300;
        Func2 := Func1(x, y);
        Func2 := Func1(n, n)
  end

begin
        i := 10;
        j := 20;
        k := Func2(i,j);
        write(k)
end.