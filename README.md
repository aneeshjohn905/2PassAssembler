# Two-Pass Assembler

## Overview

This project implements a Two-Pass Assembler in Java with a graphical user interface (GUI) using Swing. The assembler reads assembly language source code and an opcode table, generates an intermediate file, a symbol table, and an object code output.

## Features

- **Two-Pass Assembling**: The assembler processes the source code in two passes:
  - **First Pass**: Generates the symbol table and intermediate code.
  - **Second Pass**: Converts the intermediate code into object code.
- **Graphical User Interface**: A user-friendly GUI for easy interaction.
- **File Handling**: Allows users to browse and select input files for the assembly code and opcode table.
- **Output Display**: Shows the intermediate code, symbol table, and object code in the GUI.

## Requirements

- Java Development Kit (JDK) 8 or higher
- IDE for Java development (e.g., IntelliJ IDEA, Eclipse)

##Example input

START 1000
FIRST STL  ALPHA\
\-      WORD  10\
ALPHA RESW  1\
\-      END  FIRST\

##Example Optab

STL  14
WORD 00
RESW 02
RESB 03


