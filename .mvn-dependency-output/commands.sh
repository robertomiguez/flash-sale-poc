cd /home/miguez/Projects/flash-sale-poc
mvn -q dependency:list -DoutputFile=.mvn-dependency-output/dependency-list.txt
mvn -q dependency:tree -DoutputType=text -DappendOutput=true -DoutputFile=.mvn-dependency-output/dependency-tree.txt
