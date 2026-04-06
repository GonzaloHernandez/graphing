## Getting Started

Graphig is now re compiled and will be improved in the next days.


## Folder Structure

The workspace contains two folders by default, where:

- `src`:     the folder to maintain sources
- `docs`:    the folder to save information and help
- `samples`: the folder to get initial examples
- `icons`:   the folder to store images and icons

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependencies

For Users: 
- Java Runtime: JRE.

For Developers
- Java Developepment Kit JDK.

## Compile and Use

`javac --source-path src/ -d bin src/main/GrapherMain.java`

`java -cp bin main.GrapherMain`


## JAR file ready to use

Graphing-v1.3.10.jar 
[Direct download](https://github.com/GonzaloHernandez/graphing/raw/refs/heads/master/releases/Graphing-v1.3.10.jar)

## Debugger interaction from Python

To send graph data from your Python scripts to the Graphing application, you can use a TCP socket connection. The Graphing session must be open and the "Listen" toggle in the Stock view must be active.

```python
import socket
def send_to_graphing(message):
    host = '127.0.0.1'
    port = 65432
    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.connect((host, port))
            s.sendall((message + "\n").encode('utf-8'))
    except ConnectionRefusedError:
        print("The graphing session listener is not running")
```

Example

```python
send_to_graphing("vertices=[true,false,true]")
send_to_graphing("edges=[true,true,false,true,true]")
send_to_graphing("values=[4,6,1,3,3]")
```
