SET JAVA_HOME=C:\Program Files\Java\jdk1.8.0_66

call cd ../log4j-globitel_v2
call Builder.bat

xcopy "BuildData\log4j-globitel-v2.0.0.jar" "..\template\lib\log4j-common"

call cd ../template

call ant

RD bin /S/Q

pause

