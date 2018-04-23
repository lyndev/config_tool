Set vbs=CreateObject("Wscript.Shell")
'vbs.Run "run.bat",vbhide
vbs.Run "java -DplanningVersion=""true"" -Dfile.encoding=utf-8 -server -jar ConfigDBTool.jar",vbhide

