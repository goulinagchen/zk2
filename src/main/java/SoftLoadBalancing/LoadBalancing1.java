package SoftLoadBalancing;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import lombok.Getter;
import lombok.Setter;
import zookeeper.zk2.ZKBase;

/**
 * 软负载均衡
 * 
 * @author liang E-mail:gouliangchen@163.com
 * @version v1.0 创建时间:2017年12月26日 下午10:08:33
 */
public class LoadBalancing1 extends ZKBase {

	private static final Logger logger = Logger.getLogger(LoadBalancing1.class);
	private static final String SUB_PREFIX = "sub";
	private @Getter @Setter ZooKeeper zk = null;
	private int subCount = 5;
	private List<String> serviceNodeLists = new ArrayList<String>();
	private int serviceNum = 0;

	public static void main(String[] args) throws Exception {
		LoadBalancing1 balancing1 = new LoadBalancing1();
		balancing1.setZk(balancing1.startZK());
		Thread.sleep(3000);
		for (int i = 1; i <= 15; i++) {
			String request = balancing1.dealRequest();
			System.out.println("loop: " + i + "\t" + balancing1.serviceNum + "\t" + request);
			Thread.sleep(2000);
		}
	}

	public ZooKeeper startZK() throws Exception {
		return new ZooKeeper(CONNECTSTRING, SESSION_TIMEOUT, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				try {
					serviceNodeLists = zk.getChildren(PATH, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public String dealRequest() throws Exception {

		serviceNum++;

		for (int i = serviceNum; i <= subCount; i++) {
			if (serviceNodeLists.contains(SUB_PREFIX + serviceNum)) {
				return new String(zk.getData(PATH + "/" + SUB_PREFIX, false, new Stat()));
			} else {
				serviceNum++;
			}
		}

		for (int i = 1; i <= subCount; i++) {
			if (serviceNodeLists.contains(SUB_PREFIX + i)) {
				serviceNum = i;
				return new String(zk.getData(PATH + "/" + SUB_PREFIX, false, new Stat()));
			}
		}
		return "null node...";
	}
}
