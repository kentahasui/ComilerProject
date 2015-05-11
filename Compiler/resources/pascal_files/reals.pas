program realArithmetic (input, output);
var i : real;
begin
  i := 5 * (1 + 2 + 3.1 - 4.3 + 0.2) / 4;
  write(i);
  i := 400 / 7;
  write(i);
  
  {Both Reals}
  i := 100.0 + (-200.0);
  write(i);
  i := (-1.5) * 20.2;
  write(i);
  i := -40.2 + 39.8;
  write(i);
  {First is real, second is int}
  i := 1.7 * 39;
  write(i);
  i := 200 + (-9.9);
  write(i);
  {First is int, second is real}
  i := 6 / 1.5;
  write(i);
  i := 20 - 9.9;
  write(i)
end.