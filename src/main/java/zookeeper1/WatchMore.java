package zookeeper1;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.ZooKeeper;

import lombok.Getter;
import lombok.Setter;
import zookeeper.zk2.ZKBase;

/**
 * 多次命名通知服务
 * 
 * @author liang E-mail:gouliangchen@163.com
 * @version v1.0 创建时间:2017年12月26日 下午8:56:36
 */
public class WatchMore extends ZKBase {

	private Logger logger = Logger.getLogger(WatchMore.class);

	private @Setter @Getter ZooKeeper zk = null;
	private @Setter @Getter String lastValue = "";

	public static void main(String[] args) throws Exception {

		WatchMore more = new WatchMore();
		more.setZk(more.startZK());

		if (more.getZk().exists(PATH, false) == null) {
			String initValue = "0000";
			more.setLastValue(initValue);
			more.createZNode(PATH, initValue);
			System.out.println("  watchMore.znode > : " + more.getZNode(PATH));
			Thread.sleep(Long.MAX_VALUE);
		} else {
			System.out.println(" i have znode ");
		}

	}

	/**
	 * 启动zookeeper
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
	 * 停止zookeeper
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
	 * 获取node的值，并调用通知方法
	 * 
	 * @param path
	 * @return
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
	 * 通知实现
	 * 
	 * @param path
	 * @return boolean
	 * @throws Exception
	 */
	public boolean triggerValue(String path) throws Exception {
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
		String newValue = new String(data);

		if (lastValue.equals(newValue)) {
			System.out.println("there is no change...");
			return false;
		} else {
			System.out.println("lastValue : " + lastValue + "\t newValue : " + newValue);
			this.lastValue = newValue;
			return true;
		}
	}
}
