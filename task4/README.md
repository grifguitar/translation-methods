# Лабораторная работа 4. Генератор трансляторов.

Отчет подготовил студент группы М3337, Хлытин Григорий.

### Задание.

Необходимо написать некоторый упрощенный аналог генератора трансляторов. Рекомендуется брать за основу синтаксис ANTLR
или Bison. Рекомендуется для чтения входного файла с грамматикой сгенерировать разборщик с помощью ANTLR или Bison.

Необходимо набрать в сумме хотя бы 35 баллов.

Обязательное требование: должен быть лексический анализатор, не должно быть ограничения, что токен это один символ.

Необходимо из каждого пункта выполнить хотя бы 1 вариант.

#### Выбор класса грамматик:

* (10 баллов) LL(1)-грамматики, нисходящий разбор
* (15 баллов) SLR-грамматики, восходящий разбор
* (20 баллов) LALR-грамматики, восходящий разбор

#### Работа с атрибутами:

* (10 баллов) поддержка синтезируемых атрибутов
* (10 баллов) поддержка наследуемых атрибутов

#### Тестирование получившегося генератора:

* (обязательно) сгенерировать с помощью вашего генератора калькулятор
* (5 баллов) выполнить с помощью вашего генератора ваше задание второй лабораторной
* (10 баллов) выполнить с помощью вашего генератора ваше задание третьей лабораторной

### Разработка файла с грамматикой 'grammar.gk':

```haskell
-----TERMINAL-----
LPAREN:'('
RPAREN:')'
ADD:'+'
SUB:'-'
MUL:'*'
DIV:'/'
NEG:'~'
VAR:'\regex[0-9]+'
-----STATE-----
E  {val:synth}
E' {val:synth, acc:inherit}
T  {val:synth}
T' {val:synth, acc:inherit}
F  {val:synth}
U  {val:synth}
-----GRAMMAR-----
E  -> T {E'_3.acc:=[T_1.val]} E' {E_0.val:=[E'_3.val]}
E' -> ADD T {E'_4.acc:=[E'_0.acc,T_2.val,+]} E' {E'_0.val:=[E'_4.val]}
E' -> SUB T {E'_4.acc:=[E'_0.acc,T_2.val,-]} E' {E'_0.val:=[E'_4.val]}
E' -> EPS {E'_0.val:=[E'_0.acc]}
T  -> F {T'_3.acc:=[F_1.val]} T' {T_0.val:=[T'_3.val]}
T' -> MUL F {T'_4.acc:=[T'_0.acc,F_2.val,*]} T' {T'_0.val:=[T'_4.val]}
T' -> DIV F {T'_4.acc:=[T'_0.acc,F_2.val,/]} T' {T'_0.val:=[T'_4.val]}
T' -> EPS {T'_0.val:=[T'_0.acc]}
F  -> NEG U {F_0.val:=[U_2.val,neg]}
F  -> U {F_0.val:=[U_1.val]}
U  -> VAR {U_0.val:=[VAR_1.VALUE,strToInt]}
U  -> LPAREN E RPAREN {U_0.val:=[E_2.val]}
-----OPERATION-----
strToInt :: String -> Integer
String x = firstArg;
Integer result = Integer.parseInt(x);
;
+ :: Integer -> Integer -> Integer
Integer x = firstArg;
Integer y = secondArg;
Integer result = x + y;
;
- :: Integer -> Integer -> Integer
Integer x = firstArg;
Integer y = secondArg;
Integer result = x - y;
;
* :: Integer -> Integer -> Integer
Integer x = firstArg;
Integer y = secondArg;
Integer result = x * y;
;
/ :: Integer -> Integer -> Integer
Integer x = firstArg;
Integer y = secondArg;
Integer result = x / y;
;
neg :: Integer -> Integer
Integer x = firstArg;
Integer result = -x;
;
&& :: Boolean -> Boolean -> Boolean
Boolean x = firstArg;
Boolean y = secondArg;
Boolean result = x && y;
;
|| :: Boolean -> Boolean -> Boolean
Boolean x = firstArg;
Boolean y = secondArg;
Boolean result = x || y;
;
^ :: Boolean -> Boolean -> Boolean
Boolean x = firstArg;
Boolean y = secondArg;
Boolean result = x ^ y;
;
! :: Boolean -> Boolean
Boolean x = firstArg;
Boolean result = !x;
;
-----
```

## Запуск.

Запустим проект на тестовом примере:

_6 / 5 - 4 * (2 + 3)_

Получим **.dot** файл, описывающий дерево разбора. Визуализируем граф с помощью Graphviz Online:

![image.png](https://github.com/grifguitar/translation-methods/blob/main/task4/image.png)