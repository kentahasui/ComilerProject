program simpleTest (input, output);
var i : integer;
    k : array[1..5] of integer;

procedure proc1(a1 : integer; b1: real);
begin
	write(a1);
	write(b1)
end

procedure proc2(a2, b2:real);
begin
	write(a2);
	write(b2)
end

procedure proc3(a3 : integer; b3: real; c3: integer);
begin
	write(a3);
	write(b3);
	write(c3)
end

procedure proc4(a4, b4: integer; c4, d4: real; e4, f4: integer);
begin
	write(a4);
	write(b4);
	write(c4);
	write(d4);
	write(e4);
	write(f4)
end

procedure proc5 (i5, j5 : integer; k5 : array [1..5] of integer);
	var n : integer;
    begin
      write(i5);
      write(j5);
      n := i5 + j5;
      write(n);
      k5[n] := 900;
      write(k5[n])
    end

begin
  i := 10;
  
  proc1(1, 2.7);
  proc2(1.0, 2.0);
  proc3(1, 2.0, 3);
  proc4(1, 2, 3.5, 4.5, 5, 6);
  proc5(1, 2, k);
  write(i)
end.