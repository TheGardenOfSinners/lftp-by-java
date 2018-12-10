运行平台:windows7/8/10
必须工具:Java version "1.8.0_121"以上

Server端：
     进入src/Server文件夹。
     双击bulid.bat进行编译(包里已经编译好了 可跳过这一步)
     双击run.bat(或者在命令行输入java Work)运行。

Client端:
     进入src/Client文件夹。
     双击bulid.bat进行编译(包里已经编译好了 可跳过这一步)
     运行lsend:
       打开命令提示符，输入 java Work lsend ip filename
       (如java Work lsend 127.0.0.1 ./Data/0001.jpg)
       也可以双击 run_lsend.bat运行。
     运行lget:
       打开命令提示符，输入 java Work lget ip filename
       (如java Work lsend 127.0.0.1 0002.jpg)
       也可以双击 run_lget.bat运行。