set classpath=.;.\bin;
java -cp %classpath% -Djava.ext.dirs=.\lib -Xmx1024m -Xms1024m -Xmn512m -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:-PrintGC edu.xtu.bio.parallel.InnerDBBuilder
pause