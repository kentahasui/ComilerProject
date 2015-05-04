program NestedWhile (input, output);
var i, j : integer;
begin
  i := 0;
  while i < 10 do
  	begin
  		write(i);
  		j :=0;
  		while(j < 10) do
  			begin
  				write(j);
  				j := j+1
  			end;
  		i := i + 1
  	end
end.