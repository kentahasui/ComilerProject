program missingSubscript2 (input, output);
var i, j : integer;
    m, n: array[1..5] of integer;
begin
  m[3] := 14;
  m[1] := 2;
  n := m;
  write(m[3]);
  write(n[3]);
  write(n[1]);
  write(n[1])
end.