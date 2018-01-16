package zookeeper1;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.ZooDefs.Ids;

import lombok.Getter;
import lombok.Setter;
import zookeeper.zk2.ZKBase;

/**
 * 一次性通知服务
 * 
 * @author liang E-mail:gouliangchen@163.com
 * @version v1.0 创建时间:2017年12月26日 下午8:11:43
 */
public class WatchOne extends ZKBase {

	private static final Logger logger = Logger.getLogger(WatchOne.class);

	private @Setter @Getter ZooKeeper zk = null;

	public static void main(String[] args) throws Exception {

		WatchOne one = new WatchOne();
		one.setZk(one.startZK());

		if (one.getZk().exists(PATH, false) == null) {
			one.createZNode(PATH, "VVV");
			System.out.println("watchOne.znode : " + one.getZNode(PATH));
			Thread.sleep(Long.MAX_VALUE);
		} else {
			System.out.println(" i have znode ");
		}
	}

	/**
	 * 创建新的zookeeper
	 * 
	 * @return ZooKeeper
	 * @throws Exception
	 */
	public ZooKeeper startZK() throws Exception {
		return new ZooKeeper(CONNECTSTRING, SESSION_TIMEOUT, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
			}
		});
	}

	/**
	 * 关闭zookeeper
	 * 
	 * @throws Exception
	 */
	public void stopZK() throws Exception {
		if (zk != null) {
			zk.close();
		}
	}

	/**
	 * 创建新的node
	 * 
	 * @param path
	 * @param nodeValue
	 * @throws Exception
	 */
	public void createZNode(String path, String nodeValue) throws Exception {
		zk.create(PATH, nodeValue.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}

	/**
	 * 获取节点node的值，并启动通知
	 * 
	 * @param path
	 * @return String
	 * @throws Exception
	 */
	public String getZNode(String path) throws Exception {
		byte[] data = zk.getData(PATH, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				try {
					triggerValue(PATH);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, new Stat());
		return new String(data);
	}

	/**
	 * 通知业务
	 * 
	 * @param path
	 * @return String
	 * @throws Exception
	 */
	public String triggerValue(String path) throws Exception {
		byte[] data = zk.getData(PATH, false, new Stat());
		String result = new String(data);
		System.out.println("  triggerValue : " + result);
		return result;
	}
}
