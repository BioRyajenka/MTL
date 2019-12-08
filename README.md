Компилятор, дебаггер (с визуализацией ленты) и оптимайзер для машины тьюринга. Делался для упрощения написания лабораторок по АСД на 3 курсе бакалавриата.<br/>
Язык представляет из себя более высокоуровневую абстракцию над ДМТ, но имеет некоторые ограничения.

Compiler, debugger (with visualization of turing tape) and optimizer for turing machine. Created to complete my homeworks and because it is too boring to write pure turing machine instructions by hand. :) 

Here is example of MTL syntax:
```
def toleft
	cycle
		ifchar _
			break
		<

def toright
	cycle
		ifchar _
			break
		>

def main
	toright
	setchar $
	toleft
	>

	// comments are available!
	cycle
		...
```
