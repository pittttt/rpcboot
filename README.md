# rpcboot
```
├─src
│  ├─main
│  │  ├─java
│  │  │  └─cn
│  │  │      └─pitt
│  │  │          ├─annotation
│  │  │          ├─client
│  │  │          │ └─NodeChildrenChangedWatcher.java
│  │  │          │ ├─RpcClientHandler.java
│  │  │          │ └─RpcInjectHandler.java
│  │  │          ├─common
│  │  │          ├─conf
│  │  │          │ └─RpcConfiguration
│  │  │          ├─server
│  │  │          │ ├─RpcServer.java
│  │  │          │ ├─RpcServerHandler.java
│  │  │          │ └─ServerListener.java
│  │  │          ├─test
│  │  │          ├─util
│  │  │          └─zk
│  │  │            ├─ZkClient.java
│  │  │            └─ZkServer.java
│  │  └─resources
│  │    └─application.properties
│  └─test
└─target
```
- RpcConfiguration：实例化zk客户端和RPC注册组件
- ZkServer：创建ZooKeeper实例，开启zookeeper并建立连接
- RpcServer：扫描所有的RPC服务(加了RpcService注解的Service)，将接口名称(RpcService中的interfaceName)和实现类名称(注册到容器的beanName)，作为key-value保存在本地的map中，并注册到zk上，其中interfaceName作为永久节点，服务提供者的ip和端口作为临时节点，之后创建ServerListener服务
- ServerListener：这个服务主要是用来初始化并启动Netty服务端的，这个Netty服务的ChannelHandler中除了添加了Decode和Encode外，还添加了RpcServerHandler
- RpcServerHandler：接收来自客户端的Rpc服务请求，然后通过反射调用那份存储在本地的bean，然后将执行结果响应。
- zkClient：创建ZooKeeper实例，建立zk连接
- rpcInjectHandler：扫描所有的Bean中带RpcReference的Field，然后从zk获取该interface的所有子节点，即得到提供服务的ip和端口号
