package zookeeper1;

import static zookeeper.zk2.ZKBase.CONNECTSTRING;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import zookeeper.zk2.ZKBase;

/**
 * 
 * @author liang E-mail:gouliangchen@163.com
 * @version v1.0 创建时间:2017年12月26日 下午7:05:31
 */
public class HelloZK extends ZKBase {

	private static final Logger logger = Logger.getLogger(HelloZK.class);

	public static void main(String[] args) throws Exception {

		HelloZK helloZK = new HelloZK();
		ZooKeeper zk = helloZK.startZK();
		Stat stat = zk.exists(PATH, false);

		if (stat == null) {
			helloZK.createZNode(zk, PATH, "newValue");
			String zNode = helloZK.getZNode(zk, PATH);
			System.out.println("result: " + zNode);
		} else {
			System.out.println("zonde has already...");
		}

		helloZK.stopZK(zk);
	}

	/**
	 * 启动zookeeper
	 * 
	 * @return ZooKeeper
	 * @throws IOException
	 */
	public ZooKeeper startZK() throws IOException {
		// 注：此处的Watcher watcher参数也可用null，但这样输出回报一个空指针异常，故使用匿名内部类
		return new ZooKeeper(CONNECTSTRING, SESSION_TIMEOUT, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
			}
		});
	}

	/**
	 * 停止zookeeper
	 * 
	 * @throws InterruptedException
	 */
	public void stopZK(ZooKeeper zk) throws InterruptedException {
		// 判断zookeeper是否为空，防止空指针
		if (zk != null) {
			zk.close();
		}
	}

	/**
	 * 创建zookeeper节点node
	 * 
	 * @param zk
	 * @param path
	 * @throws Exception
	 */
	public void createZNode(ZooKeeper zk, String path, String nodeValue) throws Exception {

		zk.create(PATH, nodeValue.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}

	/**
	 * 获取zookeeper节点的值
	 * 
	 * @param zk
	 * @param path
	 * @return String
	 * @throws Exception
	 */
	public String getZNode(ZooKeeper zk, String path) throws Exception {

		byte[] data = zk.getData(PATH, false, new Stat());
		return new String(data);
	}
}
