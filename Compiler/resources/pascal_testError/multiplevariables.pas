program multipleVariables (input, output);
 var a, b, a : integer;
     x, y : array [1..5] of real;
  procedure one (i, j : integer; k : array [1..5] of real);
    var n, b, c: integer;
    begin
      b := 4;
      n := i + j;
      k[n] := 2.345
    end
  begin
    a := 1;
    b := 2;
    c := 7;
    one(a,b,x);
    write(x[a+b])
  end.