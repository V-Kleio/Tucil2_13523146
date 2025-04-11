echo Removing compiled files...
if exist bin rmdir /s /q bin
mkdir bin

echo Compiling java code...
dir /s /b src\*.java > sources.txt
javac -d bin\ @sources.txt
del sources.txt

echo Running the main program...
java -cp bin Main