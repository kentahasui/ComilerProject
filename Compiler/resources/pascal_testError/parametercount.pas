program procError (input, output);
 var a, b, c: integer;
     x : array [1..5] of real;
  procedure one (i, j : integer; k : array [1..5] of real);
    var n : integer;
    begin
      n := i + j;
      k[n] := 2.345
    end
  begin
    a := 1;
    b := 2;
    one(a,b,x,c);
    write(x[a+b])
  end.