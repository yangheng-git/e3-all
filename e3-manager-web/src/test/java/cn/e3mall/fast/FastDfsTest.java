package cn.e3mall.fast;

/**
 上传图片
上传步骤
1、加载配置文件，配置文件中的内容就是tracker服务的地址。
配置文件内容：tracker_server=192.168.25.133:22122
2、创建一个TrackerClient对象。直接new一个。
3、使用TrackerClient对象创建连接，获得一个TrackerServer对象。
4、创建一个StorageServer的引用，值为null
5、创建一个StorageClient对象，需要两个参数TrackerServer对象、StorageServer的引用

 */
public class FastDfsTest {

	/*@Test
	public void testFastDfs()throws Exception{
		//1加载配置文件，配置文件中的内容就是tracker服务的地址，需要是绝对路径，
		ClientGlobal.init("E:/apache-workspaces-Eclipse-taotao2016/e3-manager-web/src/main/resources/conf/client.conf");
		
		//2创建一个TrackerClient对象，直接new一个
		TrackerClient trackerClient=new TrackerClient();
		
		//3使用TrackerClient对象创建连接，获得一个TrackerServer对象
		TrackerServer tarackerServer =trackerClient.getConnection();
		
		//4创建一个StorageServer的引用，值为null
		StorageServer storageServer =null;
		
		//5创建一个StorageClient 对象 需要两个参数TrackerServer对象，StorageServer对象的引用。
		StorageClient storageClient =new StorageClient(tarackerServer,storageServer);
		
		//6使用StorageClient对象上传图片，三个参数 ，路径 ，扩展名，文件附加属性键值对.
		//路径，win8以上复制的路径可能有问题，
		//扩展名，没有点
		//文件附加属性键值对 ，可以为null
		
		
		String[] strings =storageClient.upload_file("‪C:/Users/yang/Desktop/mantenghuawenmodianshiliangbeijing_3924704.jpg", "jpg", null);
		
		//7 返回数组，包含组名和图片的路径
		for (String string : strings) {
			System.out.println(string);
		}
		
		
	}
	
	@Test
	public void testUtilsFastDFs()throws Exception{
		FastDFSClient fastDFSClient =new FastDFSClient("E:/apache-workspaces-Eclipse-taotao2016/e3-manager-web/src/main/resources/conf/client.conf");
	String string=	fastDFSClient.uploadFile("F:/a.jpg");
		System.out.println(string);
		
	}*/
}
