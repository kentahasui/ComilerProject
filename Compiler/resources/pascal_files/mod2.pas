program modTest (input, output);
 var 
 	a, b, c, d, e, f, g, h : integer;
 	p : real;
  
 begin
   
   a := 5 mod 7;
   write(a);
   b := 13 mod 7;
   write(b);
   c := - 14; 
   d := 13 mod b;
   write(d);
   e := 12;
   f := a mod e;
   write(f);
   g := 100 mod 20;
   write(g);
   h := 3201 mod 92;
   write(h);
   
   p := -1.3;
   write(p)
  
 end.