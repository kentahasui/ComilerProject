program modTest3 (input, output);
var
  w, x, y, z: integer;
 
begin
  w := 0;
  while w = 0 do
    begin
      read(x);
      read(y);
      z := x mod y;
      write(z)
    end
end.