all:
	javac Code/sender.java
	python Code/script.py

clean:
	rm -r *.class