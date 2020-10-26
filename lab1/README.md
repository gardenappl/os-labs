# Task 1

**Variant 7**: Use Java, processes, named pipes (preferably on Linux). Create the pipe externally using system tools.

* Program exits immediately on ESC press, or initiates 15 second countdown when 'q' is pressed.
* **Bonus**: Generalized to arbitrary binary operations (using Java lambdas).
* **Bonus**: Generalized to n functions (though the setup script only creates 6 sets of pipes).

This program is written for Linux only, and requires Java 8.

Relies on the IntOps class provided by the `lab1.jar` library.

### Testing

First, build the .class files, they should be in `out/production/lab1`.

Then, run this:

```
setup.sh
java -classpath 'out/production/lab1/' ua.yuhrysh.oslab1.manager.Main
```

(do this in a separate terminal as your IDE's builtin terminal might not support it)
