program unmatchedParameterTypes (input, output);
 var a, b, c: integer;
     d: real;
     x : array [1..5] of real;
  procedure proc (i, j : integer; k : array [1..5] of real);
    var n : integer;
    begin
      n := i + j;
      k[n] := 2.345
    end
  begin
    a := 1;
    b := 2;
    proc(a,b,d);
    write(x[a+b])
  end.