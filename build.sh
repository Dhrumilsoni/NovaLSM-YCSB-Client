

#SRC="jdbc/src/main/java/com/yahoo/ycsb/db"
#MEMSRC="jdbc/src/main/java/com/meetup/memcached"
END="$1"
#workloads="$2"
user="$2"
experiment_name="$3"
project_name="$4"

suffix="${experiment_name}.${project_name}-PG0.apt.emulab.net"
HOME="/tmp/NovaLSM-YCSB-Client"


#suffix="edbt.BG.emulab.net"
# CLIENT_NODE="node-0"

for ((i=0;i<END;i++)); do
    echo "building server on node $i"

  echo "sudo rm -rf $HOME"
  ssh -oStrictHostKeyChecking=no $user@node-$i.$suffix "sudo rm -rf $HOME"
  echo "git clone https://github.com/Dhrumilsoni/NovaLSM-YCSB-Client.git"
  ssh -oStrictHostKeyChecking=no $user@node-$i.$suffix "cd /tmp && git clone https://github.com/Dhrumilsoni/NovaLSM-YCSB-Client.git"
  echo "cd $HOME && mvn -pl com.yahoo.ycsb:jdbc-binding -am clean package -DskipTests"
	ssh -oStrictHostKeyChecking=no $user@node-$i.$suffix "cd $HOME && mvn -pl com.yahoo.ycsb:jdbc-binding -am clean package -DskipTests" > jbuild-node-$i &
done

sleep 30
echo "Check the files manually"
# todo: if nothing else todo
#for ((i=0;i<END;i++));
#do
#	while [ grep -c "BUILD SUCCESS" jbuild-node-0 == "0" ]
#	do
#		((sleepcount++))
#		sleep 10
#		echo "waiting for node-$i "
#	done
#	echo "setup-all done for node-$i"
#
#done


# scp rdma_bench_o2m haoyu@node-0.Nova.bg-PG0.apt.emulab.net:$HOME/
# scp rdma_bench_m2m haoyu@node-0.Nova.bg-PG0.apt.emulab.net:$HOME/
# scp nova_mem_server haoyu@node-0.Nova.bg-PG0.apt.emulab.net:$HOME/
