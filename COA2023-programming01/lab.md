# Lab1
## 1.usage of the functions listed
### Integer.parseInt(String s)
> Returns a new int initialized to the value represented by the specified String, as done by the valueOf method of class Integer.

### Float.parseFloat(String s)
> Returns a new float initialized to the value represented by the specified String, as done by the valueOf method of class Float.

### String.valueOf(int i)
> Returns the string representation of the int argument.

### String.valueOf(float f)
> Returns the string representation of the float argument.

## 2.go into the details we need to implement
### 1. convert a decimal integer to binary complement
> 1.1. convert the decimal integer to binary integer
> 1.2. convert the binary integer to binary complement
> 1.3. return the binary complement
 so I decided to implement a function to do 1.1 and one to do 1.2
I name them decimalToBinary and binaryToComplement respectively

### 2. convert a binary complement to decimal integer
it's much more easy than 1

### 3. convert a decimal integer to NBCD
> 3.1 cut the integer alone
> 3.2 convert the integer to NBCD
> 3.3 return the NBCD

### 4. convert a NBCD to decimal integer
> 4.1 cut the NBCD alone by four
> 4.2 convert the NBCD to decimal integer
> 4.3 return the decimal integer

### 5. convert a decimal float to binary float
> 5.1 confirm the sign of the float
> 5.2 convert the integer part of the float to binary integer
> 5.3 convert the decimal part of the float to binary integer

### 6. convert a binary float to decimal float
> 6.1 confirm the sign of the float
> 6.2 convert the integer part of the float to decimal integer
> 6.3 convert the decimal part of the float to decimal integer

## 3. the functions I implemented
after I finished my 6 functions , and passed the few tests in the file, I "was" happy
to push the git. However ,the score of 40 shocked me and bring me to self-testament.

During my debugging process, I found my tool function are not working normally,
which informs me the importance of *testing tool functions and describing it clearly.*

What's more, as a lazy man, I tend to call functions instead of implement it again and again,
but it turns out that my function doesn't fit every situation, which leads to a lot of problems.