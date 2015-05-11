program modError (input, output);
var i, j, k : integer;
    a, b, c : real;
begin
  i := 4;
  j := 15;
  k := j mod i;
  write(k);
  a := i;
  b := j;
  c := i mod j;
  c := j mod a;
  c := b mod j;
  c := b mod a;
  write(c)
end.