Place the MySQL JDBC driver jar in this folder.

Recommended filename
- mysql-connector-j.jar

Example Linux/macOS commands
- find src -name "*.java" | xargs javac -cp "lib/mysql-connector-j.jar" -d out
- java -cp "out:lib/mysql-connector-j.jar" ui.Main

If your jar has a versioned filename such as mysql-connector-j-9.3.0.jar,
either rename it to mysql-connector-j.jar or use the exact filename in the
classpath commands.
